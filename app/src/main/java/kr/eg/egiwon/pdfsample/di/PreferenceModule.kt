package kr.eg.egiwon.pdfsample.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kr.eg.egiwon.pdfsample.data.source.preference.FileBrowserPreferenceService
import kr.eg.egiwon.pdfsample.data.source.preference.PreferenceService
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class PreferenceModule {

    @Singleton
    @Provides
    fun providesPreferenceService(
        @ApplicationContext applicationContext: Context
    ): PreferenceService =
        FileBrowserPreferenceService(applicationContext)
}