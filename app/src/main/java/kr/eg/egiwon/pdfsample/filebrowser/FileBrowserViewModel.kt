package kr.eg.egiwon.pdfsample.filebrowser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.egiwon.scopedstorageexample.base.BaseViewModel

class FileBrowserViewModel : BaseViewModel() {

    private val _loadingBar = MutableLiveData<Boolean>()
    val loadingBar: LiveData<Boolean> get() = _loadingBar

}