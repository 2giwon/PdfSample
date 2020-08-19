package kr.eg.egiwon.pdfsample.pdfview.internal

import android.animation.ObjectAnimator
import android.graphics.Rect
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.dynamicanimation.animation.*
import kr.eg.egiwon.pdfsample.pdfview.internal.MoveAnimator.Companion.DAMPING_RATIO
import kr.eg.egiwon.pdfsample.pdfview.internal.MoveAnimator.Companion.FRICTION
import kr.eg.egiwon.pdfsample.pdfview.internal.MoveAnimator.Companion.STIFFNESS

class VerticalMoveAnimator @VisibleForTesting constructor(
    private val targetView: View,
    private val topBound: Float,
    private val bottomBound: Float,
    private val maxScale: Float,
    private val spring: SpringAnimation,
    private val fling: FlingAnimation,
    private val animator: ObjectAnimator
) : MoveAnimator {

    constructor(
        targetView: View,
        topBound: Float,
        bottomBound: Float,
        maxScale: Float
    ) : this(
        targetView = targetView,
        topBound = topBound,
        bottomBound = bottomBound,
        maxScale = maxScale,
        spring = SpringAnimation(targetView, HORIZONTAL_PROPERTY).setSpring(SPRING_FORCE),
        fling = FlingAnimation(targetView, DynamicAnimation.Y).setFriction(FRICTION),
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

    private fun outOfBounds(rect: Rect): Boolean = topBound < rect.top || rect.bottom < bottomBound

    private fun adjustToBounds(rect: Rect, velocity: Float = 0f) {
        val scale: Float = when {
            maxScale < targetView.scaleX -> maxScale
            targetView.scaleX < 1f -> 1f
            else -> targetView.scaleX
        }

        val diff: Float = (targetView.height * scale - targetView.height) / 2

        if (topBound < rect.top) {
            cancel()
            val finalPosition: Float = topBound + diff
            spring.setStartVelocity(velocity).animateToFinalPosition(finalPosition)
        } else if (rect.bottom < bottomBound) {
            cancel()
            val finalPosition: Float = bottomBound - targetView.height.toFloat() - diff
            spring.setStartVelocity(velocity).animateToFinalPosition(finalPosition)
        }
    }

    private fun getExpectRect(): Rect {
        val targetRect = Rect()
        targetView.getHitRect(targetRect)
        return when {
            maxScale < targetView.scaleY -> scaleYDownRect(targetRect)
            targetView.scaleY < 1f -> scaleYUpRect(targetRect)
            else -> targetRect
        }
    }

    private fun scaleYUpRect(targetRect: Rect): Rect {
        val heightDiff: Int = (targetView.height - targetRect.height()) / 2
        val widthDiff: Int = (targetView.width - targetRect.width()) / 2
        return targetRect.leftTopScaledRect(widthDiff, heightDiff)
    }

    private fun scaleYDownRect(targetRect: Rect): Rect {
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

    override fun move(delta: Float) {
        cancel()
        with(animator) {
            setFloatValues(targetView.translationY + delta)
            start()
        }
    }

    override fun adjust() {
        val expectRect = getExpectRect()
        if (outOfBounds(expectRect)) {
            adjustToBounds(expectRect)
        }
    }

    override fun fling(velocity: Float) {
        cancel()
        with(fling) {
            addUpdateListener(updateListener)
            setStartVelocity(velocity).start()
        }
    }

    private fun cancel() {
        animator.cancel()
        spring.cancel()
        fling.cancel()
        fling.removeUpdateListener(updateListener)
    }

    companion object {

        private val ANIMATOR = ObjectAnimator().apply {
            setProperty(View.TRANSLATION_Y)
            interpolator = null
            duration = 0
        }

        private val HORIZONTAL_PROPERTY =
            object : FloatPropertyCompat<View>("Y") {
                override fun getValue(view: View): Float {
                    return view.y
                }

                override fun setValue(view: View, value: Float) {
                    view.y = value
                }
            }

        private val SPRING_FORCE =
            SpringForce().setStiffness(STIFFNESS).setDampingRatio(DAMPING_RATIO)
    }
}