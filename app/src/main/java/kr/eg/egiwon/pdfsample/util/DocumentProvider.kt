package kr.eg.egiwon.pdfsample.util

import android.net.Uri
import android.os.ParcelFileDescriptor
import kr.eg.egiwon.pdfsample.filebrowser.model.DocumentItem

interface DocumentProvider {

    fun getDocuments(documentUri: Uri): List<DocumentItem>

    fun documentUriToParcelFileDescriptor(documentUri: Uri): ParcelFileDescriptor?
}