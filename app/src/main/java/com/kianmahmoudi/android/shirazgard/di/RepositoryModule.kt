package com.kianmahmoudi.android.shirazgard.di

import com.kianmahmoudi.android.shirazgard.repository.FavoritePlacesRepository
import com.kianmahmoudi.android.shirazgard.repository.FavoritePlacesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindFavoritePlacesRepository(
        impl: FavoritePlacesRepositoryImpl
    ): FavoritePlacesRepository
}