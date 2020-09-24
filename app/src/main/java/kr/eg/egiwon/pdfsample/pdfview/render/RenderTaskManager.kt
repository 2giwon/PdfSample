package kr.eg.egiwon.pdfsample.pdfview.render

import kr.eg.egiwon.pdfsample.pdfview.render.model.PagePart
import kr.eg.egiwon.pdfsample.pdfview.render.model.RenderTask

interface RenderTaskManager {
    fun makePagePart(task: RenderTask): PagePart?
}