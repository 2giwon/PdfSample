package kr.eg.egiwon.pdfsample.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kr.eg.egiwon.pdfsample.data.source.FileBrowserDataSource
import kr.eg.egiwon.pdfsample.data.source.local.FileBrowserLocalDataSource
import kr.eg.egiwon.pdfsample.data.source.preference.PreferenceService
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class LocalDataSourceModule {

    @Singleton
    @Provides
    fun provideLocalFileBrowserDataSource(
        preferenceService: PreferenceService
    ): FileBrowserDataSource = FileBrowserLocalDataSource(preferenceService)
}