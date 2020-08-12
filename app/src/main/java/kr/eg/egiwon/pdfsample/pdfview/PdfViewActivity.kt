package kr.eg.egiwon.pdfsample.pdfview

import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kr.eg.egiwon.pdfsample.BR
import kr.eg.egiwon.pdfsample.R
import kr.eg.egiwon.pdfsample.base.BaseActivity
import kr.eg.egiwon.pdfsample.base.BaseAdapter2
import kr.eg.egiwon.pdfsample.databinding.ActivityPdfBinding
import kr.eg.egiwon.pdfsample.filebrowser.MainActivity.Companion.OPEN_DOCUMENT_URI
import kr.eg.egiwon.pdfsample.pdfview.model.PdfPage

@AndroidEntryPoint
class PdfViewActivity : BaseActivity<ActivityPdfBinding, PdfViewModel>(
    R.layout.activity_pdf
) {
    override val viewModel: PdfViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind {
            vm = viewModel
            rvPdf.adapter = object : BaseAdapter2(
                BR.pdfPage,
                mapOf(BR.vm to viewModel),
                mapOf(PdfPage::class to R.layout.item_pdf_page)

            ) {}
            rvPdf.setHasFixedSize(true)
        }
        intent.extras?.getString(OPEN_DOCUMENT_URI)?.let {
            val uri = Uri.parse(it)
            runCatching {
                requireNotNull(contentResolver.openFileDescriptor(uri, "r"))
            }
                .onSuccess { fd -> viewModel.loadPdfDocument(fd) }
                .onFailure { throwable -> showToast(throwable.message ?: "") }

        }

    }

}