package kr.eg.egiwon.pdfsample.pdfview.setup

import io.reactivex.Single
import kr.eg.egiwon.pdfsample.util.Size

interface PdfSetupManager {

    fun pageSetup(viewSize: Size<Int>): Single<Unit>

    fun getPageCountFromLoadedDocument(): Int

    fun getPageAtOffset(offset: Float): Int

    fun getCurrentDocumentPageCount(): Int

    fun getPageZoom(): Float

    fun getPageOffset(pageIndex: Int): Float

    fun getScaledPageSize(pageIndex: Int): Size<Float>

    fun getPageSize(pageIndex: Int): Size<Float>

    fun getSecondaryOffset(pageIndex: Int): Float

    fun getFitWidth(): Float

    fun getFitHeight(): Float
}