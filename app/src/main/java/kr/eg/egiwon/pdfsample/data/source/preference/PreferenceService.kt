package kr.eg.egiwon.pdfsample.data.source.preference

import io.reactivex.Single

interface PreferenceService {
    fun saveRootUriToPreference(uri: String)

    fun loadRootUriFromPreference(): Single<String>
}