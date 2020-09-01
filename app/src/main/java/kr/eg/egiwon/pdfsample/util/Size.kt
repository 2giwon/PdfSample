package kr.eg.egiwon.pdfsample.util

class Size<T : Number>(
    val width: T,
    val height: T
) {

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }

        if (this === other) {
            return true
        }

        if (other is Size<*>) {
            return width == other.width && height == other.height
        }

        return false
    }

    override fun hashCode(): Int {
        if (height is Int && width is Int) {
            return height xor (width shl Integer.SIZE / 2 or (width ushr Integer.SIZE / 2))
        } else if (height is Float && width is Float) {
            return width.toBits() xor height.toBits()
        }

        return super.hashCode()
    }

    override fun toString(): String {
        return "$width x $height"
    }
}