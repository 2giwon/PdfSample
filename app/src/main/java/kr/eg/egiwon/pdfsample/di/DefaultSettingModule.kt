package kr.eg.egiwon.pdfsample.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kr.eg.egiwon.pdfsample.util.DefaultSetting

@Module
@InstallIn(ActivityRetainedComponent::class)
class DefaultSettingModule {

    @ActivityRetainedScoped
    @Provides
    fun provideDefaultSettingProvider(
        @ApplicationContext applicationContext: Context
    ): DefaultSetting = DefaultSetting(applicationContext)
}