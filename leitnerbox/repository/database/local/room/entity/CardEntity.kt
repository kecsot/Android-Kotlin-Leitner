package com.kecsot.leitnerbox.repository.database.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.kecsot.leitnerbox.repository.database.local.room.converter.ImageItemTypeConverter
import com.kecsot.leitnerbox.repository.database.local.room.model.ImageItem


@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var frontText: String,
    var backText: String,

    @TypeConverters(ImageItemTypeConverter::class) var frontImagePathList: ArrayList<ImageItem>,
    @TypeConverters(ImageItemTypeConverter::class) var backImagePathList: ArrayList<ImageItem>,
    var deckId: Long,


    var leitnerBoxLevel: Int,
    var leitnerBoxLevelSetAt: Long,

    var created: Long
)