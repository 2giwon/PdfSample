package kr.eg.egiwon.pdfsample.util

import android.content.Context
import kr.eg.egiwon.pdfsample.ext.pixelToDP
import javax.inject.Inject

class DefaultSetting @Inject constructor(context: Context) {

    val defaultDocumentSpacing: Int by lazy {
        DEFAULT_DOCUMENT_SPACING.pixelToDP(context).toInt()  // dp
    }

    val defaultOffset: Float by lazy {
        DEFAULT_OFFSET.pixelToDP(context)
    }

    val defaultPartSize: Float = 256f

    val defaultCacheSize = 120

    companion object {
        private const val DEFAULT_DOCUMENT_SPACING = 10.0f
        private const val DEFAULT_OFFSET = 20f

        const val CACHE_SIZE = 120
        const val THUMBNAILS_CACHE_SIZE = 8
    }
}