package com.kianmahmoudi.android.shirazgard.di

import android.content.Context
import com.kianmahmoudi.android.shirazgard.api.WeatherApi
import com.kianmahmoudi.android.shirazgard.repository.MainDataRepository
import com.kianmahmoudi.android.shirazgard.repository.MainDataRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://haji-api.ir/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherApi(retrofit: Retrofit): WeatherApi {
        return retrofit.create(WeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMainDataRepository(): MainDataRepository {
        return MainDataRepositoryImpl()
    }

}
