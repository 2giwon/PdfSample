package kr.eg.egiwon.pdfsample.util

import android.content.Context
import kr.eg.egiwon.pdfsample.ext.pixelToDP
import javax.inject.Inject

class DefaultSetting @Inject constructor(context: Context) {

    val defaultDocumentSpacing: Float by lazy {
        DEFAULT_DOCUMENT_SPACING.pixelToDP(context)  // dp
    }

    val defaultOffset: Float by lazy {
        DEFAULT_OFFSET.pixelToDP(context)
    }

    val defaultPartSize: Float = 256f

    companion object {
        private const val DEFAULT_DOCUMENT_SPACING = 10.0f
        private const val DEFAULT_OFFSET = 20f
    }
}