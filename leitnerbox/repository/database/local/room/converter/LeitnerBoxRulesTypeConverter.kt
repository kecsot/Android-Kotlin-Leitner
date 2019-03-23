package com.kecsot.leitnerbox.repository.database.local.room.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.kecsot.leitnerbox.repository.database.local.room.model.LeitnerBoxRule


public class LeitnerBoxRulesTypeConverter {

    @TypeConverter
    fun fromString(value: String): ArrayList<LeitnerBoxRule> {
        return ArrayList(Gson().fromJson<Array<LeitnerBoxRule>>(value, Array<LeitnerBoxRule>::class.java).toMutableList())
    }

    @TypeConverter
    fun fromArrayList(list: ArrayList<LeitnerBoxRule>): String {
        return Gson().toJson(list)
    }
}