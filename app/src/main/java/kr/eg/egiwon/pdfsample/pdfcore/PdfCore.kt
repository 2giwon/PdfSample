package kr.eg.egiwon.pdfsample.pdfcore

import android.content.Context
import android.graphics.Bitmap
import android.os.ParcelFileDescriptor
import android.util.Log
import com.shockwave.pdfium.PdfDocument
import com.shockwave.pdfium.PdfiumCore
import javax.inject.Inject

class PdfCore @Inject constructor(context: Context) : PdfReadable {

    private val pdfCore: PdfiumCore = PdfiumCore(context)
    private lateinit var pdfDocument: PdfDocument


    override fun loadPdfBitmap(pageNum: Int): Bitmap? {
        try {
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
                height,
                true
            )

            return documentBitmap
        } catch (ex: Exception) {
            throw Exception(ex.message)
        }
    }

    override fun getPageCount(): Int = pdfCore.getPageCount(pdfDocument)

    override fun openPdfDocument(fd: ParcelFileDescriptor): Boolean {
        runCatching {
            pdfDocument = pdfCore.newDocument(fd)
        }.onSuccess {
            printInfo(pdfCore, pdfDocument)
            return true
        }.onFailure {
            return false
        }
        return false
    }

    override fun closeDocument() {
        try {
            pdfCore.closeDocument(pdfDocument)
        } catch (ignore: Exception) {
        }
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