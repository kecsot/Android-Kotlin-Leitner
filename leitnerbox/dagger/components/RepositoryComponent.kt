package com.kecsot.leitnerbox.dagger.components

import com.kecsot.leitnerbox.dagger.modules.RepositoryModule
import com.kecsot.leitnerbox.view.card.CardDetailsViewModel
import com.kecsot.leitnerbox.view.cardlist.CardListViewModel
import com.kecsot.leitnerbox.view.deck.DeckDetailsViewModel
import com.kecsot.leitnerbox.view.decklist.DeckListViewModel
import com.kecsot.leitnerbox.view.learn.ChooseModeViewModel
import com.kecsot.leitnerbox.view.learn.LearnViewModel
import com.kecsot.leitnerbox.view.splash.SplashViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [RepositoryModule::class])
interface RepositoryComponent {

    fun inject(viewModel: DeckListViewModel)

    fun inject(viewModel: DeckDetailsViewModel)

    fun inject(cardListViewModel: CardListViewModel)

    fun inject(cardDetailsViewModel: CardDetailsViewModel)

    fun inject(learnViewModel: LearnViewModel)

    fun inject(splashViewModel: SplashViewModel)

    fun inject(chooseModeViewModel: ChooseModeViewModel)

}