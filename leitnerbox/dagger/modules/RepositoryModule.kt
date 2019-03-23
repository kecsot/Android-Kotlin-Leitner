package com.kecsot.leitnerbox.dagger.modules

import com.kecsot.leitnerbox.repository.CardRepository
import com.kecsot.leitnerbox.repository.DeckRepository
import com.kecsot.leitnerbox.repository.ImageRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    private val cardRepository = CardRepository()

    @Provides
    @Singleton
    fun provideDeckRepository(): DeckRepository = DeckRepository()

    @Provides
    @Singleton
    fun provideCardRepository(): CardRepository = cardRepository

    @Provides
    @Singleton
    fun provideFileRepository(): ImageRepository = ImageRepository(cardRepository)

}