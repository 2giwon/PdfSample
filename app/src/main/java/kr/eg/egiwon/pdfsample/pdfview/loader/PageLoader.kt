package kr.eg.egiwon.pdfsample.pdfview.loader

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.os.SystemClock
import android.util.SparseBooleanArray
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import kr.eg.egiwon.pdfsample.pdfcore.PdfCoreAction
import kr.eg.egiwon.pdfsample.pdfview.model.GridSize
import kr.eg.egiwon.pdfsample.pdfview.model.Holder
import kr.eg.egiwon.pdfsample.pdfview.model.RenderingRange
import kr.eg.egiwon.pdfsample.pdfview.render.model.PagePart
import kr.eg.egiwon.pdfsample.pdfview.render.model.RenderTask
import kr.eg.egiwon.pdfsample.pdfview.setup.PdfSetupManager
import kr.eg.egiwon.pdfsample.util.DefaultSetting
import kr.eg.egiwon.pdfsample.util.PdfLog
import kr.eg.egiwon.pdfsample.util.Size
import java.util.*
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

class PageLoader(
    private val defaultSetting: DefaultSetting,
    private val pageSetup: PdfSetupManager,
    private val pdfCoreAction: PdfCoreAction
) : PageLoadable {

    private var xOffset = 0f
    private var yOffset = 0f

    private var pageRelativePartWidth = 0f
    private var pageRelativePartHeight = 0f

    private var partRenderWidth = 0f
    private var partRenderHeight = 0f

    private var cacheOrder = 0

    private var startTime = 0L
    private var endTime = 0L

    private val pageParts = mutableListOf<PagePart>()

    private val lock = Any()

    private val openedPages = SparseBooleanArray()

    private val renderMatrix = Matrix()
    private val renderBounds = RectF()
    private val roundedBounds = Rect()

    override fun loadPages(viewSize: Size<Int>): Observable<PagePart> {
        return Observable.create { subscriber ->
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

            for (i in rangeList.indices) {
                pageRelativePartWidth = calculateRelativePartWidth(rangeList[i].gridSize)
                pageRelativePartHeight = calculateRelativePartHeight(rangeList[i].gridSize)
                partRenderWidth = calculateRenderWidth()
                partRenderHeight = calculateRenderHeight()

                partCount += loadPage(
                    rangeList[i].page,
                    rangeList[i].leftTop.row,
                    rangeList[i].rightBottom.row,
                    rangeList[i].leftTop.col,
                    rangeList[i].rightBottom.col,
                    defaultSetting.defaultCacheSize - partCount,
                    subscriber
                )
                if (partCount >= defaultSetting.defaultCacheSize) continue
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
        subscriber: ObservableEmitter<PagePart>
    ): Int {

        var loaded = 0
        for (row in firstRow..lastRow) {
            for (col in firstCol..lastCol) {
                if (loadPart(
                        page,
                        row,
                        col,
                        pageRelativePartWidth,
                        pageRelativePartHeight,
                        subscriber
                    )
                ) {
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
        subscriber: ObservableEmitter<PagePart>
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

            val renderTask = createRenderTask(
                page, renderWidth, renderHeight, boundRect, cacheOrder,
                annotRender = true,
                isThumbnail = false
            )

            makePagePart(renderTask)?.let(subscriber::onNext)

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
            val pageFirstYOffset: Float
            val pageLastXOffset: Float
            val pageLastYOffset: Float

            if (page == firstPage) {
                pageFirstXOffset = fixedFirstXOffset
                pageFirstYOffset = fixedFirstYOffset
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
                val pageOffset = pageSetup.getPageOffset(page)

                pageFirstXOffset = fixedFirstXOffset
                pageFirstYOffset = pageOffset

                pageLastXOffset = fixedLastXOffset
                pageLastYOffset = fixedLastYOffset
            } else {
                val pageOffset = pageSetup.getPageOffset(page)
                val pageSize: Size<Float> = pageSetup.getScaledPageSize(page)

                pageFirstXOffset = fixedFirstXOffset
                pageFirstYOffset = pageOffset

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
                pageFirstYOffset,
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

    private fun makePagePart(task: RenderTask): PagePart? {
        startTime = SystemClock.elapsedRealtime()
        synchronized(lock) {
            runCatching {
                pdfCoreAction.openPage(task.page)
                endTime = SystemClock.elapsedRealtime()
                PdfLog.e("Leegiwon", "openPage after : ${endTime - startTime} ms")
                startTime = SystemClock.elapsedRealtime()
            }
                .onSuccess {
                    openedPages.put(task.page, true)
                }
                .onFailure {
                    openedPages.put(task.page, false)
                }

            val w: Int = round(task.width).toInt()
            val h: Int = round(task.height).toInt()

            if (w == 0 || h == 0 || hasErrorPage(task.page)) {
                return null
            }

            runCatching {
                Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            }.onSuccess {
                renderMatrix.calculateBounds(w, h, task.bounds)

                pdfCoreAction.renderPageBitmap(
                    task.page,
                    it,
                    roundedBounds,
                    task.isAnnotationRendering
                )
                endTime = SystemClock.elapsedRealtime()
                PdfLog.e("Leegiwon", "loadPart renderBitmap : ${endTime - startTime} ms")
                return PagePart(task.page, it, task.bounds, task.cacheOrder, task.thumbnail)
            }.onFailure {
                return null
            }
        }

        return null
    }

    private fun Matrix.calculateBounds(width: Int, height: Int, pageSliceBounds: RectF) {
        reset()
        postTranslate(-pageSliceBounds.left * width, -pageSliceBounds.top * height)
        postScale(1 / pageSliceBounds.width(), 1 / pageSliceBounds.height())

        renderBounds.set(0f, 0f, width.toFloat(), height.toFloat())
        mapRect(renderBounds)
        renderBounds.round(roundedBounds)
    }

    private fun hasErrorPage(page: Int): Boolean =
        !openedPages.get(page, false)

//    private fun loadThumbnail(page: Int) {
//        val pageSize = pageSetup.getPageSize(page)
//        val thumbnailWidth = pageSize.width * THUMBNAIL_RATIO
//        val thumbnailHeight = pageSize.height * THUMBNAIL_RATIO
//        if ()
//    }

    private fun RenderingRange.makeRenderingRange(
        pageFirstXOffset: Float,
        pageFirstYOffset: Float,
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
                    abs(pageFirstYOffset - pageSetup.getPageOffset(page)) / rowHeight
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

    private fun calculateRenderWidth(): Float =
        defaultSetting.defaultPartSize / pageRelativePartWidth

    private fun calculateRenderHeight(): Float =
        defaultSetting.defaultPartSize / pageRelativePartHeight

    private fun createRenderTask(
        page: Int,
        width: Float,
        height: Float,
        bounds: RectF,
        cacheOrder: Int,
        annotRender: Boolean,
        isThumbnail: Boolean
    ): RenderTask = RenderTask(
        page = page,
        width = width,
        height = height,
        bounds = bounds,
        cacheOrder = cacheOrder,
        isAnnotationRendering = annotRender,
        thumbnail = isThumbnail
    )
}