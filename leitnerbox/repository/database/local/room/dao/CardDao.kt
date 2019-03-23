package com.kecsot.leitnerbox.repository.database.local.room.dao


import androidx.room.*
import com.kecsot.leitnerbox.repository.database.local.room.entity.CardEntity
import io.reactivex.Single


@Dao
interface CardDao {

    @Query("SELECT * FROM cards WHERE id = :deckId LIMIT 1")
    fun getById(deckId: Long): Single<CardEntity>

    @Query("SELECT * from cards ORDER BY id ASC")
    fun getAll(): Single<List<CardEntity>>

    @Query("SELECT * from cards WHERE deckId = :deckId ORDER BY id ASC")
    fun getAllByDeckId(deckId: Long): Single<List<CardEntity>>

    @Query("SELECT count(*) FROM cards WHERE deckId = :deckId")
    fun countCardsByDeckId(deckId: Long) : Int

    @Query("SELECT count(*) FROM cards WHERE deckId = :deckId AND leitnerBoxLevel = :boxLevel AND leitnerBoxLevelSetAt + :spacedRepMs < :dateTimeNow")
    fun countDueCardsInDeckByLevel(deckId: Long, boxLevel: Int, spacedRepMs: Long, dateTimeNow: Long) : Int

    @Query("SELECT * FROM cards WHERE deckId = :deckId AND leitnerBoxLevel = :boxLevel AND leitnerBoxLevelSetAt + :spacedRepMs < :dateTimeNow")
    fun getDueCardsInDeckByLevel(deckId: Long, boxLevel: Int, spacedRepMs: Long, dateTimeNow: Long) : Single<List<CardEntity>>

    @Insert
    fun insert(card: CardEntity): Long

    @Update
    fun update(card: CardEntity): Int

    @Delete
    fun delete(card: CardEntity)

    @Query("DELETE FROM cards WHERE id = :id")
    fun deleteById(id: Long): Int

    @Query("DELETE FROM cards WHERE deckId = :id")
    fun deleteAllByDeckId(id: Long): Int

}
