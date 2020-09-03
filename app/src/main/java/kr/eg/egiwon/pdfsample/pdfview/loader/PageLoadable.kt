package kr.eg.egiwon.pdfsample.pdfview.loader

import kr.eg.egiwon.pdfsample.util.Size

interface PageLoadable {

    fun loadPages(viewSize: Size<Int>)
}