package kr.eg.egiwon.pdfsample.ext

import android.graphics.Bitmap
import androidx.databinding.BindingAdapter
import kr.eg.egiwon.pdfsample.pdfview.PdfView
import kr.eg.egiwon.pdfsample.pdfview.PdfViewAction

@BindingAdapter("loadPageBitmap")
fun PdfView.loadPageBitmap(bitmap: Bitmap) {
    (this as? PdfViewAction)?.loadPdfPage { bitmap }
}