package kr.eg.egiwon.pdfsample.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

object GlideWrapper {
    private fun <T> asyncLoadImage(
        target: ImageView,
        loadContent: T,
        block: RequestOptions.() -> RequestOptions
    ) {
        loadImage(target, loadContent)
            .transitionApply()
            .applyRequestOptions(block)
            .into(target)
    }

    private fun <T> loadImage(
        target: ImageView,
        loadContent: T
    ): RequestBuilder<Bitmap> =
        Glide.with(target)
            .asBitmap()
            .load(loadContent)

    private fun RequestBuilder<Bitmap>.transitionApply(): RequestBuilder<Bitmap> {
        return transition(BitmapTransitionOptions.withCrossFade())
            .centerInside()
    }

    private fun RequestBuilder<Bitmap>.applyRequestOptions(
        block: RequestOptions.() -> RequestOptions
    ): RequestBuilder<Bitmap> {
        return apply(RequestOptions().block())
    }

    fun asyncLoadImage(
        target: ImageView,
        url: String?,
        requestOptions: RequestOptions = RequestOptions()
    ) {
        asyncLoadImage(target, url) {
            requestOptions
        }
    }

    fun asyncLoadImage(
        target: ImageView,
        bitmap: Bitmap?,
        requestOptions: RequestOptions = RequestOptions()
    ) {
        bitmap ?: return
        asyncLoadImage(target, bitmap) {
            requestOptions
        }
    }


    fun getBitmapFromUri(context: Context, uri: String, block: (Bitmap) -> Unit) {
        Glide.with(context)
            .asBitmap()
            .load(uri)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    block(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) = Unit
            })
    }

    fun loadThumbnailImage(
        target: ImageView,
        bitmap: Bitmap,
        thumbnailSize: Float,
        placeholder: Int = 0
    ) {
        loadImage(target, bitmap)
            .thumbnail(thumbnailSize)
            .transitionApply()
            .placeholder(placeholder)
            .into(target)
    }

    fun recycledImage(target: ImageView) {
        Glide.with(target).clear(target)
    }
}