package kr.eg.egiwon.pdfsample.pdfview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PaintFlagsDrawFilter
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout
import dagger.hilt.android.AndroidEntryPoint
import kr.eg.egiwon.pdfsample.pdfview.cache.CacheManager
import kr.eg.egiwon.pdfsample.pdfview.cache.CacheManagerImpl
import kr.eg.egiwon.pdfsample.pdfview.render.model.PagePart
import kr.eg.egiwon.pdfsample.pdfview.setup.PdfSetupManager
import javax.inject.Inject

@AndroidEntryPoint
class PdfPageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val paint = Paint()

    private var pageCount = 0

    private val currentXOffset = 0f
    private val currentYOffset = 0f

    private val cacheManager: CacheManager = CacheManagerImpl()

    private val antialiasFilter =
        PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

    @Inject
    lateinit var viewModel: PdfViewModel

//    @Inject
//    lateinit var handler: RenderHandler

    private lateinit var pageSetupManager: PdfSetupManager

    private val pageParts = mutableListOf<PagePart>()

    init {
        setWillNotDraw(false)
//        handler.setActionListener(this)
    }

    fun setPageCount(pageCount: Int) {
        this.pageCount = pageCount
    }

    fun initCacheManager() {
        cacheManager.init()
    }

    fun setPageParts(parts: List<PagePart>) {
        pageParts.clear()
        pageParts.addAll(parts)

        for (part in pageParts) {
            post { onPagePartRendered(part) }
        }
    }

    fun setPagePart(part: PagePart) {
        post { onPagePartRendered(part) }
    }

    private fun onPagePartRendered(pagePart: PagePart) {
        cacheManager.cachePart(pagePart)
        invalidate()
    }

    fun setPageSetupManager(pageSetupManager: PdfSetupManager) {
        this.pageSetupManager = pageSetupManager
    }

    override fun onDraw(canvas: Canvas) {

        canvas.drawFilter = antialiasFilter

        val currentOffsetX = currentXOffset
        val currentOffsetY = currentYOffset
        canvas.translate(currentOffsetX, currentOffsetY)

        val pageParts = cacheManager.getPageParts()
        for (i in pageParts.indices) {
            drawPart(canvas, pageParts[i])
        }

        canvas.translate(-currentOffsetX, -currentOffsetY)

    }

    private fun drawPart(canvas: Canvas, part: PagePart) {
        val bounds: RectF = part.bounds
        val renderedBitmap = part.renderedPart

        if (renderedBitmap.isRecycled) {
            return
        }

        with(pageSetupManager) {
            val size = getPageSize(part.page)

            val translateY: Float = getPageOffset(part.page)
            val maxWidth: Float = getFitWidth()
            val translateX: Float = (maxWidth - size.width).toZoomScale() / 2

            canvas.translate(translateX, translateY)

            val srcRect = Rect(0, 0, renderedBitmap.width, renderedBitmap.height)

            val offsetX: Float = (bounds.left * size.width).toZoomScale()
            val offsetY: Float = (bounds.top * size.height).toZoomScale()
            val width: Float = (bounds.width() * size.width).toZoomScale()
            val height: Float = (bounds.height() * size.height).toZoomScale()

            val dstRect = RectF(offsetX, offsetY, (offsetX + width), (offsetY + height))

            val translationX: Float = currentXOffset + translateX
            val translationY: Float = currentYOffset + translateY
            if (translationX + dstRect.left >= this@PdfPageView.width ||
                translationX + dstRect.right <= 0 ||
                translationY + dstRect.top >= this@PdfPageView.height ||
                translationY + dstRect.bottom <= 0
            ) {
                canvas.translate(-translateX, -translateY)
                return
            }

            canvas.drawBitmap(renderedBitmap, srcRect, dstRect, paint)

            canvas.translate(-translateX, -translateY)
        }
    }

    private fun Float.toZoomScale(): Float = this * pageSetupManager.getPageZoom()

    fun clearData() {
        cacheManager.clearCache()
    }
}