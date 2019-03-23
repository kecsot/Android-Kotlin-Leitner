package com.kecsot.leitnerbox.view.deck.adapter

import com.kecsot.basekecsot.adapter.BaseDiffUtilCallback

class LeitnerBoxRuleListItemListDiffUtilCallback : BaseDiffUtilCallback<LeitnerBoxRuleListItem>() {

    override fun areItemsTheSame(oldItem: LeitnerBoxRuleListItem, newItem: LeitnerBoxRuleListItem): Boolean {
        return oldItem.uid == newItem.uid
    }

    override fun areContentsTheSame(oldItem: LeitnerBoxRuleListItem, newItem: LeitnerBoxRuleListItem): Boolean {
        return oldItem.level == newItem.level &&
                oldItem.spaceRepetitionTime == newItem.spaceRepetitionTime
    }

}