package kr.eg.egiwon.pdfsample.pdfview.loader

import android.graphics.RectF
import io.reactivex.Flowable
import kr.eg.egiwon.pdfsample.pdfview.model.GridSize
import kr.eg.egiwon.pdfsample.pdfview.model.Holder
import kr.eg.egiwon.pdfsample.pdfview.model.RenderingRange
import kr.eg.egiwon.pdfsample.pdfview.render.RenderTaskManager
import kr.eg.egiwon.pdfsample.pdfview.render.model.RenderTask
import kr.eg.egiwon.pdfsample.pdfview.setup.PdfSetupManager
import kr.eg.egiwon.pdfsample.util.DefaultSetting
import kr.eg.egiwon.pdfsample.util.Size
import org.reactivestreams.Subscriber
import java.util.*
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class PageLoader(
    private val defaultSetting: DefaultSetting,
    private val pageSetup: PdfSetupManager,
    private val renderer: RenderTaskManager
) : PageLoadable {

    private var xOffset = 0f
    private var yOffset = 0f

    private var pageRelativePartWidth = 0f
    private var pageRelativePartHeight = 0f

    private var partRenderWidth = 0f
    private var partRenderHeight = 0f

    private var cacheOrder = 0

    override fun loadPages(viewSize: Size<Int>): Flowable<RenderTask> {
        return Flowable.unsafeCreate<RenderTask> { subscriber ->
            cacheOrder = 1
            var partCount = 0
            xOffset = -0f
            yOffset = -0f

            val preloadOffset = defaultSetting.defaultOffset
            val firstXOffset = -xOffset + preloadOffset
            val lastXOffset = -xOffset - viewSize.width - preloadOffset
            val firstYOffset = -yOffset + preloadOffset
            val lastYOffset = -yOffset - viewSize.height - preloadOffset

            val rangeList: List<RenderingRange> = getRenderingRange(
                firstXOffset,
                firstYOffset,
                lastXOffset,
                lastYOffset
            )

            rangeList.forEach {
                pageRelativePartWidth = calculateRelativePartWidth(it.gridSize)
                pageRelativePartHeight = calculateRelativePartHeight(it.gridSize)
                partRenderWidth = calculateRenderWidth(it.gridSize)
                partRenderHeight = calculateRenderHeight(it.gridSize)

                partCount += loadPage(
                    it.page,
                    it.leftTop.row,
                    it.rightBottom.row,
                    it.leftTop.col,
                    it.rightBottom.col,
                    defaultSetting.defaultCacheSize - partCount,
                    subscriber
                )
                if (partCount >= defaultSetting.defaultCacheSize) return@forEach
            }

            subscriber.onComplete()
        }
    }

    private fun loadPage(
        page: Int,
        firstRow: Int,
        lastRow: Int,
        firstCol: Int,
        lastCol: Int,
        partsLoadable: Int,
        subscriber: Subscriber<in RenderTask>
    ): Int {

        var loaded = 0
        for (row in firstRow..lastRow) {
            for (col in firstCol..lastCol) {
                if (loadPart(page, row, col, pageRelativePartWidth, pageRelativePartHeight, subscriber)) {
                    loaded++
                }
                if (loaded >= partsLoadable) {
                    return loaded
                }
            }
        }

        return loaded
    }

    private fun loadPart(
        page: Int,
        row: Int,
        col: Int,
        pageRelativePartWidth: Float,
        pageRelativePartHeight: Float,
        subscriber: Subscriber<in RenderTask>
    ): Boolean {

        val relX = pageRelativePartWidth * col
        val relY = pageRelativePartHeight * row
        var partWidth = pageRelativePartWidth
        var partHeight = pageRelativePartHeight

        var renderWidth = partRenderWidth
        var renderHeight = partRenderHeight
        if (relX + partWidth > 1) {
            partWidth = 1 - relX
        }

        if (relY + partHeight > 1) {
            partHeight = 1 - relY
        }
        renderWidth *= partWidth
        renderHeight *= partHeight
        val boundRect = RectF(relX, relY, relX + partWidth, relY + partHeight)

        if (renderWidth > 0 && renderHeight > 0) {
            subscriber.onNext(renderer.createRenderTask(
                page, renderWidth, renderHeight, boundRect, cacheOrder, false
            ))
            cacheOrder++
            return true
        }

        return false
    }

    private fun getRenderingRange(
        firstXOffset: Float,
        firstYOffset: Float,
        lastXOffset: Float,
        lastYOffset: Float
    ): List<RenderingRange> {

        val fixedFirstXOffset = -min(firstXOffset, 0f)
        val fixedFirstYOffset = -min(firstYOffset, 0f)

        val fixedLastXOffset = -min(lastXOffset, 0f)
        val fixedLastYOffset = -min(lastYOffset, 0f)

        val firstPage = pageSetup.getPageAtOffset(fixedFirstYOffset)
        val lastPage = pageSetup.getPageAtOffset(fixedLastYOffset)
        val pageCount = lastPage - firstPage + 1

        val renderingRanges = LinkedList<RenderingRange>()

        for (page in firstPage..lastPage) {
            val tempRange = RenderingRange(page = page)

            val pageFirstXOffset: Float
            val pageLastXOffset: Float
            val pageLastYOffset: Float

            if (page == firstPage) {
                pageFirstXOffset = fixedFirstXOffset
                if (pageCount == 1) {
                    pageLastXOffset = fixedLastXOffset
                    pageLastYOffset = fixedLastYOffset
                } else {
                    val pageOffset = pageSetup.getPageOffset(page)
                    val pageSize: Size<Float> = pageSetup.getScaledPageSize(page)
                    pageLastXOffset = fixedLastXOffset
                    pageLastYOffset = pageOffset + pageSize.height
                }
            } else if (page == lastPage) {
                pageSetup.getPageOffset(page)

                pageFirstXOffset = fixedFirstXOffset

                pageLastXOffset = fixedLastXOffset
                pageLastYOffset = fixedLastYOffset
            } else {
                val pageOffset = pageSetup.getPageOffset(page)
                val pageSize: Size<Float> = pageSetup.getScaledPageSize(page)

                pageFirstXOffset = fixedFirstXOffset

                pageLastXOffset = fixedLastXOffset
                pageLastYOffset = pageOffset + pageSize.height
            }

            var range = RenderingRange(
                gridSize = getPageColsRows(tempRange.page),
                page = tempRange.page
            )
            val scaledSize = pageSetup.getScaledPageSize(range.page)
            val colWidth = scaledSize.width / range.gridSize.cols
            val rowHeight = scaledSize.height / range.gridSize.rows

            val secondaryOffset = pageSetup.getSecondaryOffset(page)

            range = range.makeRenderingRange(
                pageFirstXOffset,
                range.page,
                rowHeight,
                secondaryOffset,
                colWidth,
                pageLastYOffset,
                pageLastXOffset
            )

            renderingRanges.add(range)
        }

        return renderingRanges
    }

    private fun RenderingRange.makeRenderingRange(
        pageFirstXOffset: Float,
        page: Int,
        rowHeight: Float,
        secondaryOffset: Float,
        colWidth: Float,
        pageLastYOffset: Float,
        pageLastXOffset: Float
    ): RenderingRange {
        return RenderingRange(
            gridSize = this.gridSize,
            page = this.page,
            leftTop = Holder(
                row = floor(
                    abs(pageFirstXOffset - pageSetup.getPageOffset(page)) / rowHeight
                ).toInt(),
                col = floor(
                    max(pageFirstXOffset - secondaryOffset, 0f) / colWidth
                ).toInt()
            ),
            rightBottom = Holder(
                row = ceil(
                    abs(pageLastYOffset - pageSetup.getPageOffset(page)) / rowHeight
                ).toInt(),
                col = floor(
                    max(pageLastXOffset - secondaryOffset, 0f) / colWidth
                ).toInt()
            )
        )
    }

    private fun getPageColsRows(index: Int): GridSize {
        val size = pageSetup.getPageSize(index)
        val ratioX = 1f / size.width
        val ratioY = 1f / size.height
        val partWidth = defaultSetting.defaultPartSize * ratioX / pageSetup.getPageZoom()
        val partHeight = defaultSetting.defaultPartSize * ratioY / pageSetup.getPageZoom()
        return GridSize(
            cols = ceil(1f / partWidth).toInt(),
            rows = ceil(1f / partHeight).toInt()
        )
    }

    private fun calculateRelativePartWidth(gridSize: GridSize): Float = 1f / gridSize.cols

    private fun calculateRelativePartHeight(gridSize: GridSize): Float = 1f / gridSize.rows

    private fun calculateRenderWidth(gridSize: GridSize): Float =
        defaultSetting.defaultPartSize / calculateRelativePartWidth(gridSize)

    private fun calculateRenderHeight(gridSize: GridSize): Float =
        defaultSetting.defaultPartSize / calculateRelativePartHeight(gridSize)
}