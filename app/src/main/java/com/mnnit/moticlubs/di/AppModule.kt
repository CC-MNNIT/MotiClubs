package com.mnnit.moticlubs.di

import com.google.gson.GsonBuilder
import com.mnnit.moticlubs.Constants
import com.mnnit.moticlubs.network.ApiService
import com.mnnit.moticlubs.network.Repository
import com.mnnit.moticlubs.network.RepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApiService(): ApiService {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .build()
            )
            .build()
            .create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideRepository(apiService: ApiService): Repository =
        RepositoryImpl(apiService = apiService)
}
