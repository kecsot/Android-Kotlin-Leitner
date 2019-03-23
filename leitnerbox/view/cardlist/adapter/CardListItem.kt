package com.kecsot.leitnerbox.view.cardlist.adapter

data class CardListItem(
    val id: Long,
    val frontText: String,
    val backText: String,
    val leitnerBoxLevel: Int,
    val dueDate: Long,
    val isFinished: Boolean
)

