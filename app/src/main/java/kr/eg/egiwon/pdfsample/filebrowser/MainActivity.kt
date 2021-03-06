package kr.eg.egiwon.pdfsample.filebrowser

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.BehaviorSubject
import kr.eg.egiwon.pdfsample.BR
import kr.eg.egiwon.pdfsample.EventObserver
import kr.eg.egiwon.pdfsample.R
import kr.eg.egiwon.pdfsample.base.BaseActivity
import kr.eg.egiwon.pdfsample.base.BaseAdapter2
import kr.eg.egiwon.pdfsample.databinding.ActivityMainBinding
import kr.eg.egiwon.pdfsample.ext.setupActionBar
import kr.eg.egiwon.pdfsample.filebrowser.model.DocumentItem
import kr.eg.egiwon.pdfsample.pdfview.PdfViewActivity

@AndroidEntryPoint
class MainActivity :
    BaseActivity<ActivityMainBinding, FileBrowserViewModel>(R.layout.activity_main) {

    override val viewModel: FileBrowserViewModel by viewModels()

    private val behaviorSubject = BehaviorSubject.createDefault(0L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupActionBar(R.id.toolbar) {
            title = getString(R.string.actionbar_title)
        }

        bind {
            vm = viewModel
            initAdapter()
        }

        viewModel.loadRootUri()
        processBackPressedExit()
    }

    override fun addObserve() {
        viewModel.directoryUri.observe(this, EventObserver {
            if (it.toString().isNotEmpty()) {
                viewModel.loadDirectory(it)
            } else {
                openIntentDirectory()
            }
        })

        viewModel.directoryLiveData.observe(this, EventObserver { document ->
            viewModel.setDocumentUri(document.uri)
        })

        viewModel.documentLiveData.observe(this, EventObserver { document ->
            openDocument(document)
        })

        viewModel.isBackBreadCrumbsEmpty.observe(this, EventObserver {
            behaviorSubject.onNext(System.currentTimeMillis())
        })
    }

    private fun ActivityMainBinding.initAdapter() {
        with(rvFiles) {
            adapter = object : BaseAdapter2(
                BR.documentItem,
                mapOf(BR.vm to viewModel),
                mapOf(DocumentItem::class to R.layout.item_file)
            ) {}
            setHasFixedSize(true)
            addItemDecoration(
                DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL)
            )
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (OPEN_DIRECTORY_REQUEST_CODE == requestCode) {
            if (resultCode == RESULT_OK) {
                val directoryUri = data?.data ?: return

                contentResolver.takePersistableUriPermission(
                    directoryUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                viewModel.setDirectoryUri(directoryUri)
                viewModel.saveRootUri(directoryUri.toString())
            } else {
                finish()
            }

        }
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    private fun openIntentDirectory() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        }

        startActivityForResult(intent, OPEN_DIRECTORY_REQUEST_CODE)
    }

    override fun onBackPressed() {
        viewModel.backBreadCrumbs()
    }

    private fun processBackPressedExit() {
        behaviorSubject.buffer(2, 1)
            .map { it[0] to it[1] }
            .subscribe {
                if (it.second - it.first < 2000L) {
                    super.onBackPressed()
                } else {
                    showToast(R.string.back_press_exit)
                }
            }.addTo(compositeDisposable)
    }

    private fun openDocument(item: DocumentItem) {
        val openIntent = Intent(this, PdfViewActivity::class.java).apply {
            putExtras(bundleOf(OPEN_DOCUMENT_URI to item.uri.toString()))
        }
        startActivity(openIntent)
//        try {
//            val openIntent = Intent(Intent.ACTION_VIEW).apply {
//                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//                data = item.uri
//            }
//            startActivity(openIntent)
//        } catch (ex: ActivityNotFoundException) {
//            showToast(getString(R.string.error_invalid_activity, item.name))
//        }
    }

    companion object {
        private const val OPEN_DIRECTORY_REQUEST_CODE = 0xf11e
        const val OPEN_DOCUMENT_URI = "open_document_uri"
    }

}