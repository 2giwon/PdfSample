package kr.eg.egiwon.pdfsample.pdfview.internal

interface ActionListener {

    fun onScaled(scale: Float)

    fun onScaleEnded()

    fun onMoved(dx: Float, dy: Float)

    fun onFling(velocityX: Float, velocityY: Float)

    fun onMoveEnded()
}