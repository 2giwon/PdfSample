package kr.eg.egiwon.pdfsample.pdfview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.FrameLayout

class PdfView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), PdfViewAction {

    private val paint = Paint().apply {
        isAntiAlias = true
    }

    private var pageBitmap: Bitmap? = null

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        pageBitmap?.let {
            canvas?.drawBitmap(it, 0f, 0f, paint)
        }

    }

    override fun loadPdfPage(block: () -> Bitmap) {
        pageBitmap = block()
    }
}