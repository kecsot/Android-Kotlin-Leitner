package com.kecsot.leitnerbox.repository.database.local.room.model

import java.util.*

data class LeitnerBoxRule(
    val level: Int,
    var spaceRepetitionTime: Long,
    val uid: String = UUID.randomUUID().toString()
)