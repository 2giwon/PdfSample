package kr.eg.egiwon.pdfsample.pdfview.setup

import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kr.eg.egiwon.pdfsample.pdfcore.PdfReadable
import kr.eg.egiwon.pdfsample.pdfview.pagesize.PageSizeOptimizeManagerImpl
import kr.eg.egiwon.pdfsample.util.DefaultSetting
import kr.eg.egiwon.pdfsample.util.Size

class PdfSetupManagerImpl(
    private val pdfReadable: PdfReadable,
    private val compositeDisposable: CompositeDisposable,
    private val defaultSetting: DefaultSetting
) : PdfSetupManager {

    private var maxPageSize = Size(0, 0)

    private var fitPageWidth = Size(0f, 0f)
    private var fitPageHeight = Size(0f, 0f)

    private val pageSizes = mutableListOf<Size<Float>>()

    private val originalPageSizes = mutableListOf<Size<Int>>()

    private val pageOffsets = mutableListOf<Float>()

    private var documentLength: Float = 0f

    override fun pageSetup(viewSize: Size<Int>, getPageCount: (Int) -> Unit, setupComplete: () -> Unit) {
        Single.fromCallable {
            val pageCount = pdfReadable.getPageCount()
            getPageCount(pageCount)

            for (i in 0 until pageCount) {
                val pageSize: Size<Int> = pdfReadable.getPageSize(i)
                if (pageSize.width > maxPageSize.width) {
                    maxPageSize = pageSize
                }
                if (pageSize.height > maxPageSize.height) {
                    maxPageSize = pageSize
                }

                originalPageSizes.add(pageSize)
            }
        }.observeOn(Schedulers.single())
            .subscribeBy {
                calcPageFitSize(viewSize)
                setupComplete()
            }.addTo(compositeDisposable)
    }

    private fun calcPageFitSize(viewSize: Size<Int>) {
        pageSizes.clear()

        val pageSizeOptimizeManager =
            PageSizeOptimizeManagerImpl(maxPageSize, viewSize).apply {
                calcMaxSize()
            }
        fitPageWidth = pageSizeOptimizeManager.getFitPageWidth()
        fitPageHeight = pageSizeOptimizeManager.getFitPageHeight()

        originalPageSizes.forEach {
            pageSizes.add(pageSizeOptimizeManager.calculateCurrentViewSizeFitDocument(it))
        }

        documentLength = getDocumentLength()
        preparePagesOffset()
    }

    private fun getDocumentLength(): Float {
        val pageCount = pdfReadable.getPageCount()
        var documentLength = 0f

        for (i in 0 until pageCount) {
            val pageSize = pageSizes[i]
            documentLength += pageSize.height

            if (i < pageCount - 1) {
                documentLength += defaultSetting.defaultDocumentSpacing
            }
        }

        return documentLength
    }

    private fun preparePagesOffset() {
        pageOffsets.clear()
        var offset = 0f
        val pageCount = pdfReadable.getPageCount()

        for (i in 0 until pageCount) {
            val pageSize = pageSizes[i]
            val height = pageSize.height

            offset += height + defaultSetting.defaultDocumentSpacing
            pageOffsets.add(offset)
        }
    }

}