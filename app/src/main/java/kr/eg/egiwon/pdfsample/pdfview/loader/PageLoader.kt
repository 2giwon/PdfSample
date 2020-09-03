package kr.eg.egiwon.pdfsample.pdfview.loader

import kr.eg.egiwon.pdfsample.pdfview.model.GridSize
import kr.eg.egiwon.pdfsample.pdfview.model.Holder
import kr.eg.egiwon.pdfsample.pdfview.model.RenderingRange
import kr.eg.egiwon.pdfsample.pdfview.setup.PdfSetupManager
import kr.eg.egiwon.pdfsample.util.DefaultSetting
import kr.eg.egiwon.pdfsample.util.Size
import java.util.*
import kotlin.math.*

class PageLoader(
    private val defaultSetting: DefaultSetting,
    private val pageSetup: PdfSetupManager
) : PageLoadable {

    private var xOffset = 0f
    private var yOffset = 0f

    override fun loadPages(viewSize: Size<Int>) {
        var partCount = 0
        xOffset = -0f
        yOffset = -0f

        val preloadOffset = defaultSetting.defaultOffset
        val firstXOffset = -xOffset + preloadOffset
        val lastXOffset = -xOffset - viewSize.width - preloadOffset
        val firstYOffset = -yOffset + preloadOffset
        val lastYOffset = -yOffset - viewSize.height - preloadOffset

        val rangeList: List<RenderingRange> =
            getRenderingRange(
                firstXOffset,
                firstYOffset,
                lastXOffset,
                lastYOffset
            )
    }

    private fun getRenderingRange(
        firstXOffset: Float,
        firstYOffset: Float,
        lastXOffset: Float,
        lastYOffset: Float
    ): List<RenderingRange> {

        val fixedFirstXOffset = -max(firstXOffset, 0f)
        val fixedFirstYOffset = -max(firstYOffset, 0f)

        val fixedLastXOffset = -max(lastXOffset, 0f)
        val fixedLastYOffset = -max(lastYOffset, 0f)

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

            range = makeRenderingRange(
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

    private fun makeRenderingRange(
        pageFirstXOffset: Float,
        page: Int,
        rowHeight: Float,
        secondaryOffset: Float,
        colWidth: Float,
        pageLastYOffset: Float,
        pageLastXOffset: Float
    ): RenderingRange {
        return RenderingRange(
            leftTop = Holder(
                row = floor(
                    abs(pageFirstXOffset - pageSetup.getPageOffset(page)) / rowHeight
                ).toInt(),
                col = floor(
                    min(pageFirstXOffset - secondaryOffset, 0f) / colWidth
                ).toInt()
            ),
            rightBottom = Holder(
                row = ceil(
                    abs(pageLastYOffset - pageSetup.getPageOffset(page)) / rowHeight
                ).toInt(),
                col = floor(
                    min(pageLastXOffset - secondaryOffset, 0f) / colWidth
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

}