package com.kecsot.leitnerbox.repository.database.local

import com.kecsot.leitnerbox.repository.database.local.room.AppDatabase
import com.kecsot.leitnerbox.repository.database.local.room.entity.CardEntity
import com.kecsot.leitnerbox.repository.local.CardLocalDataSourceInterface
import io.reactivex.Single

// FIXME: BaseLocalDataSource? insert, delete, update
class CardLocalDataSource : CardLocalDataSourceInterface {

    // FIXME inject
    private val cardDao = AppDatabase.getInstance().getCardDao()

    override fun getAll(): Single<List<CardEntity>> {
        return cardDao.getAll()
    }

    override fun getAllByDeckId(deckId: Long): Single<List<CardEntity>> {
        return cardDao.getAllByDeckId(deckId)
    }

    override fun getCardById(id: Long): Single<CardEntity> {
        return cardDao.getById(id)
    }

    override fun countCardsByDeckId(deckId: Long): Single<Int> {
        return Single.fromCallable {
            cardDao.countCardsByDeckId(deckId)
        }
    }

    override fun countDueCardsInDeckByLevel(
        deckId: Long,
        boxLevel: Int,
        boxSpacedRepTime: Long
    ): Single<Int> {
        return Single.fromCallable {
            cardDao.countDueCardsInDeckByLevel(deckId, boxLevel, boxSpacedRepTime, System.currentTimeMillis())
        }
    }

    override fun getDueCardsInDeckByLevel(
        deckId: Long,
        boxLevel: Int,
        boxSpacedRepTime: Long
    ): Single<List<CardEntity>> {
        return cardDao.getDueCardsInDeckByLevel(deckId, boxLevel, boxSpacedRepTime, System.currentTimeMillis())
    }

    override fun insertCard(cardEntity: CardEntity): Single<Boolean> {
        return Single.fromCallable {
            cardDao.insert(cardEntity)
        }.map {
            it != 0L
        }
    }

    override fun updateCard(cardEntity: CardEntity): Single<Boolean> {
        return Single.fromCallable {
            cardDao.update(cardEntity)
        }.map {
            it != 0
        }
    }

    override fun deleteCardById(id: Long): Single<Boolean> {
        return Single.fromCallable {
            cardDao.deleteById(id)
        }.map {
            it != 0
        }
    }

}