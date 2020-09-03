package kr.eg.egiwon.pdfsample.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn

import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kr.eg.egiwon.pdfsample.util.DefaultSetting
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class DefaultSettingModule {

    @Singleton
    @Provides
    fun provideDefaultSettingProvider(
        @ApplicationContext applicationContext: Context
    ): DefaultSetting = DefaultSetting(applicationContext)
}