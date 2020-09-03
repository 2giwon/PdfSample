package kr.eg.egiwon.pdfsample.pdfview.model

data class RenderingRange(
    val page: Int = 0,
    val gridSize: GridSize = GridSize(0, 0),
    val leftTop: Holder = Holder(0, 0),
    val rightBottom: Holder = Holder(0, 0)
)

data class GridSize(
    val rows: Int,
    val cols: Int
)

data class Holder(
    val row: Int,
    val col: Int
)