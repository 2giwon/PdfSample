package kr.eg.egiwon.pdfsample.pdfview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class PdfRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    @Suppress("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent?): Boolean {
        return false
    }
}