package com.kecsot.leitnerbox.view.learn

import com.kecsot.leitnerbox.repository.database.local.room.entity.CardEntity

data class LearnModel(
    val card : CardEntity,
    var state: CardState
)


enum class CardState{
    READY,
    FAILED,
    REPEAT,
    DONE
}