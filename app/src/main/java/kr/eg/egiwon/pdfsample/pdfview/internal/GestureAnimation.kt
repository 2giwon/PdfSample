package kr.eg.egiwon.pdfsample.pdfview.internal

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.core.view.GestureDetectorCompat
import javax.inject.Inject

class GestureAnimation @Inject constructor(
    private val view: View,
    private val actionListener: ActionListener
) {

    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return true
        }

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            actionListener.onFling(velocityX, velocityY)
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            actionListener.onMoved(distanceX, distanceY)
            return true
        }

        override fun onShowPress(e: MotionEvent?) = Unit
        override fun onLongPress(e: MotionEvent?) = Unit
    }

    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            super.onScaleEnd(detector)
            actionListener.onScaleEnded()
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            actionListener.onScaled(detector.scaleFactor)
            return true
        }
    }

    private val gestureDetector = GestureDetectorCompat(view.context, gestureListener)
    private val scaleGestureDetector = ScaleGestureDetector(view.context, scaleListener)


    fun start() {
        @Suppress("ClickableViewAccessibility")
        view.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            scaleGestureDetector.onTouchEvent(event)
            when (event.action) {
                MotionEvent.ACTION_UP -> actionListener.onMoveEnded()
            }

            true
        }
    }

    fun stop() {
        view.setOnTouchListener(null)
    }
}