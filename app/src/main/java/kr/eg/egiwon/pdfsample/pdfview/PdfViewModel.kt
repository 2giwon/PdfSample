package kr.eg.egiwon.pdfsample.pdfview

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
import kr.eg.egiwon.pdfsample.Event
import kr.eg.egiwon.pdfsample.base.BaseViewModel
import kr.eg.egiwon.pdfsample.pdfcore.PdfReadable
import kr.eg.egiwon.pdfsample.pdfview.loader.PageLoadable
import kr.eg.egiwon.pdfsample.pdfview.loader.PageLoader
import kr.eg.egiwon.pdfsample.pdfview.model.PdfPage
import kr.eg.egiwon.pdfsample.pdfview.render.RenderTaskManager
import kr.eg.egiwon.pdfsample.pdfview.render.RenderTaskManagerImpl
import kr.eg.egiwon.pdfsample.pdfview.render.model.PagePart
import kr.eg.egiwon.pdfsample.pdfview.setup.PdfSetupManager
import kr.eg.egiwon.pdfsample.pdfview.setup.PdfSetupManagerImpl
import kr.eg.egiwon.pdfsample.util.DefaultSetting
import kr.eg.egiwon.pdfsample.util.Size


class PdfViewModel @ViewModelInject constructor(
    private val pdfReadable: PdfReadable,
    private val defaultSetting: DefaultSetting
) : BaseViewModel() {

    private val _isOpenDocument = MutableLiveData<Event<Boolean>>()
    val isOpenDocument: LiveData<Event<Boolean>> get() = _isOpenDocument

    private val _isShowLoadingBar = MutableLiveData<Boolean>()
    val isShowLoadingBar: LiveData<Boolean> get() = _isShowLoadingBar

    private val _pageCount = MutableLiveData<Event<Int>>()
    val pageCount: LiveData<Event<Int>> get() = _pageCount

    private val _pageSize = MutableLiveData<Event<Size<Float>>>()
    val pageSize: LiveData<Event<Size<Float>>> get() = _pageSize

    private val _pagePart = MutableLiveData<Event<PagePart>>()
    val pagePart: LiveData<Event<PagePart>> get() = _pagePart

    private val pdfPageList = mutableListOf<PdfPage>()

    private val pageSetupManager: PdfSetupManager by lazy(::createPdfSetupManager)

    private val _pageSetupCompletedManager = MutableLiveData<Event<PdfSetupManager>>()
    val pageSetupCompletedManager: LiveData<Event<PdfSetupManager>> get() = _pageSetupCompletedManager

    private val renderTaskManager: RenderTaskManager = RenderTaskManagerImpl(pdfReadable)

    private val pdfLoader: PageLoadable = PageLoader(
        defaultSetting,
        pageSetupManager,
        renderTaskManager
    )

    fun loadPdfDocument(fd: ParcelFileDescriptor) {
        Single.fromCallable {
            pdfReadable.openPdfDocument(fd)
        }.subscribeOn(Schedulers.single())
            .doOnSubscribe { _isShowLoadingBar.postValue(true) }
            .doOnError { _isShowLoadingBar.postValue(false) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                _isOpenDocument.value = Event(it)
            }.addTo(compositeDisposable)
    }

    fun requestPageSetup(viewSize: Size<Int>) {
        pageSetupManager.pageSetup(viewSize,
            onPageCount = {
                _pageCount.postValue(Event(it))
            },
            setupComplete = {
                _pageSetupCompletedManager.value = Event(pageSetupManager)
                loadPages(viewSize)
            }
        )
    }

    private fun loadPages(viewSize: Size<Int>) {
        pdfLoader.loadPages(viewSize)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    renderTaskManager.makePagePart(it)?.let { part ->
                        _pagePart.value = Event(part)
                    }
                },
                onComplete = {
                    _isShowLoadingBar.postValue(false)
                },
                onError = {
                    _isShowLoadingBar.postValue(false)
                }
            ).addTo(compositeDisposable)
    }

    private fun loadPdfBitmaps() {
        _pageCount.value?.let { pageCount ->
            Observable.fromIterable(0 until pageCount.peekContent())
                .doOnSubscribe { _isShowLoadingBar.postValue(true) }
                .doAfterTerminate { _isShowLoadingBar.postValue(false) }
                .subscribeOn(Schedulers.single())
                .concatMapEager { pageNum ->
                    Single.create<PdfPage> { emitter ->
                        pdfReadable.loadPdfBitmap(pageNum)?.let { documentBitmap ->
                            val size = pdfReadable.getPageSize(pageNum)
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
                        pdfReadable.closeDocument()
                    }
                ).addTo(compositeDisposable)
        }

    }

    private fun createPdfSetupManager(): PdfSetupManagerImpl =
        PdfSetupManagerImpl(pdfReadable, compositeDisposable, defaultSetting)

    fun requestPageSize(pageNum: Int) {
        _pageSize.value = Event(pageSetupManager.getPageSize(pageNum))
    }
}