package kr.eg.egiwon.pdfsample.pdfcore

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.ParcelFileDescriptor
import kr.eg.egiwon.pdfsample.util.Size

interface PdfReadable {

    fun openPdfDocument(fd: ParcelFileDescriptor): Boolean

    fun closeDocument()

    fun loadPdfBitmap(pageNum: Int): Bitmap?

    fun getPageCount(): Int

    fun getPageSize(pageNum: Int): Size<Int>

    fun openPage(page: Int)

    fun renderPageBitmap(
        page: Int,
        bitmap: Bitmap,
        bounds: Rect,
        isAnnotationRender: Boolean
    ): Bitmap
}