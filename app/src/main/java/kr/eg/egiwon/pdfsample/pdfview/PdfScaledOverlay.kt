package kr.eg.egiwon.pdfsample.pdfview

import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout

class PdfScaledOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var frame: RectF? = null

    fun setFrame(frame: RectF) {
        this.frame = frame
    }
}