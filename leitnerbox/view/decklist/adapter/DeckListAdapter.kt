package com.kecsot.leitnerbox.view.decklist.adapter

import android.view.View
import com.jakewharton.rxbinding3.view.clicks
import com.kecsot.basekecsot.adapter.BaseDiffUtilCallback
import com.kecsot.basekecsot.adapter.BaseListAdapter
import com.kecsot.leitnerbox.R
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.listitem_fragment_decklist.view.*

class DeckListAdapter : BaseListAdapter<DeckListItem, BaseDiffUtilCallback<DeckListItem>>(DeckListDiffUtilCallback()) {

    public val onItemEditButtonClickedPublishSubject = PublishSubject.create<DeckListItem>()
    public val onLearnButtonClickedPublishSubject = PublishSubject.create<DeckListItem>()

    override fun getLayoutId(): Int {
        return R.layout.listitem_fragment_decklist
    }

    override fun onBindViewHolder(holder: ViewHolder, item: DeckListItem) {
        holder.view.apply {
            // Texts
            val formattedAllCardsText = this.context.getString(
                R.string.listitem_fragment_decklist_deck_count_template_text,
                item.allCardCount
            )

            val formattedDueCardsText = this.context.getString(
                R.string.listitem_fragment_decklist_deck_due_count_template_text,
                item.allDueCardCount
            )

            listitem_fragment_decklist_deck_deckname_text.text = item.deckName
            listitem_fragment_decklist_deck_description_text.text = item.description
            listitem_fragment_decklist_deck_count_text.text = formattedAllCardsText
            listitem_fragment_decklist_deck_due_count_text.text = formattedDueCardsText


            // Visibilities
            val isDueCardFound = item.allDueCardCount != 0
            val isAtLeastOneCardFound = item.allCardCount != 0
            val isButtonVisible = isDueCardFound || isAtLeastOneCardFound
            listitem_fragment_decklist_deck_learn_button.visibility = if (isButtonVisible) {
                View.VISIBLE
            } else {
                View.GONE
            }


            // Clicks
            listitem_fragment_decklist_deck_view_button
                .clicks()
                .map {
                    item
                }
                .subscribe(onItemEditButtonClickedPublishSubject)

            listitem_fragment_decklist_deck_learn_button
                .clicks()
                .map {
                    item
                }
                .subscribe(onLearnButtonClickedPublishSubject)
        }
    }

}