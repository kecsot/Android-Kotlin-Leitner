package com.kecsot.leitnerbox.repository

import com.kecsot.leitnerbox.application.LeitnerBoxApplication
import com.kecsot.leitnerbox.repository.database.local.room.entity.CardEntity
import com.kecsot.leitnerbox.repository.local.CardLocalDataSourceInterface
import com.kecsot.leitnerbox.repository.local.DeckLocalDataSourceInterface
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class CardRepository {

    @Inject
    lateinit var cardLocalDataSource: CardLocalDataSourceInterface

    @Inject
    lateinit var deckLocalDataSource: DeckLocalDataSourceInterface

    init {
        LeitnerBoxApplication.instance.localDataSourceComponent.inject(this)
    }

    fun getAll(): Single<List<CardEntity>> {
        return cardLocalDataSource.getAll()
    }

    fun getAllByDeckId(deckId: Long): Single<List<CardEntity>> {
        return cardLocalDataSource.getAllByDeckId(deckId)
    }

    fun getCardById(id: Long): Single<CardEntity> {
        return cardLocalDataSource.getCardById(id)
    }

    fun countCardsByDeckId(deckId: Long): Single<Int> {
        return cardLocalDataSource.countCardsByDeckId(deckId)
    }

    fun countDueCardsByDeckId(deckId: Long): Single<Int> {
        return deckLocalDataSource.getDeckById(deckId)
            .flatMap { deck ->
                return@flatMap Observable.fromIterable(deck.deckLeitnerBoxRules)
                    .flatMapSingle { rule ->
                        cardLocalDataSource.countDueCardsInDeckByLevel(
                            deckId,
                            rule.level,
                            rule.spaceRepetitionTime
                        )
                    }
                    .toList()
                    .map {
                        it.sum()
                    }
            }
    }

    fun getDueCardsByDeckId(deckId: Long): Single<List<CardEntity>> {
        return deckLocalDataSource.getDeckById(deckId)
            .flatMap { deck ->
                return@flatMap Observable.fromIterable(deck.deckLeitnerBoxRules)
                    .flatMapSingle { rule ->
                        cardLocalDataSource.getDueCardsInDeckByLevel(
                            deckId,
                            rule.level,
                            rule.spaceRepetitionTime
                        )
                    }
                    .toList()
                    .map {
                        it.flatten()
                    }
            }
    }

    fun insertCard(cardEntity: CardEntity): Single<Boolean> {
        return cardLocalDataSource.insertCard(cardEntity)
    }

    fun updateCard(cardEntity: CardEntity): Single<Boolean> {
        return cardLocalDataSource.updateCard(cardEntity)
    }

    fun deleteCardById(id: Long): Single<Boolean> {
        return cardLocalDataSource.deleteCardById(id)
    }

}