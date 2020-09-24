package kr.eg.egiwon.pdfsample.pdfview.loader

import io.reactivex.Single
import kr.eg.egiwon.pdfsample.pdfview.render.model.RenderTask
import kr.eg.egiwon.pdfsample.util.Size

interface PageLoadable {

    fun loadPages(viewSize: Size<Int>): Single<List<RenderTask>>
}