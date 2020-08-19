package kr.eg.egiwon.pdfsample.pdfview.internal

import android.graphics.RectF
import android.view.View

class GestureAnimator(
    private val horizontalAnimator: MoveAnimator,
    private val verticalAnimator: MoveAnimator,
    private val scaleAnimator: ScaleAnimator
) : ActionListener {

    override fun onScaled(scale: Float) {
        scaleAnimator.scale(scale)
    }

    override fun onScaleEnded() {
        scaleAnimator.adjust()
    }

    override fun onMoved(dx: Float, dy: Float) {
        horizontalAnimator.move(dx)
        verticalAnimator.move(dy)
    }

    override fun onFling(velocityX: Float, velocityY: Float) {
        horizontalAnimator.fling(velocityX)
        verticalAnimator.fling(velocityY)
    }

    override fun onMoveEnded() {
        horizontalAnimator.adjust()
        verticalAnimator.adjust()
    }

    companion object {


        fun getInstance(target: View, frame: RectF, scale: Float): GestureAnimator =
            run {
                val horizontalMoveAnimator = HorizontalMoveAnimator(
                    targetView = target,
                    leftBound = frame.left,
                    rightBound = frame.right,
                    maxScale = scale
                )

                val verticalMoveAnimator = VerticalMoveAnimator(
                    targetView = target,
                    topBound = frame.top,
                    bottomBound = frame.bottom,
                    maxScale = scale
                )

                val scaleAnimator = ScaleAnimatorImpl(
                    targetView = target,
                    maxScale = scale
                )

                GestureAnimator(horizontalMoveAnimator, verticalMoveAnimator, scaleAnimator)
            }

    }
}