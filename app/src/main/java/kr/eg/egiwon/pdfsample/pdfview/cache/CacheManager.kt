package kr.eg.egiwon.pdfsample.pdfview.cache

import kr.eg.egiwon.pdfsample.pdfview.render.model.PagePart

interface CacheManager {
    fun cachePart(part: PagePart)

    fun init()

    fun getPageParts(): List<PagePart>

}