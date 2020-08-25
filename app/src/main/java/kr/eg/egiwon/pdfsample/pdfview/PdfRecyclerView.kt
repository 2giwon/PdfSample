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

    private var isScaled = false

    @Suppress("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
//        return false
        return when (e.actionMasked and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_POINTER_DOWN,
            MotionEvent.ACTION_POINTER_UP -> {
                isScaled = true
                !isScaled
            }
            MotionEvent.ACTION_DOWN -> super.onTouchEvent(e)
            MotionEvent.ACTION_UP -> {
                val ret = if (isScaled) false else super.onTouchEvent(e)
                isScaled = false
                ret
            }
            MotionEvent.ACTION_MOVE -> if (isScaled) false else super.onTouchEvent(e)
            else -> super.onTouchEvent(e)
        }
    }
}