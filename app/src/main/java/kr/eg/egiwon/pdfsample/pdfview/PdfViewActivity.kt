package kr.eg.egiwon.pdfsample.pdfview

import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import kr.eg.egiwon.pdfsample.R
import kr.eg.egiwon.pdfsample.base.BaseActivity
import kr.eg.egiwon.pdfsample.databinding.ActivityPdfBinding
import kr.eg.egiwon.pdfsample.filebrowser.MainActivity.Companion.OPEN_DOCUMENT_URI

@AndroidEntryPoint
class PdfViewActivity : BaseActivity<ActivityPdfBinding, PdfViewModel>(
    R.layout.activity_pdf
) {
    override val viewModel: PdfViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.extras?.getString(OPEN_DOCUMENT_URI)?.let {
            val uri = Uri.parse(it)
            runCatching {
                requireNotNull(contentResolver.openFileDescriptor(uri, "r"))
            }
                .onSuccess { fd -> viewModel.loadPdfDocument(fd) }
                .onFailure { throwable -> showToast(throwable.message ?: "") }

        }

        addObserve()
    }

    override fun addObserve() {
        super.addObserve()

        viewModel.pdfPageBitmap.observe(this, Observer {
            (binding.pdfView as PdfViewAction).loadPdfPage { it }
        })
    }
}