package com.kecsot.leitnerbox.view.decklist.adapter

data class DeckListItem(
        val id: Long,
        val deckName: String,
        val description: String,
        var allCardCount: Int,
        var allDueCardCount: Int
)

