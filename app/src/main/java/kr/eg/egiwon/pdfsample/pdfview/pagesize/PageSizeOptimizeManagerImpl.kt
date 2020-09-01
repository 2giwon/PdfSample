package kr.eg.egiwon.pdfsample.pdfview.pagesize

import kr.eg.egiwon.pdfsample.util.Size
import kotlin.math.floor

class PageSizeOptimizeManagerImpl(
    private val maxPageSize: Size<Int>,
    private val viewSize: Size<Int>
) : PageSizeOptimizeManager {

    private var fitWidth = Size(0f, 0f)
    private var fitHeight = Size(0f, 0f)
    private var widthRatio = 0f

    override fun getFitPageWidth(): Size<Float> {
        return fitWidth
    }


    override fun getFitPageHeight(): Size<Float> {
        return fitHeight
    }

    override fun calcMaxSize() {
        fitWidth = calcFitWidth(maxPageSize, viewSize.width.toFloat())
        widthRatio = fitWidth.width / fitWidth.height
        fitHeight = calcFitWidth(maxPageSize, maxPageSize.width * widthRatio)
    }

    override fun calculateCurrentViewSizeFitDocument(pageSize: Size<Int>): Size<Float> {
        if (pageSize.width <= 0 || pageSize.height <= 0) {
            return Size(0f, 0f)
        }

        val maxWidth = viewSize.width
        return calcFitWidth(pageSize, maxWidth.toFloat())
    }

    private fun calcFitWidth(pageSize: Size<Int>, maxWidth: Float): Size<Float> {
        val ratio: Float = pageSize.width.toFloat() / pageSize.height.toFloat()
        val height = floor(maxWidth / ratio)
        return Size(maxWidth, height)
    }

}