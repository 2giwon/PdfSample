package kr.eg.egiwon.pdfsample.pdfview.setup

import kr.eg.egiwon.pdfsample.util.Size

interface PdfSetupManager {

    fun pageSetup(viewSize: Size<Int>, onPageCount: (Int) -> Unit, setupComplete: () -> Unit)

    fun getPageAtOffset(offset: Float): Int

    fun getCurrentDocumentPageCount(): Int

    fun getPageZoom(): Float

    fun getPageOffset(pageIndex: Int): Float

    fun getScaledPageSize(pageIndex: Int): Size<Float>

    fun getPageSize(pageIndex: Int): Size<Float>

    fun getSecondaryOffset(pageIndex: Int): Float
}