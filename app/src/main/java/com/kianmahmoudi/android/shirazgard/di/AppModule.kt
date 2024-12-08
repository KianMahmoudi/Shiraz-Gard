package com.kianmahmoudi.android.shirazgard.di

import com.kianmahmoudi.android.shirazgard.api.WeatherApi
import com.kianmahmoudi.android.shirazgard.repository.HomeRepository
import com.kianmahmoudi.android.shirazgard.repository.ParseHomeRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

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
    fun provideHotelRepository(): HomeRepository {
        return ParseHomeRepository()
    }

}
