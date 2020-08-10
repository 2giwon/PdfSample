package kr.eg.egiwon.pdfsample.pdfview

import android.graphics.Bitmap

interface PdfViewAction {
    fun loadPdfPage(block: () -> Bitmap)
}