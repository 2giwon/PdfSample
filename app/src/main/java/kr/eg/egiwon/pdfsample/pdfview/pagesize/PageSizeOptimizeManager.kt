package kr.eg.egiwon.pdfsample.pdfview.pagesize

import kr.eg.egiwon.pdfsample.util.Size

interface PageSizeOptimizeManager {
    fun getFitPageWidth(): Size<Float>

    fun getFitPageHeight(): Size<Float>

    fun calcMaxSize()

    fun calculateCurrentViewSizeFitDocument(pageSize: Size<Int>): Size<Float>
}