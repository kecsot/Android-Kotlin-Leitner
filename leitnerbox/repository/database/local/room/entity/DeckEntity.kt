package com.kecsot.leitnerbox.repository.database.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.kecsot.leitnerbox.repository.database.local.room.converter.LeitnerBoxRulesTypeConverter
import com.kecsot.leitnerbox.repository.database.local.room.model.LeitnerBoxRule

@Entity(tableName = "decks")
data class DeckEntity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,

    var name: String,

    var description: String,

    @TypeConverters(LeitnerBoxRulesTypeConverter::class) var deckLeitnerBoxRules: ArrayList<LeitnerBoxRule>

)