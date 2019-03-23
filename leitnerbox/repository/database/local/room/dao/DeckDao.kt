package com.kecsot.leitnerbox.repository.database.local.room.dao

import androidx.room.*
import com.kecsot.leitnerbox.repository.database.local.room.entity.CardEntity
import com.kecsot.leitnerbox.repository.database.local.room.entity.DeckEntity
import io.reactivex.Single


@Dao
interface DeckDao {

    @Query("SELECT * from decks ORDER BY id ASC")
    fun getAll(): Single<List<DeckEntity>>

    @Query("SELECT * FROM decks WHERE id = :deckId LIMIT 1")
    fun getById(deckId: Long): Single<DeckEntity>

    @Insert
    fun insert(item: DeckEntity): Long

    @Update
    fun update(item: DeckEntity): Int

    @Delete
    fun delete(card: CardEntity)

    @Query("DELETE FROM decks WHERE id = :id")
    fun deleteById(id: Long): Int
}
