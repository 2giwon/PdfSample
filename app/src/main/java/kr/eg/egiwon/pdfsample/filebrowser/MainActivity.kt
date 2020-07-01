package kr.eg.egiwon.pdfsample.filebrowser

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.egiwon.scopedstorageexample.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import kr.eg.egiwon.pdfsample.R
import kr.eg.egiwon.pdfsample.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity :
    BaseActivity<ActivityMainBinding, FileBrowserViewModel>(R.layout.activity_main) {

    override val viewModel: FileBrowserViewModel by viewModels()

    private val behaviorSubject = BehaviorSubject.createDefault(0L)

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind {
            vm = viewModel
            initAdapter()
        }
    }

    private fun ActivityMainBinding.initAdapter() {
        rvFiles.adapter = FileViewAdapter(viewModel = viewModel)
        rvFiles.setHasFixedSize(true)
        rvFiles.addItemDecoration(
            DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL)
        )
    }

}