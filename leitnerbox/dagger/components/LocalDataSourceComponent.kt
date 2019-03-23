package com.kecsot.leitnerbox.dagger.components

import com.kecsot.leitnerbox.dagger.modules.LocalDataSourceModule
import com.kecsot.leitnerbox.repository.CardRepository
import com.kecsot.leitnerbox.repository.DeckRepository
import com.kecsot.leitnerbox.repository.ImageRepository
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [LocalDataSourceModule::class])
interface LocalDataSourceComponent {

    fun inject(repository: DeckRepository)

    fun inject(repository: CardRepository)

    fun inject(imageRepository: ImageRepository)

}