package kr.eg.egiwon.pdfsample.util

import android.content.Context
import kr.eg.egiwon.pdfsample.ext.pixelToDP
import javax.inject.Inject

class DefaultSetting @Inject constructor(context: Context) {

    val defaultDocumentSpacing: Float by lazy {
        DEFAULT_DOCUMENT_SPACING.pixelToDP(context)  // dp
    }

    companion object {
        private const val DEFAULT_DOCUMENT_SPACING = 10.0f
    }
}