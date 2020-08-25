package kr.eg.egiwon.pdfsample.pdfview

import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import kr.eg.egiwon.pdfsample.R
import kr.eg.egiwon.pdfsample.base.BaseActivity
import kr.eg.egiwon.pdfsample.databinding.ActivityPdfBinding
import kr.eg.egiwon.pdfsample.filebrowser.MainActivity.Companion.OPEN_DOCUMENT_URI
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class PdfViewActivity : BaseActivity<ActivityPdfBinding, PdfViewModel>(
    R.layout.activity_pdf
) {
    override val viewModel: PdfViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind {
            vm = viewModel
        }

        intent.extras?.getString(OPEN_DOCUMENT_URI)?.let { uri ->
            Single.timer(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy {
                    loadPdfDocument(uri)
                }.addTo(compositeDisposable)
        }

        addObserve()
    }

    override fun addObserve() {
        super.addObserve()

        viewModel.pdfPage.observe(this, Observer { pdfPage ->
            (binding.pdfView as? PdfViewAction)?.loadPdfPage { pdfPage.pageBitmap }
        })
    }

    private fun loadPdfDocument(it: String) {
        val uri = Uri.parse(it)
        runCatching {
            requireNotNull(contentResolver.openFileDescriptor(uri, "r"))
        }
            .onSuccess { fd -> viewModel.loadPdfDocument(fd) }
            .onFailure { throwable -> showToast(throwable.message ?: "") }
    }

//    private fun ActivityPdfBinding.initAdapter() {
//        rvPdf.adapter = object : BaseAdapter2(
//            BR.pdfPage,
//            mapOf(BR.vm to viewModel),
//            mapOf(PdfPage::class to R.layout.item_pdf_page)
//
//        ) {}
//        rvPdf.setHasFixedSize(true)
//    }
}