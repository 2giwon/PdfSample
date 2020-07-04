package kr.eg.egiwon.pdfsample.filebrowser.model

import android.net.Uri
import kr.eg.egiwon.pdfsample.base.BaseIdentifier

data class DocumentItem(
    val name: String,
    val type: String,
    val isDirectory: Boolean,
    val uri: Uri,
    val size: String,
    val lastModified: String
) : BaseIdentifier() {
    override val id: Any
        get() = uri
}