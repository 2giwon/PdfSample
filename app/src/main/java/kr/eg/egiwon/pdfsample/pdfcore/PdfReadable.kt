package kr.eg.egiwon.pdfsample.pdfcore

import android.graphics.Bitmap
import android.os.ParcelFileDescriptor

interface PdfReadable {
    fun loadPdfBitmap(fd: ParcelFileDescriptor, pageNum: Int): Bitmap?
}