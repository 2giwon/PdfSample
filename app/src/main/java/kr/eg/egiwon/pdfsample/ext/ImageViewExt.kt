package kr.eg.egiwon.pdfsample.ext

import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import kr.eg.egiwon.pdfsample.R

@BindingAdapter("directorySrc")
fun AppCompatImageView.directorySrc(isDirectory: Boolean) {
    setImageDrawable(
        if (isDirectory) resources.getDrawable(R.drawable.ic_folder, null) else null
    )
}