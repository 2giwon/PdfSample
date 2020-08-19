package kr.eg.egiwon.pdfsample.pdfview.internal

import android.animation.ObjectAnimator
import android.view.View
import androidx.annotation.VisibleForTesting

class ScaleAnimatorImpl @VisibleForTesting constructor(
    private val targetView: View,
    private val maxScale: Float,
    private val animatorX: ObjectAnimator,
    private val animatorY: ObjectAnimator
) : ScaleAnimator {

    constructor(
        targetView: View,
        maxScale: Float
    ) : this(
        targetView = targetView,
        maxScale = maxScale,
        animatorX = ANIMATOR_X,
        animatorY = ANIMATOR_Y
    )

    init {
        animatorX.target = targetView
        animatorY.target = targetView
    }

    override fun scale(scale: Float) {
        animatorX.scaleX(targetView.scaleX * scale)
        animatorY.scaleY(targetView.scaleY * scale)
    }

    private fun ObjectAnimator.scaleX(scale: Float) {
        cancel()
        clearProperties()
        setFloatValues(scale)
        start()
    }

    private fun ObjectAnimator.scaleY(scale: Float) {
        cancel()
        clearProperties()
        setFloatValues(scale)
        start()
    }

    override fun adjust() {
        if (targetView.scaleX < ORIGINAL_SCALE) {
            animatorX.scaleX(ORIGINAL_SCALE)
        } else if (maxScale < targetView.scaleX) {
            animatorX.scaleY(maxScale)
        }

        if (targetView.scaleY < ORIGINAL_SCALE) {
            animatorY.scaleY(ORIGINAL_SCALE)
        } else if (maxScale < targetView.scaleY) {
            animatorY.scaleY(maxScale)
        }
    }

    private fun ObjectAnimator.clearProperties() {
        duration = 0
        interpolator = null
    }

    private fun ObjectAnimator.setupProperties() {
        duration = ADJUSTING_DURATION
        interpolator = DecelerateInterpolator(ADJUSTING_FACTOR)
    }

    companion object {

        private val ANIMATOR_X = ObjectAnimator().apply {
            setProperty(View.SCALE_X)
        }

        private val ANIMATOR_Y = ObjectAnimator().apply {
            setProperty(View.SCALE_Y)
        }
    }
}