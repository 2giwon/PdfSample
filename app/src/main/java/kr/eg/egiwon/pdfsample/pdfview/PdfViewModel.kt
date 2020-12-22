package kr.eg.egiwon.pdfsample.pdfview

import android.os.ParcelFileDescriptor
import android.os.SystemClock
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kr.eg.egiwon.pdfsample.Event
import kr.eg.egiwon.pdfsample.base.BaseViewModel
import kr.eg.egiwon.pdfsample.pdfcore.PdfCoreAction
import kr.eg.egiwon.pdfsample.pdfview.loader.PageLoadable
import kr.eg.egiwon.pdfsample.pdfview.loader.PageLoader
import kr.eg.egiwon.pdfsample.pdfview.model.PdfPage
import kr.eg.egiwon.pdfsample.pdfview.render.model.PagePart
import kr.eg.egiwon.pdfsample.pdfview.setup.PdfSetupManager
import kr.eg.egiwon.pdfsample.pdfview.setup.PdfSetupManagerImpl
import kr.eg.egiwon.pdfsample.util.DefaultSetting
import kr.eg.egiwon.pdfsample.util.Size


class PdfViewModel @ViewModelInject constructor(
    private val pdfCoreAction: PdfCoreAction,
    private val defaultSetting: DefaultSetting
) : BaseViewModel() {

    private val _isOpenDocument = MutableLiveData<Event<Boolean>>()
    val isOpenDocument: LiveData<Event<Boolean>> get() = _isOpenDocument

    private val _isShowLoadingBar = MutableLiveData<Boolean>()
    val isShowLoadingBar: LiveData<Boolean> get() = _isShowLoadingBar

    private val _pageCount = MutableLiveData<Int>()
    val pageCount: LiveData<Int> get() = _pageCount

    private val _pageSize = MutableLiveData<Event<Size<Float>>>()
    val pageSize: LiveData<Event<Size<Float>>> get() = _pageSize

    private val _pagePart = MutableLiveData<Event<PagePart>>()
    val pagePart: LiveData<Event<PagePart>> get() = _pagePart

    private val _time = MutableLiveData<Event<Long>>()
    val time: LiveData<Event<Long>> get() = _time

    private val pdfPageList = mutableListOf<PdfPage>()

    private val pageSetupManager: PdfSetupManager by lazy(::createPdfSetupManager)

    private val _pageSetupCompletedManager = MutableLiveData<Event<PdfSetupManager>>()
    val pageSetupCompletedManager: LiveData<Event<PdfSetupManager>> get() = _pageSetupCompletedManager

    val errorLiveData: LiveData<Unit> = Transformations.map(errorThrowableMutableLiveData) {
        closeDocument()
    }

    private var startTime = 0L
    private var endTime = 0L

    private val pdfLoader: PageLoadable =
        PageLoader(defaultSetting, pageSetupManager, pdfCoreAction)

    fun loadPdfDocument(fd: ParcelFileDescriptor) {
        startTime = SystemClock.elapsedRealtime()
        Single.fromCallable {
            pdfCoreAction.openPdfDocument(fd)
        }.subscribeOn(Schedulers.single())
            .doOnSubscribe { _isShowLoadingBar.postValue(true) }
            .doOnError { _isShowLoadingBar.postValue(false) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    _isOpenDocument.value = Event(it)
                    _pageCount.value = pageSetupManager.getPageCountFromLoadedDocument()

                },
                onError = {
                    errorThrowableMutableLiveData.value = it
                }
            ).addTo(compositeDisposable)
    }

    fun requestPageSetup(viewSize: Size<Int>) {
        startTime = SystemClock.elapsedRealtime()
        pageSetupManager.pageSetup(viewSize)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    endTime = SystemClock.elapsedRealtime()
                    _time.value = Event(endTime - startTime)
                    _pageSetupCompletedManager.value = Event(pageSetupManager)
                    loadPages(viewSize)
                },
                onError = {
                    errorThrowableMutableLiveData.value = it
                }
            ).addTo(compositeDisposable)
    }

    private fun loadPages(viewSize: Size<Int>) {
        pdfLoader.loadPages(viewSize)
            .subscribeOn(Schedulers.computation())
            .doAfterTerminate { _isShowLoadingBar.postValue(false) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { part -> _pagePart.value = Event(part) },
                onError = { errorThrowableMutableLiveData.value = it }
            ).addTo(compositeDisposable)
    }

    private fun loadPdfBitmaps() {
        _pageCount.value?.let { pageCount ->
            Observable.fromIterable(0 until pageCount)
                .doOnSubscribe { _isShowLoadingBar.postValue(true) }
                .doAfterTerminate { _isShowLoadingBar.postValue(false) }
                .subscribeOn(Schedulers.single())
                .concatMapEager { pageNum ->
                    Single.create<PdfPage> { emitter ->
                        pdfCoreAction.loadPdfBitmap(pageNum)?.let { documentBitmap ->
                            val size = pdfCoreAction.getPageSize(pageNum)
                            val pdfPage =
                                PdfPage(pageNum, documentBitmap, pageNum, size.width, size.height)
                            emitter.onSuccess(pdfPage)
                        }
                    }.toObservable()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onNext = { pdfPage ->
                        pdfPageList.add(pdfPage)
//                        _pdfPage.value = Event(pdfPage)
//                    _pdfPages.value = pdfPageList
                    },
                    onError = {
                        errorThrowableMutableLiveData.value = it
                    },
                    onComplete = {
                        pdfCoreAction.closeDocument()
                    }
                ).addTo(compositeDisposable)
        }

    }

    private fun createPdfSetupManager(): PdfSetupManagerImpl =
        PdfSetupManagerImpl(pdfCoreAction, compositeDisposable, defaultSetting)

    fun requestPageSize(pageNum: Int) {
        _pageSize.value = Event(pageSetupManager.getPageSize(pageNum))
    }

    fun closeDocument() {
        pdfCoreAction.closeDocument()
    }
}