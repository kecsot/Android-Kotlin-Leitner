package com.kecsot.leitnerbox.repository

import com.kecsot.leitnerbox.application.LeitnerBoxApplication
import com.kecsot.leitnerbox.repository.database.local.room.entity.DeckEntity
import com.kecsot.leitnerbox.repository.local.DeckLocalDataSourceInterface
import io.reactivex.Single
import javax.inject.Inject

class DeckRepository  {

    @Inject
    lateinit var deckLocalDataSource: DeckLocalDataSourceInterface

    init {
        LeitnerBoxApplication.instance.localDataSourceComponent.inject(this)
    }

    fun getAll(): Single<List<DeckEntity>> {
        return deckLocalDataSource.getAll()
    }

    fun getDeckById(id: Long): Single<DeckEntity> {
        return deckLocalDataSource.getDeckById(id)
    }

    fun insertDeck(deckEntity: DeckEntity): Single<Long> {
        return deckLocalDataSource.insertDeck(deckEntity)
    }

    fun insertDeckEntity(deckEntity: DeckEntity): Single<Boolean> {
        return deckLocalDataSource.insertDeck(deckEntity).map {
            it != 0L
        }
    }

    fun updateDeck(deckEntity: DeckEntity): Single<Boolean> {
        return deckLocalDataSource.updateDeck(deckEntity)
    }

    fun deleteDeckById(id: Long): Single<Boolean> {
        return deckLocalDataSource.deleteDeckById(id)
    }

}