package kr.eg.egiwon.pdfsample.util

class Size(
    val width: Int,
    val height: Int
) {

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }

        if (this === other) {
            return true
        }

        if (other is Size) {
            val obj = other
            return width == obj.width && height == obj.height
        }

        return false
    }

    override fun hashCode(): Int {
        return height xor (width shl Integer.SIZE / 2 or (width ushr Integer.SIZE / 2))
    }

    override fun toString(): String {
        return "$width x $height"
    }
}