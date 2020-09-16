package kr.eg.egiwon.pdfsample.pdfview.render.model

import android.graphics.Bitmap
import android.graphics.RectF

data class PagePart(
    val page: Int,
    val renderedPart: Bitmap,
    val bounds: RectF,
    val cacheOrder: Int,
    val isThumbnail: Boolean
)