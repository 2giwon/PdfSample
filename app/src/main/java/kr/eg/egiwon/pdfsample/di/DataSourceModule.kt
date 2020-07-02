package kr.eg.egiwon.pdfsample.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kr.eg.egiwon.pdfsample.data.FileBrowserRepository
import kr.eg.egiwon.pdfsample.data.FileBrowserRepositoryImpl
import kr.eg.egiwon.pdfsample.data.source.FileBrowserDataSource

@Module
@InstallIn(ActivityRetainedComponent::class)
class DataSourceModule {

    @ActivityRetainedScoped
    @Provides
    fun provideFileBrowserRepository(
        fileBrowserDataSource: FileBrowserDataSource
    ): FileBrowserRepository = FileBrowserRepositoryImpl(fileBrowserDataSource)

}