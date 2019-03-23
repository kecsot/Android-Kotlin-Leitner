package com.kecsot.leitnerbox.repository.local

import com.kecsot.leitnerbox.repository.database.local.room.entity.CardEntity
import io.reactivex.Single

interface CardLocalDataSourceInterface {

    fun getAll(): Single<List<CardEntity>>

    fun getAllByDeckId(deckId: Long): Single<List<CardEntity>>

    fun getCardById(id: Long): Single<CardEntity>

    fun countCardsByDeckId(deckId: Long): Single<Int>

    fun countDueCardsInDeckByLevel(
        deckId: Long,
        boxLevel: Int,
        boxSpacedRepTime: Long
    ): Single<Int>

    fun getDueCardsInDeckByLevel(
        deckId: Long,
        boxLevel: Int,
        boxSpacedRepTime: Long
    ): Single<List<CardEntity>>

    fun insertCard(cardEntity: CardEntity): Single<Boolean>

    fun updateCard(cardEntity: CardEntity): Single<Boolean>

    fun deleteCardById(id: Long): Single<Boolean>

}