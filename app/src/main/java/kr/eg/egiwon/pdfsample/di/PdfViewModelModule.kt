package kr.eg.egiwon.pdfsample.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import kr.eg.egiwon.pdfsample.pdfcore.PdfCore
import kr.eg.egiwon.pdfsample.pdfview.PdfViewActivity
import kr.eg.egiwon.pdfsample.pdfview.PdfViewModel
import kr.eg.egiwon.pdfsample.util.DefaultSetting

@Module
@InstallIn(ActivityComponent::class)
class PdfViewModelModule {

    @ActivityScoped
    @Provides
    fun providePdfViewModel(
        @ActivityContext context: Context
    ): PdfViewModel =
        @Suppress("UNCHECKED_CAST")
        ViewModelProvider(context as PdfViewActivity, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return PdfViewModel(
                    PdfCore(context),
                    DefaultSetting(context)
                ) as T
            }
        }).get(PdfViewModel::class.java)
}