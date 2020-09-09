package kr.eg.egiwon.pdfsample.pdfview.setup

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
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

    private val pageCount: Int by lazy(pdfReadable::getPageCount)

    private var zoom = 1f

    override fun pageSetup(viewSize: Size<Int>, onPageCount: (Int) -> Unit, setupComplete: () -> Unit) {
        Single.fromCallable {
            onPageCount(pageCount)
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
        }.subscribeOn(Schedulers.single())
            .map { calcPageFitSize(viewSize) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                setupComplete()
            }.addTo(compositeDisposable)
    }

    override fun getPageAtOffset(offset: Float): Int {
        var currentPage = 0
        for (i in 0 until getCurrentDocumentPageCount()) {
            val tempOffset: Float = pageOffsets[i] * zoom - getPageSpacing(zoom) / 2f
            if (tempOffset >= offset) {
                break
            }

            currentPage++
        }
        return if (--currentPage >= 0) currentPage else 0
    }

    override fun getCurrentDocumentPageCount(): Int = pageCount

    override fun getPageZoom(): Float = zoom

    override fun getPageOffset(pageIndex: Int): Float = pageOffsets[pageIndex] * zoom

    override fun getScaledPageSize(pageIndex: Int): Size<Float> =
        Size(pageSizes[pageIndex].width * zoom, pageSizes[pageIndex].height * zoom)

    override fun getPageSize(pageIndex: Int): Size<Float> = pageSizes[pageIndex]

    override fun getSecondaryOffset(pageIndex: Int): Float =
        zoom * (fitPageWidth.width - getPageSize(pageIndex).width) / 2

    override fun getFitWidth(): Float {
        return fitPageWidth.width
    }

    override fun getFitHeight(): Float {
        return fitPageWidth.height
    }

    private fun getPageSpacing(zoom: Float): Float = defaultSetting.defaultDocumentSpacing * zoom

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

            pageOffsets.add(offset)
            offset += height + defaultSetting.defaultDocumentSpacing
        }
    }


}