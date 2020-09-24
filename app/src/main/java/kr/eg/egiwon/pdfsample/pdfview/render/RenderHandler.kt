package kr.eg.egiwon.pdfsample.pdfview.render

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.os.Handler
import android.os.Message
import android.util.SparseBooleanArray
import kr.eg.egiwon.pdfsample.pdfcore.PdfReadable
import kr.eg.egiwon.pdfsample.pdfview.render.model.PagePart
import kr.eg.egiwon.pdfsample.pdfview.render.model.RenderTask
import javax.inject.Inject
import kotlin.math.round

interface RenderHandlerActionListener {
    fun onBitmapRendered(part: PagePart)
}

class RenderHandler @Inject constructor(
    private val pdfReadable: PdfReadable
) : Handler() {

    private val lock = Any()

    private val openedPages = SparseBooleanArray()

    private val renderMatrix = Matrix()
    private val renderBounds = RectF()
    private val roundedBounds = Rect()

    private var listener: RenderHandlerActionListener? = null

    override fun handleMessage(msg: Message) {
        val task = msg.obj as RenderTask
        try {
            val part = makePagePart(task)

            if (part != null) {
                listener?.onBitmapRendered(part)
            }
        } catch (ex: Exception) {

        }
    }

    private fun makePagePart(task: RenderTask): PagePart? {
        synchronized(lock) {
            runCatching { pdfReadable.openPage(task.page) }
                .onSuccess {
                    openedPages.put(task.page, true)
                }
                .onFailure {
                    openedPages.put(task.page, false)
                }

            val w: Int = round(task.width).toInt()
            val h: Int = round(task.height).toInt()

            if (w == 0 || h == 0 || hasErrorPage(task.page)) {
                return null
            }

            runCatching {
                Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            }.onSuccess {
                renderMatrix.calculateBounds(w, h, task.bounds)
                pdfReadable.renderPageBitmap(
                    task.page,
                    it,
                    roundedBounds,
                    task.isAnnotationRendering
                )

                return PagePart(task.page, it, task.bounds, task.cacheOrder, task.thumbnail)
            }.onFailure {
                return null
            }
        }

        return null
    }

    private fun Matrix.calculateBounds(width: Int, height: Int, pageSliceBounds: RectF) {
        reset()
        postTranslate(-pageSliceBounds.left * width, -pageSliceBounds.top * height)
        postScale(1 / pageSliceBounds.width(), 1 / pageSliceBounds.height())

        renderBounds.set(0f, 0f, width.toFloat(), height.toFloat())
        mapRect(renderBounds)
        renderBounds.round(roundedBounds)
    }

    private fun hasErrorPage(page: Int): Boolean =
        !openedPages.get(page, false)

    fun setActionListener(listener: RenderHandlerActionListener) {
        this.listener = listener
    }

    companion object {
        private const val MSG_RENDER_TASK = 1

        fun RenderHandler.sendTask(task: RenderTask) {
            val msg: Message = obtainMessage(MSG_RENDER_TASK, task)
            sendMessage(msg)
        }
    }
}