package kr.eg.egiwon.pdfsample.pdfview

import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout

class PdfPageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var frame: RectF? = null

    init {
        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val height = measuredHeight.toFloat()
        val width = measuredWidth.toFloat()
        val frameRect = this@PdfPageView.frame
        val widthScale = if (frameRect != null) frameRect.width() / width else 1f
        val heightScale = if (frameRect != null) frameRect.height() / height else 1f
        val scale = maxOf(widthScale, heightScale)
        setMeasuredDimension((width * scale).toInt(), (height * scale).toInt())
    }

    fun setFrame(frame: RectF) {
        this.frame = frame
    }
}