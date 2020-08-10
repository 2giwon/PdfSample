package kr.eg.egiwon.pdfsample.pdfview

import android.graphics.Bitmap
import android.os.ParcelFileDescriptor
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kr.eg.egiwon.pdfsample.base.BaseViewModel
import kr.eg.egiwon.pdfsample.pdfcore.PdfReadable


class PdfViewModel @ViewModelInject constructor(
    private val pdfReadable: PdfReadable
) : BaseViewModel() {

    private val _pdfPageBitmap = MutableLiveData<Bitmap>()
    val pdfPageBitmap: LiveData<Bitmap> get() = _pdfPageBitmap

    fun loadPdfDocument(fd: ParcelFileDescriptor) {
        Single.create<Bitmap> { emitter ->
            pdfReadable.loadPdfBitmap(fd, 0)?.let {
                emitter.onSuccess(it)
            } ?: run {
                emitter.onError(Throwable())
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = {
                    errorThrowableMutableLiveData.value = it
                },
                onSuccess = {
                    _pdfPageBitmap.value = it
                }
            ).addTo(compositeDisposable)
    }
}