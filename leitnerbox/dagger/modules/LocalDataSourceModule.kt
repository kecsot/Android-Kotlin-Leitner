package com.kecsot.leitnerbox.dagger.modules

import com.kecsot.leitnerbox.repository.database.local.CardLocalDataSource
import com.kecsot.leitnerbox.repository.database.local.DeckLocalDataSource
import com.kecsot.leitnerbox.repository.database.local.ImageLocalDataSource
import com.kecsot.leitnerbox.repository.local.CardLocalDataSourceInterface
import com.kecsot.leitnerbox.repository.local.DeckLocalDataSourceInterface
import com.kecsot.leitnerbox.repository.local.ImageLocalDataSourceInterface
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LocalDataSourceModule {

    @Provides
    @Singleton
    fun provideDeckLocalDataSource(): DeckLocalDataSourceInterface =
        DeckLocalDataSource()


    @Provides
    @Singleton
    fun provideCardLocalDataSource(): CardLocalDataSourceInterface =
        CardLocalDataSource()

    @Provides
    @Singleton
    fun provideFileLocalDataSource(): ImageLocalDataSourceInterface =
        ImageLocalDataSource()


}