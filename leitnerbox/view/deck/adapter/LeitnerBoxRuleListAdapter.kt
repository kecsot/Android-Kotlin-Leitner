package com.kecsot.leitnerbox.view.deck.adapter

import android.view.View
import com.jakewharton.rxbinding3.view.clicks
import com.kecsot.basekecsot.adapter.BaseDiffUtilCallback
import com.kecsot.basekecsot.adapter.BaseListAdapter
import com.kecsot.leitnerbox.R
import com.kecsot.leitnerbox.common.util.LeitnerBoxTimeFormatUtil
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.listitem_fragment_deck_details_leitnerboxrule.view.*

class LeitnerBoxRuleListAdapter(private var mode: LeitnerBoxRuleAdapterMode = LeitnerBoxRuleAdapterMode.VIEW) :
    BaseListAdapter<LeitnerBoxRuleListItem, BaseDiffUtilCallback<LeitnerBoxRuleListItem>>(
        LeitnerBoxRuleListItemListDiffUtilCallback()
    ) {

    enum class LeitnerBoxRuleAdapterMode {
        VIEW,
        EDIT
    }

    public fun setNewMode(newMode: LeitnerBoxRuleAdapterMode) {
        if (mode != newMode) {
            this.mode = newMode
            notifyDataSetChanged()
        }
    }

    public val onItemEditButtonClickedPublishSubject = PublishSubject.create<LeitnerBoxRuleListItem>()

    override fun getLayoutId(): Int {
        return R.layout.listitem_fragment_deck_details_leitnerboxrule
    }

    override fun onBindViewHolder(holder: ViewHolder, item: LeitnerBoxRuleListItem) {

        holder.view.run {
            val levelText =
                this.context.getString(
                    R.string.listitem_fragment_deck_details_leitnerboxrule_level_template_text,
                    item.level
                )

            val formattedLeitnerBoxRule =
                LeitnerBoxTimeFormatUtil.formatRuleTime(context, item.spaceRepetitionTime, false)

            listitem_fragment_deck_details_leitnerboxrule_level_field.text = levelText
            listitem_fragment_deck_details_leitnerboxrule_due_field.text = formattedLeitnerBoxRule

            if (mode == LeitnerBoxRuleAdapterMode.VIEW) {
                listitem_fragment_deck_details_leitnerboxrule_edit_button.visibility = View.INVISIBLE
            } else {
                listitem_fragment_deck_details_leitnerboxrule_edit_button.visibility = View.VISIBLE
            }

            listitem_fragment_deck_details_leitnerboxrule_edit_button
                .clicks()
                .map {
                    item
                }
                .subscribe(onItemEditButtonClickedPublishSubject)
        }
    }

}

