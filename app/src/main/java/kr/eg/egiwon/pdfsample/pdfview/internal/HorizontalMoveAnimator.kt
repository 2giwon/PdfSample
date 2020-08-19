package kr.eg.egiwon.pdfsample.pdfview.internal

import android.animation.ObjectAnimator
import android.graphics.Rect
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.dynamicanimation.animation.*

class HorizontalMoveAnimator @VisibleForTesting constructor(
    private val targetView: View,
    private val leftBound: Float,
    private val rightBound: Float,
    private val maxScale: Float,
    private val spring: SpringAnimation,
    private val fling: FlingAnimation,
    private val animator: ObjectAnimator
) : MoveAnimator {

    constructor(
        targetView: View,
        leftBound: Float,
        rightBound: Float,
        maxScale: Float
    ) : this(
        targetView = targetView,
        leftBound = leftBound,
        rightBound = rightBound,
        maxScale = maxScale,
        spring = SpringAnimation(targetView, VERTICAL_PROPERTY).setSpring(SPRING_FORCE),
        fling = FlingAnimation(targetView, DynamicAnimation.X).setFriction(MoveAnimator.FRICTION),
        animator = ANIMATOR
    )

    init {
        animator.target = targetView
    }

    private val updateListener = DynamicAnimation.OnAnimationUpdateListener { _, _, velocity ->
        val expectedRect = getExpectRect()
        if (outOfBounds(expectedRect)) {
            adjustToBounds(expectedRect, velocity)
        }
    }

    private fun getExpectRect(): Rect {
        val targetRect = Rect()
        targetView.getHitRect(targetRect)
        return when {
            maxScale < targetView.scaleX -> scaleXDownRect(targetRect)
            targetView.scaleX < 1f -> scaleXUpRect(targetRect)
            else -> targetRect
        }
    }

    private fun scaleXUpRect(targetRect: Rect): Rect {
        val heightDiff: Int = (targetView.height - targetRect.height()) / 2
        val widthDiff: Int = (targetView.width - targetRect.width()) / 2
        return targetRect.leftTopScaledRect(widthDiff, heightDiff)
    }

    private fun scaleXDownRect(targetRect: Rect): Rect {
        val heightDiff: Int =
            ((targetRect.height() - targetRect.height() * (maxScale / targetView.scaleY)) / 2)
                .toInt()

        val widthDiff: Int =
            ((targetRect.width() - targetRect.width() * (maxScale / targetView.scaleY)) / 2)
                .toInt()

        return targetRect.leftTopScaledRect(widthDiff, heightDiff)
    }

    private fun Rect.leftTopScaledRect(widthDiff: Int, heightDiff: Int): Rect =
        Rect(
            left + widthDiff,
            top + heightDiff,
            right - widthDiff,
            bottom - heightDiff
        )

    private fun outOfBounds(rect: Rect): Boolean = leftBound < rect.left || rect.right < rightBound

    private fun adjustToBounds(rect: Rect, velocity: Float = 0f) {
        val scale: Float = when {
            maxScale < targetView.scaleX -> maxScale
            targetView.scaleX < 1f -> 1f
            else -> targetView.scaleX
        }

        val diff: Float = (targetView.width * scale - targetView.width) / 2

        if (leftBound < rect.left) {
            cancel()
            val finalPosition: Float = leftBound + diff
            spring.setStartVelocity(velocity).animateToFinalPosition(finalPosition)
        } else if (rect.right < rightBound) {
            cancel()
            val finalPosition: Float = rightBound - targetView.width.toFloat() - diff
            spring.setStartVelocity(velocity).animateToFinalPosition(finalPosition)
        }
    }

    private fun cancel() {
        animator.cancel()
        spring.cancel()
        fling.cancel()
        fling.removeUpdateListener(updateListener)
    }

    override fun move(delta: Float) {
        cancel()
        with(animator) {
            setFloatValues(targetView.translationX + delta)
            start()
        }
    }

    override fun adjust() {
        val expectedRect: Rect = getExpectRect()
        if (outOfBounds(expectedRect)) {
            adjustToBounds(expectedRect)
        }
    }

    override fun fling(velocity: Float) {
        cancel()
        with(fling) {
            addUpdateListener(updateListener)
            setStartVelocity(velocity).start()
        }
    }

    companion object {

        private val ANIMATOR = ObjectAnimator().apply {
            setProperty(View.TRANSLATION_X)
            interpolator = null
            duration = 0
        }

        private val VERTICAL_PROPERTY: FloatPropertyCompat<View> =
            object : FloatPropertyCompat<View>("X") {
                override fun getValue(view: View): Float {
                    return view.x
                }

                override fun setValue(view: View, value: Float) {
                    view.x = value
                }
            }

        private val SPRING_FORCE: SpringForce =
            SpringForce().setStiffness(MoveAnimator.STIFFNESS).setDampingRatio(MoveAnimator.DAMPING_RATIO)
    }
}