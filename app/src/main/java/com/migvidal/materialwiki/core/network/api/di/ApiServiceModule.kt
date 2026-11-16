package com.migvidal.materialwiki.core.network.api.di

import com.migvidal.materialwiki.BuildConfig
import com.migvidal.materialwiki.core.network.api.api_service.FakeOfflineApi
import com.migvidal.materialwiki.core.network.api.api_service.NetworkApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ApiServiceModule {
    @Provides
    fun providesApiService(fakeApi: FakeOfflineApi, networkApi: NetworkApi) =
        if (BuildConfig.FLAVOR.contentEquals("mocked")) {
            fakeApi
        } else {
            networkApi
        }
}