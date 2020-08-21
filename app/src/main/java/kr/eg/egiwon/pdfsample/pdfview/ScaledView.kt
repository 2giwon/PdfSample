package kr.eg.egiwon.pdfsample.pdfview

import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.view.ViewTreeObserver
import androidx.constraintlayout.widget.ConstraintLayout
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_pdf.view.*
import kr.eg.egiwon.pdfsample.R
import kr.eg.egiwon.pdfsample.pdfview.internal.GestureAnimation
import kr.eg.egiwon.pdfsample.pdfview.internal.GestureAnimator

class ScaledView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var frameCache: RectF? = null

    private val compositeDisposable: CompositeDisposable by lazy(::CompositeDisposable)

    private val pdfPageView: PdfPageView by lazy { pdf_page_view }
    private val pdfScaledOverlay: PdfScaledOverlay by lazy { pdf_scaled_overlay }

    init {

        val scale: Float
        val percentWidth: Float
        val percentHeight: Float

        val typeArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.ScaledView,
            0,
            0
        )

        try {

            scale = typeArray.getFloat(
                R.styleable.ScaledView_pdf_max_scale,
                DEFAULT_MAX_SCALE
            )

            percentWidth = typeArray.getFraction(
                R.styleable.ScaledView_pdf_frame_width_percent,
                DEFAULT_BASE,
                DEFAULT_PBASE,
                DEFAULT_PERCENT_WIDTH
            )

            percentHeight = typeArray.getFraction(
                R.styleable.ScaledView_pdf_frame_height_percent,
                DEFAULT_BASE,
                DEFAULT_PBASE,
                DEFAULT_PERCENT_HEIGHT
            )

        } finally {
            typeArray.recycle()
        }

        viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                animatorStart(percentWidth, percentHeight, scale)
                when {
                    viewTreeObserver.isAlive -> viewTreeObserver.removeOnPreDrawListener(this)
                    else -> pdfScaledOverlay.viewTreeObserver.removeOnPreDrawListener(this)
                }
                return true
            }

        })
    }

    override fun onDetachedFromWindow() {
        compositeDisposable.dispose()
        super.onDetachedFromWindow()
    }

    private fun animatorStart(percentWidth: Float, percentHeight: Float, scale: Float) {
        val totalWidth = measuredWidth.toFloat()
        val totalHeight = measuredHeight.toFloat()
        val frameWidth = measuredWidth * percentWidth
        val frameHeight = measuredHeight * percentHeight
        val frame = RectF(
            (totalWidth - frameWidth) / 2f,
            (totalHeight - frameHeight) / 2f,
            (totalWidth + frameWidth) / 2f,
            (totalHeight + frameHeight) / 2f
        )

        pdfPageView.setFrame(frame)
        pdfPageView.requestLayout()
        pdfScaledOverlay.setFrame(frame)
        pdfScaledOverlay.requestLayout()
        frameCache = frame

        val animator = GestureAnimator.getInstance(pdfPageView, frame, scale)
        val animation = GestureAnimation(pdfScaledOverlay, animator)
        animation.start()
    }

    companion object {
        private const val DEFAULT_MAX_SCALE = 5f
        private const val DEFAULT_BASE = 1
        private const val DEFAULT_PBASE = 1

        private const val DEFAULT_PERCENT_WIDTH = 0.8f
        private const val DEFAULT_PERCENT_HEIGHT = 0.8f
    }
}