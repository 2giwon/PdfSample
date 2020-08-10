package kr.eg.egiwon.pdfsample.pdfcore

import android.content.Context
import android.graphics.Bitmap
import android.os.ParcelFileDescriptor
import android.util.Log
import com.shockwave.pdfium.PdfDocument
import com.shockwave.pdfium.PdfiumCore
import javax.inject.Inject

class PdfCore @Inject constructor(private val context: Context) : PdfReadable {

    private val pdfCore: PdfiumCore = PdfiumCore(context)

    override fun loadPdfBitmap(fd: ParcelFileDescriptor, pageNum: Int): Bitmap? {

        try {
            val pdfDocument = pdfCore.newDocument(fd)
            pdfCore.openPage(pdfDocument, pageNum)

            val width = pdfCore.getPageWidthPoint(pdfDocument, pageNum)
            val height = pdfCore.getPageHeightPoint(pdfDocument, pageNum)

            val documentBitmap =
                Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            pdfCore.renderPageBitmap(
                pdfDocument,
                documentBitmap,
                pageNum,
                0,
                0,
                width,
                height
            )

            printInfo(pdfCore, pdfDocument)

            pdfCore.closeDocument(pdfDocument)

            return documentBitmap
        } catch (throwable: Throwable) {

        }

        return null
    }

    private fun printInfo(core: PdfiumCore, doc: PdfDocument) {
        val meta: PdfDocument.Meta = core.getDocumentMeta(doc)
        Log.e(TAG, "title = ${meta.title}")
        Log.e(TAG, "author = ${meta.author}")
        Log.e(TAG, "subject = ${meta.subject}")
        Log.e(TAG, "keywords = ${meta.keywords}")
        Log.e(TAG, "creator = ${meta.creator}")
        Log.e(TAG, "producer = ${meta.producer}")
        Log.e(TAG, "creationDate = ${meta.creationDate}")
        Log.e(TAG, "modDate = ${meta.modDate}")
    }

    companion object {
        private const val TAG = "PdfCore"
    }
}