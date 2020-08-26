package kr.eg.egiwon.pdfsample.pdfview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.recyclerview.widget.RecyclerView
import kr.eg.egiwon.pdfsample.pdfview.internal.ActionListener
import kr.eg.egiwon.pdfsample.pdfview.internal.ScaleAnimator
import kr.eg.egiwon.pdfsample.pdfview.internal.ScaleAnimatorImpl

class PdfRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr), ActionListener {

    private var isScaled = false

    private val scaleAnimator: ScaleAnimator = ScaleAnimatorImpl(
        this, DEFAULT_MAX_SCALE
    )

    private val scaleListener =
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                super.onScaleEnd(detector)
                this@PdfRecyclerView.onScaleEnded()
            }

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                this@PdfRecyclerView.onScaled(detector.scaleFactor)
                return true
            }
        }

    private val scaleGestureDetector = ScaleGestureDetector(context, scaleListener)

    @Suppress("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
//        return false
        scaleGestureDetector.onTouchEvent(e)
        return super.onTouchEvent(e)
    }

    override fun onScaled(scale: Float) {
        scaleAnimator.scale(scale)
    }

    override fun onScaleEnded() {
        scaleAnimator.adjust()
    }

    override fun onMoved(dx: Float, dy: Float) {

    }

    override fun onFling(velocityX: Float, velocityY: Float) {

    }

    override fun onMoveEnded() {

    }

    companion object {
        private const val DEFAULT_MAX_SCALE = 25f
    }
}