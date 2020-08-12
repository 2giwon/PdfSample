package kr.eg.egiwon.pdfsample.pdfcore

import android.graphics.Bitmap
import android.os.ParcelFileDescriptor

interface PdfReadable {

    fun openPdfDocument(fd: ParcelFileDescriptor): Boolean

    fun closeDocument()

    fun loadPdfBitmap(pageNum: Int): Bitmap?

    fun getPageCount(): Int
}