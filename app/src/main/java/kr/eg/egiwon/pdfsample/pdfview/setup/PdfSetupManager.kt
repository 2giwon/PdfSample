package kr.eg.egiwon.pdfsample.pdfview.setup

import kr.eg.egiwon.pdfsample.util.Size

interface PdfSetupManager {

    fun pageSetup(viewSize: Size<Int>, getPageCount: (Int) -> Unit, setupComplete: () -> Unit)

}