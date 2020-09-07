package kr.eg.egiwon.pdfsample.pdfview.render

import android.graphics.RectF
import android.util.SparseBooleanArray
import kr.eg.egiwon.pdfsample.pdfcore.PdfReadable
import kr.eg.egiwon.pdfsample.pdfview.render.model.PagePart
import kr.eg.egiwon.pdfsample.pdfview.render.model.RenderTask
import kotlin.math.round

class Renderer(private val pdfReadable: PdfReadable) : RenderManager {

    private val lock = Any()

    private val openedPages = SparseBooleanArray()

    override fun render(task: RenderTask): PagePart {
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
                throw Error(Throwable())
            }

            runCatching {

            }
        }
    }

    private fun hasErrorPage(page: Int): Boolean =
        openedPages.get(page, false)

    override fun createRenderTask(
        page: Int,
        width: Float,
        height: Float,
        bounds: RectF,
        cacheOrder: Int,
        annotRender: Boolean
    ): RenderTask = RenderTask(
        page = page,
        width = width,
        height = height,
        bounds = bounds,
        cacheOrder = cacheOrder,
        isAnnotationRendering = annotRender
    )


}