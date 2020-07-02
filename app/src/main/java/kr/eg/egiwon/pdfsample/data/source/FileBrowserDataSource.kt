package kr.eg.egiwon.pdfsample.data.source

import io.reactivex.Single

interface FileBrowserDataSource {
    fun saveRootUri(uri: String)

    fun loadRootUri(): Single<String>
}