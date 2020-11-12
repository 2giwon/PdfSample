package kr.eg.egiwon.pdfsample.pdfview.loader

import io.reactivex.Observable
import kr.eg.egiwon.pdfsample.pdfview.render.model.PagePart
import kr.eg.egiwon.pdfsample.util.Size

interface PageLoadable {

    fun loadPages(viewSize: Size<Int>): Observable<PagePart>
}