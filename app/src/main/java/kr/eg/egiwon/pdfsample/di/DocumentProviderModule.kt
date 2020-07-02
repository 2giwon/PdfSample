package kr.eg.egiwon.pdfsample.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kr.eg.egiwon.pdfsample.util.DocumentProvider
import kr.eg.egiwon.pdfsample.util.DocumentProviderImpl

@Module
@InstallIn(ActivityRetainedComponent::class)
class DocumentProviderModule {

    @ActivityRetainedScoped
    @Provides
    fun provideDocumentProvider(
        @ApplicationContext applicationContext: Context
    ): DocumentProvider = DocumentProviderImpl(applicationContext)
}