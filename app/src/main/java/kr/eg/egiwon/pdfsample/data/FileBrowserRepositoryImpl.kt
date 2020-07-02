package kr.eg.egiwon.pdfsample.data

import io.reactivex.Single
import kr.eg.egiwon.pdfsample.data.source.FileBrowserDataSource
import javax.inject.Inject

class FileBrowserRepositoryImpl @Inject constructor(
    private val dataSource: FileBrowserDataSource
) : FileBrowserRepository {
    override fun saveRootUri(uri: String) = dataSource.saveRootUri(uri)

    override fun loadRootUri(): Single<String> = dataSource.loadRootUri()

}