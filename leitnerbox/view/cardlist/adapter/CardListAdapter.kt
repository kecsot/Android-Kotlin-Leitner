package com.kecsot.leitnerbox.view.cardlist.adapter

import android.annotation.SuppressLint
import android.widget.TextView
import com.jakewharton.rxbinding3.view.clicks
import com.kecsot.basekecsot.adapter.BaseDiffUtilCallback
import com.kecsot.basekecsot.adapter.BaseListAdapter
import com.kecsot.leitnerbox.R
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.listitem_fragment_cardlist.view.*
import java.text.SimpleDateFormat


class CardListAdapter : BaseListAdapter<CardListItem, BaseDiffUtilCallback<CardListItem>>(CardListDiffUtilCallback()) {

    public val onItemEditButtonClickedPublishSubject = PublishSubject.create<CardListItem>()
    @SuppressLint("SimpleDateFormat")
    private val dueDateFormatter = SimpleDateFormat("MMMM dd (HH:mm)")

    override fun getLayoutId(): Int {
        return R.layout.listitem_fragment_cardlist
    }

    override fun onBindViewHolder(holder: ViewHolder, item: CardListItem) {
        holder.view.apply {
            listitem_fragment_cardlist_card_front_text.text = item.frontText
            listitem_fragment_cardlist_card_back_text.text = item.backText
            listitem_fragment_cardlist_leitner_level.text =
                this.context.getString(R.string.listitem_fragment_cardlist_leitnerlevel_template, item.leitnerBoxLevel)

            setDueDateTime(listitem_fragment_cardlist_duedate, item)

            listitem_fragment_cardlist_card_back_edit_button
                .clicks()
                .map {
                    item
                }
                .subscribe(onItemEditButtonClickedPublishSubject)
        }
    }

    private fun setDueDateTime(textView: TextView, item: CardListItem) {
        val context = textView.context
        val isDueNow = item.dueDate < System.currentTimeMillis()

        textView.text = when {
            item.isFinished -> context.getString(R.string.listitem_fragment_cardlist_duedate_never)
            isDueNow -> context.getString(R.string.listitem_fragment_cardlist_duedate_now)
            else -> context.getString(
                R.string.listitem_fragment_cardlist_duedate_template,
                dueDateFormatter.format(item.dueDate)
            )
        }
    }

}