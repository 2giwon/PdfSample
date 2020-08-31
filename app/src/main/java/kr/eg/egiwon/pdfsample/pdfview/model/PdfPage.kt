package kr.eg.egiwon.pdfsample.pdfview.model

import android.graphics.Bitmap
import kr.eg.egiwon.pdfsample.base.BaseIdentifier

data class PdfPage(
    val identifier: Int,
    val pageBitmap: Bitmap,
    val pageNum: Int,
    val width: Int,
    val height: Int
) : BaseIdentifier() {
    override val id: Any
        get() = identifier
}