package kr.eg.egiwon.pdfsample.pdfview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.FrameLayout

class PdfView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), PdfViewAction {

    private val paint = Paint().apply {
        isAntiAlias = true
    }

    private var pageBitmap: Bitmap? = null

    init {
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        pageBitmap?.let {
            canvas?.drawBitmap(
                it,
                ((measuredWidth - it.width) / 2).toFloat(),
                ((measuredHeight - it.height) / 2).toFloat(),
                paint
            )
        }

    }

    override fun loadPdfPage(block: () -> Bitmap) {
        pageBitmap = block()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        pageBitmap?.let {
            val heightMode = MeasureSpec.getMode(heightMeasureSpec)
            val heightSize = MeasureSpec.getSize(heightMeasureSpec)

            val height = when (heightMode) {
                MeasureSpec.EXACTLY -> heightSize
                MeasureSpec.AT_MOST -> it.height
                else -> it.height
            }


            setMeasuredDimension(
                MeasureSpec.getSize(widthMeasureSpec), height + PAGE_DIVIDER
            )

        }

    }

    companion object {
        private const val PAGE_DIVIDER = 10
    }
}