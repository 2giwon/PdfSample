package kr.eg.egiwon.pdfsample.pdfview.loader

import io.reactivex.Flowable
import kr.eg.egiwon.pdfsample.pdfview.render.model.RenderTask
import kr.eg.egiwon.pdfsample.util.Size

interface PageLoadable {

    fun loadPages(viewSize: Size<Int>): Flowable<RenderTask>
}