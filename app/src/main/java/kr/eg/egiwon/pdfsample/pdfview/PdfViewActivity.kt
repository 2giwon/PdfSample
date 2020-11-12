package kr.eg.egiwon.pdfsample.pdfview

import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import kr.eg.egiwon.pdfsample.EventObserver
import kr.eg.egiwon.pdfsample.R
import kr.eg.egiwon.pdfsample.base.BaseActivity
import kr.eg.egiwon.pdfsample.databinding.ActivityPdfBinding
import kr.eg.egiwon.pdfsample.filebrowser.MainActivity.Companion.OPEN_DOCUMENT_URI
import kr.eg.egiwon.pdfsample.util.Size
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
            Single.timer(200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy {
                    loadPdfDocument(uri)
                }.addTo(compositeDisposable)
        }

    }

    override fun addObserve() {
        super.addObserve()
        viewModel.isOpenDocument.observe(this, EventObserver { isOpened ->
            if (isOpened) {
                val size =
                    Size(binding.pdfPageView.measuredWidth, binding.pdfPageView.measuredHeight)

                viewModel.requestPageSetup(size)
            }
        })
//
//        viewModel.pdfPage.observe(this, EventObserver {
//            binding.pdfPageView.addPdfPage(it)
//        })

        viewModel.pageCount.observe(this, EventObserver { pageCount ->
            binding.pdfPageView.setPageCount(pageCount)
        })

        viewModel.pagePart.observe(this, EventObserver { pagePart ->
            binding.pdfPageView.setPagePart(pagePart)
        })

        viewModel.pageSetupCompletedManager.observe(this, EventObserver {
            binding.pdfPageView.initCacheManager()
            binding.pdfPageView.setPageSetupManager(it)
        })

        viewModel.time.observe(this, EventObserver {
            showToast("pageSetup Complete Elapsed Time ${it}ms")
        })
    }

    override fun onStop() {
        super.onStop()
        viewModel.closeDocument()
        binding.pdfPageView.clearData()
    }

    private fun loadPdfDocument(it: String) {
        val uri = Uri.parse(it)
        runCatching {
            requireNotNull(contentResolver.openFileDescriptor(uri, "r"))
        }
            .onSuccess { fd -> viewModel.loadPdfDocument(fd) }
            .onFailure { throwable -> showToast(throwable.message ?: "") }
    }

}