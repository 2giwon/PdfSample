package kr.eg.egiwon.pdfsample.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kr.eg.egiwon.pdfsample.pdfcore.PdfCore
import kr.eg.egiwon.pdfsample.pdfcore.PdfCoreAction

@Module
@InstallIn(ActivityRetainedComponent::class)
object PdfCoreModule {

    @ActivityRetainedScoped
    @Provides
    fun providePdfCore(
        @ApplicationContext applicationContext: Context
    ): PdfCoreAction = PdfCore(applicationContext)
}