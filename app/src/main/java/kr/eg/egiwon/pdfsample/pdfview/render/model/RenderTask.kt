package kr.eg.egiwon.pdfsample.pdfview.render.model

import android.graphics.RectF

data class RenderTask(
    val width: Float,
    val height: Float,
    val bounds: RectF,
    val page: Int,
    val thumbnail: Boolean,
    val cacheOrder: Int,
    val isAnnotationRendering: Boolean
)