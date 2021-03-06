package kr.eg.egiwon.pdfsample.pdfcore

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.ParcelFileDescriptor
import android.util.Log
import com.shockwave.pdfium.PdfDocument
import com.shockwave.pdfium.PdfiumCore
import kr.eg.egiwon.pdfsample.util.Size
import javax.inject.Inject

class PdfCore @Inject constructor(context: Context) : PdfCoreAction {

    private val pdfCore: PdfiumCore = PdfiumCore(context)
    private lateinit var pdfDocument: PdfDocument

    override fun loadPdfBitmap(pageNum: Int): Bitmap? {
        try {
            pdfCore.openPage(pdfDocument, pageNum)
            val width = pdfCore.getPageWidth(pdfDocument, pageNum)
            val height = pdfCore.getPageHeight(pdfDocument, pageNum)

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

            return documentBitmap
        } catch (ex: Exception) {
            throw Exception(ex.message)
        }
    }

    override fun getPageCount(): Int = runCatching {
        pdfCore.getPageCount(pdfDocument)
    }.getOrDefault(0)

    override fun getPageSize(pageNum: Int): Size<Int> {
        val size = pdfCore.getPageSize(pdfDocument, pageNum)
        return Size(size.width, size.height)
    }

    override fun openPage(page: Int) {
        pdfCore.openPage(pdfDocument, page)
    }

    override fun renderPageBitmap(
        page: Int,
        bitmap: Bitmap,
        bounds: Rect,
        isAnnotationRender: Boolean
    ): Bitmap {
        pdfCore.renderPageBitmap(
            pdfDocument,
            bitmap,
            page,
            bounds.left,
            bounds.top,
            bounds.width(),
            bounds.height(),
            isAnnotationRender
        )

        return bitmap
    }

    override fun openPdfDocument(fd: ParcelFileDescriptor): Boolean {
        runCatching {
            pdfDocument = pdfCore.newDocument(fd, null)

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

        private var instance: PdfCore? = null

        fun getInstance(context: Context): PdfCore =
            instance ?: PdfCore(context).apply {
                instance = this
            }

    }
}