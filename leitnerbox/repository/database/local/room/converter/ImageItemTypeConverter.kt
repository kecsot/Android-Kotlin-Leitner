package com.kecsot.leitnerbox.repository.database.local.room.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.kecsot.leitnerbox.repository.database.local.room.model.ImageItem


public class ImageItemTypeConverter {

    @TypeConverter
    fun fromString(value: String): ArrayList<ImageItem> {
        return ArrayList(Gson().fromJson<Array<ImageItem>>(value, Array<ImageItem>::class.java).toMutableList())
    }

    @TypeConverter
    fun fromArrayList(list: ArrayList<ImageItem>): String {
        return Gson().toJson(list)
    }
}