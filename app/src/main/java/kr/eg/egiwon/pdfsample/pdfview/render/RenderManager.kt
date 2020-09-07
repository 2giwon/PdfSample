package kr.eg.egiwon.pdfsample.pdfview.render

import android.graphics.RectF
import kr.eg.egiwon.pdfsample.pdfview.render.model.PagePart
import kr.eg.egiwon.pdfsample.pdfview.render.model.RenderTask

interface RenderManager {
    fun render(task: RenderTask): PagePart

    fun createRenderTask(
        page: Int,
        width: Float,
        height: Float,
        bounds: RectF,
        cacheOrder: Int,
        annotRender: Boolean
    ): RenderTask
}