package com.kecsot.leitnerbox.repository.local

import com.kecsot.leitnerbox.repository.database.local.room.entity.DeckEntity
import io.reactivex.Single

interface DeckLocalDataSourceInterface  {

    fun getAll(): Single<List<DeckEntity>>

    fun getDeckById(id: Long): Single<DeckEntity>

    fun insertDeck(deckEntity: DeckEntity): Single<Long>

    fun updateDeck(deckEntity: DeckEntity): Single<Boolean>

    fun deleteDeckById(id: Long): Single<Boolean>

}