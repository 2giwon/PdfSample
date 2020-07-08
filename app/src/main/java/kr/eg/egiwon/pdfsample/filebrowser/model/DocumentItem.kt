package kr.eg.egiwon.pdfsample.filebrowser.model

import android.graphics.Bitmap
import android.net.Uri
import kr.eg.egiwon.pdfsample.base.BaseIdentifier

data class DocumentItem(
    val name: String,
    val type: String,
    val isDirectory: Boolean,
    val uri: Uri,
    val size: String,
    val lastModified: String,
    val thumbnail: Bitmap
) : BaseIdentifier() {
    override val id: Any
        get() = uri
}