package com.kianmahmoudi.android.shirazgard.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.kianmahmoudi.android.shirazgard.api.WeatherApi
import com.kianmahmoudi.android.shirazgard.repository.FavoritePlacesRepository
import com.kianmahmoudi.android.shirazgard.repository.FavoritePlacesRepositoryImpl
import com.kianmahmoudi.android.shirazgard.repository.MainDataRepository
import com.kianmahmoudi.android.shirazgard.repository.MainDataRepositoryImpl
import com.kianmahmoudi.android.shirazgard.repository.UserRepository
import com.kianmahmoudi.android.shirazgard.repository.UserRepositoryImpl
import com.kianmahmoudi.android.shirazgard.viewmodel.SettingViewModel
import com.kianmahmoudi.android.shirazgard.viewmodel.UserViewModel
import com.kianmahmoudi.android.shirazgard.viewmodel.dataStore
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

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return appContext.dataStore
    }

    @Provides
    @Singleton
    fun provideUserRepository(@ApplicationContext appContext: Context): UserRepository {
        return UserRepositoryImpl(appContext)
    }

    @Provides
    @Singleton
    fun provideSettingViewModel(
        dataStore: DataStore<Preferences>
    ): SettingViewModel {
        return SettingViewModel(dataStore)
    }

    @Provides
    @Singleton
    fun provideUserViewModel(
        userRepository: UserRepository
    ): UserViewModel {
        return UserViewModel(userRepository)
    }

    @Provides
    @Singleton
    fun provideFavoritePlacesRepository():FavoritePlacesRepository{
        return FavoritePlacesRepositoryImpl()
    }

}