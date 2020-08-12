package kr.eg.egiwon.pdfsample.pdfview

import android.graphics.Bitmap
import android.os.ParcelFileDescriptor
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kr.eg.egiwon.pdfsample.base.BaseViewModel
import kr.eg.egiwon.pdfsample.pdfcore.PdfReadable
import kr.eg.egiwon.pdfsample.pdfview.model.PdfPage


class PdfViewModel @ViewModelInject constructor(
    private val pdfReadable: PdfReadable
) : BaseViewModel() {

    private val _isOpenDocument = MutableLiveData<Boolean>()
    val isOpenDocument: LiveData<Boolean> get() = _isOpenDocument

    private val _pdfPages = MutableLiveData<List<PdfPage>>()
    val pdfPages: LiveData<List<PdfPage>> get() = _pdfPages

    private val _isShowLoadingBar = MutableLiveData<Boolean>()
    val isShowLoadingBar: LiveData<Boolean> get() = _isShowLoadingBar

    private var _pageCount = 0

    private val pdfPageList = mutableListOf<PdfPage>()
    private var pageNum = 0

    fun loadPdfDocument(fd: ParcelFileDescriptor) {
        val isOpened = pdfReadable.openPdfDocument(fd)
        if (isOpened) {
            _pageCount = pdfReadable.getPageCount()
            loadPdfBitmaps()
        }
        _isOpenDocument.value = isOpened
    }

    fun loadPdfBitmaps() {
        Observable.fromIterable(0 until _pageCount)
            .doOnSubscribe { _isShowLoadingBar.postValue(true) }
            .doAfterTerminate { _isShowLoadingBar.postValue(false) }
            .subscribeOn(Schedulers.single())
            .concatMapEager { pageNum ->
                Single.create<Bitmap> { emitter ->
                    pdfReadable.loadPdfBitmap(pageNum)?.let {
                        emitter.onSuccess(it)
                    }
                }.toObservable()

            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    pdfPageList.add(
                        PdfPage(pageNum, it, pageNum++)
                    )
                },
                onError = {
                    errorThrowableMutableLiveData.value = it
                },
                onComplete = {
                    _pdfPages.value = pdfPageList
                    pdfReadable.closeDocument()
                }
            ).addTo(compositeDisposable)
    }
}