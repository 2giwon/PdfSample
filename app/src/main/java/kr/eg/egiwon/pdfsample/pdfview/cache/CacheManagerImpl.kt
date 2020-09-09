package kr.eg.egiwon.pdfsample.pdfview.cache

import kr.eg.egiwon.pdfsample.pdfview.render.model.PagePart
import kr.eg.egiwon.pdfsample.util.DefaultSetting
import java.util.*

class CacheManagerImpl : CacheManager {

    private val orderComparator: PagePartComparator = PagePartComparator()

    private val passiveCache: PriorityQueue<PagePart> =
        PriorityQueue(DefaultSetting.CACHE_SIZE, orderComparator)

    private val activeCache: PriorityQueue<PagePart> =
        PriorityQueue(DefaultSetting.CACHE_SIZE, orderComparator)

    private val lock = Any()

    override fun cachePart(part: PagePart) {
        synchronized(lock) {
            makeFreeSpace()
            activeCache.offer(part)
        }
    }

    override fun init() {
        synchronized(lock) {
            passiveCache.addAll(activeCache)
            activeCache.clear()
        }
    }

    override fun getPageParts(): List<PagePart> {
        synchronized(lock) {
            val parts = mutableListOf<PagePart>()
            parts.addAll(activeCache)
            return parts
        }
    }

    private fun makeFreeSpace() {
        synchronized(lock) {
            while (activeCache.size + passiveCache.size >= DefaultSetting.CACHE_SIZE &&
                !passiveCache.isEmpty()
            ) {
                val part = passiveCache.poll()
                part?.renderedPart?.recycle()
            }

            while (activeCache.size + passiveCache.size >= DefaultSetting.CACHE_SIZE &&
                !activeCache.isEmpty()
            ) {
                activeCache.poll()?.renderedPart?.recycle()
            }
        }
    }

    inner class PagePartComparator : Comparator<PagePart> {
        override fun compare(o1: PagePart, o2: PagePart): Int {
            if (o1.cacheOrder == o2.cacheOrder) {
                return 0
            }

            return if (o1.cacheOrder > o2.cacheOrder) 1 else -1
        }

    }
}