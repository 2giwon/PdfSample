package kr.eg.egiwon.pdfsample.pdfview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class PdfView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr), PdfViewAction {

    private var desiredHeight: Int = 0

    private val paint = Paint().apply {
        isAntiAlias = true
    }

    init {
        setWillNotDraw(false)
    }

    private var pageBitmap: Bitmap? = null

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
        val requireWidth = context.resources.displayMetrics.widthPixels
        var requireHeight = 0

        pageBitmap?.let {
            requireHeight = (requireWidth * it.height) / it.width
            val scaledBitmap =
                Bitmap.createScaledBitmap(it, requireWidth, requireHeight, true)

            pageBitmap = scaledBitmap

            desiredHeight = scaledBitmap.height + 10
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> desiredHeight
            else -> desiredHeight
        }

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height)
    }
}