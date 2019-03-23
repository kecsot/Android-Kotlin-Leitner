package com.kecsot.leitnerbox.view.decklist.adapter

import com.kecsot.basekecsot.adapter.BaseDiffUtilCallback

class DeckListDiffUtilCallback : BaseDiffUtilCallback<DeckListItem>() {

    override fun areItemsTheSame(oldItem: DeckListItem, newItem: DeckListItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DeckListItem, newItem: DeckListItem): Boolean {
        return oldItem.deckName == newItem.deckName &&
                oldItem.description == newItem.description &&
                oldItem.allCardCount == newItem.allCardCount &&
                oldItem.allDueCardCount == newItem.allDueCardCount
    }

}