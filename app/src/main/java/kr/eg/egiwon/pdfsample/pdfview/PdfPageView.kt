package kr.eg.egiwon.pdfsample.pdfview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout
import kr.eg.egiwon.pdfsample.pdfview.model.PdfPage

class PdfPageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var frame: RectF? = null

    private val paint = Paint().apply {
        isAntiAlias = true
    }

    private val pageBitmaps: MutableList<Bitmap> = mutableListOf()
    private var viewHeight = 0f

    init {
        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

//        val height = measuredHeight.toFloat()
//        val width = measuredWidth.toFloat()
//        val frameRect = this@PdfPageView.frame
//        val widthScale = if (frameRect != null) frameRect.width() / width else 1f
//        val heightScale = if (frameRect != null) frameRect.height() / height else 1f
//        val scale = maxOf(widthScale, heightScale)
//        setMeasuredDimension((width * scale).toInt(), (height * scale).toInt())

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> viewHeight.toInt()
            else -> viewHeight.toInt()
        }

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height)

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        pageBitmaps.forEachIndexed { index, bitmap ->
            canvas?.drawBitmap(
                bitmap,
                ((measuredWidth - bitmap.width) / 2).toFloat(),
                ((bitmap.height + PAGE_DIVIDER) * index).toFloat(),
                paint
            )
        }

    }

    fun setFrame(frame: RectF) {
        this.frame = frame
    }

    fun addPdfPage(page: PdfPage) {
        pageBitmaps += page.pageBitmap
        viewHeight += page.pageBitmap.height + PAGE_DIVIDER
        requestLayout()
    }

    companion object {
        private const val PAGE_DIVIDER = 10
    }
}