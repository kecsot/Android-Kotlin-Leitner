package com.kecsot.leitnerbox.repository.database.local.room.model

import java.util.*

data class ImageItem(
    val imagePath: String,
    val fileName: String,
    val createdAt: Long,

    val uid: String = UUID.randomUUID().toString()
)