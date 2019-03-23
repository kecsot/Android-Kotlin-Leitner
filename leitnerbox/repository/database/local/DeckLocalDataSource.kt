package com.kecsot.leitnerbox.repository.database.local

import com.kecsot.leitnerbox.repository.database.local.room.AppDatabase
import com.kecsot.leitnerbox.repository.database.local.room.entity.DeckEntity
import com.kecsot.leitnerbox.repository.local.DeckLocalDataSourceInterface
import io.reactivex.Single

// FIXME: BaseLocalDataSource?  insert, delete, update with map there
class DeckLocalDataSource : DeckLocalDataSourceInterface {

    // FIXME: inject
    private val deckDao = AppDatabase.getInstance().getDeckDao()

    override fun getAll(): Single<List<DeckEntity>> {
        return deckDao.getAll()
    }

    override fun getDeckById(id: Long): Single<DeckEntity> {
        return deckDao.getById(id)
    }

    override fun insertDeck(deckEntity: DeckEntity): Single<Long> {
        return Single.fromCallable {
            deckDao.insert(deckEntity)
        }
    }

    override fun updateDeck(deckEntity: DeckEntity): Single<Boolean> {
        return Single.fromCallable {
            deckDao.update(deckEntity)
        }.map {
            it != 0
        }

    }

    override fun deleteDeckById(id: Long): Single<Boolean> {
        return Single.fromCallable {
            deckDao.deleteById(id)
        }.map {
            it != 0
        }
    }


}