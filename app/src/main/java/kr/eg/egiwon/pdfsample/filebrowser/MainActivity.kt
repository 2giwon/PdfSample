package kr.eg.egiwon.pdfsample.filebrowser

import androidx.activity.viewModels
import com.egiwon.scopedstorageexample.base.BaseActivity
import kr.eg.egiwon.pdfsample.R
import kr.eg.egiwon.pdfsample.databinding.ActivityMainBinding

class MainActivity :
    BaseActivity<ActivityMainBinding, FileBrowserViewModel>(R.layout.activity_main) {

    override val viewModel: FileBrowserViewModel by viewModels()

}