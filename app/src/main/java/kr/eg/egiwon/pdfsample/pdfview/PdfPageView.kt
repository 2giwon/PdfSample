package kr.eg.egiwon.pdfsample.pdfview

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.FrameLayout
import kr.eg.egiwon.pdfsample.pdfview.internal.ActionListener
import kr.eg.egiwon.pdfsample.pdfview.internal.ScaleAnimator
import kr.eg.egiwon.pdfsample.pdfview.internal.ScaleAnimatorImpl
import kr.eg.egiwon.pdfsample.pdfview.model.PdfPage
import kotlin.math.max
import kotlin.math.min

class PdfPageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ActionListener {

    private var scaleFactor = 1.0f

    private val scaleAnimator: ScaleAnimator = ScaleAnimatorImpl(this, DEFAULT_MAX_SCALE)

    private val paint = Paint()

    private val pdfPages: MutableList<PdfPage> = mutableListOf()
    private var viewHeight = 0f
    private var pageCount = 0

    init {
        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> viewHeight.toInt()
            else -> viewHeight.toInt()
        }

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height)

    }

    fun addPdfPage(page: PdfPage) {
        pdfPages.add(page)
//        drawContents()
        postInvalidate()
    }

//    private fun drawContents() {
//        synchronized(surfaceHolder) {
//            var canvas: Canvas? = null
//            try {
//                canvas = surfaceHolder.lockCanvas()
//                draw(canvas)
//
//            } finally {
//                if (canvas != null) {
//                    surfaceHolder.unlockCanvasAndPost(canvas)
//                }
//            }
//        }
//    }

    fun setPageCount(pageCount: Int) {
        this.pageCount = pageCount
    }

    private val scaleListener =
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

            override fun onScaleEnd(detector: ScaleGestureDetector) {
                super.onScaleEnd(detector)
                this@PdfPageView.onScaleEnded()
            }

            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor
                scaleFactor = max(0.1f, min(scaleFactor, 5.0f))
                this@PdfPageView.onScaled(scaleFactor)
                return true
            }
        }

    private val scaleGestureDetector = ScaleGestureDetector(context, scaleListener)

    @Suppress("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {

        return scaleGestureDetector.onTouchEvent(e)
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