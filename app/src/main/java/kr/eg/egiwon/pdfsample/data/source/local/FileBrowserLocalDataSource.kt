package kr.eg.egiwon.pdfsample.data.source.local

import io.reactivex.Single
import kr.eg.egiwon.pdfsample.data.source.FileBrowserDataSource
import kr.eg.egiwon.pdfsample.data.source.preference.PreferenceService
import javax.inject.Inject

class FileBrowserLocalDataSource @Inject constructor(
    private val preferenceService: PreferenceService
) : FileBrowserDataSource {

    override fun saveRootUri(uri: String) = preferenceService.saveRootUriToPreference(uri)

    override fun loadRootUri(): Single<String> = preferenceService.loadRootUriFromPreference()
}