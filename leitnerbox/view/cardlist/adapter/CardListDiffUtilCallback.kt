package com.kecsot.leitnerbox.view.cardlist.adapter

import com.kecsot.basekecsot.adapter.BaseDiffUtilCallback

class CardListDiffUtilCallback : BaseDiffUtilCallback<CardListItem>() {

    override fun areItemsTheSame(oldItem: CardListItem, newItem: CardListItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CardListItem, newItem: CardListItem): Boolean {
        return oldItem.frontText == newItem.frontText &&
                oldItem.backText == newItem.backText
    }

}