package kr.eg.egiwon.pdfsample.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kr.eg.egiwon.pdfsample.pdfcore.PdfCore
import kr.eg.egiwon.pdfsample.pdfcore.PdfReadable

@Module
@InstallIn(ActivityRetainedComponent::class)
class PdfCoreModule {

    @ActivityRetainedScoped
    @Provides
    fun providePdfCore(
        @ApplicationContext applicationContext: Context
    ): PdfReadable = PdfCore(applicationContext)
}