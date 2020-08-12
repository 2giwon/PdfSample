package kr.eg.egiwon.pdfsample.ext

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import kr.eg.egiwon.pdfsample.R
import kr.eg.egiwon.pdfsample.util.GlideWrapper

@BindingAdapter("directorySrc")
fun AppCompatImageView.directorySrc(isDirectory: Boolean) {
    setImageDrawable(
        if (isDirectory) {
            resources.getDrawable(R.drawable.ic_folder, null)
        } else {
            resources.getDrawable(R.drawable.ic_pdf, null)
        }
    )

}

@BindingAdapter("loadAsyncImage", "thumbnailSize")
fun ImageView.loadAsyncThumbnailImage(bitmap: Bitmap, size: Float) =
    GlideWrapper.loadThumbnailImage(this, bitmap, size, R.drawable.ic_pdf)

@BindingAdapter("loadPdfPageBitmap")
fun ImageView.loadAsyncPdfPageBitmap(bitmap: Bitmap) =
    GlideWrapper.asyncLoadImage(this, bitmap)