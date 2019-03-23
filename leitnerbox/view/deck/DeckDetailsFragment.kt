package com.kecsot.leitnerbox.view.deck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.kecsot.basekecsot.util.ValidationUtil
import com.kecsot.basekecsot.view.AbstractViewModel
import com.kecsot.domoftimepickerdialog.DomofAlertDialog
import com.kecsot.leitnerbox.R
import com.kecsot.leitnerbox.base.detail.*
import com.kecsot.leitnerbox.repository.database.local.room.entity.DeckEntity
import com.kecsot.leitnerbox.repository.database.local.room.model.LeitnerBoxRule
import com.kecsot.leitnerbox.view.deck.adapter.LeitnerBoxRuleListAdapter
import kotlinx.android.synthetic.main.fragment_deck_details.*

class DeckDetailsFragment : AbstractDetailsFragment<DeckEntity, DeckDetailsViewModel>() {

    private val MINIMUM_DECK_NAME_LENGTH = 2
    private val listAdapter = LeitnerBoxRuleListAdapter()

    override fun createViewModel(): DeckDetailsViewModel {
        return AbstractViewModel.get(this)
    }

    override fun getArgumentItemId(): Long {
        return DeckDetailsFragmentArgs.fromBundle(arguments).deckId
    }

    override fun getArgumentScreenType(): BaseDetailsFragmentScreenType {
        return DeckDetailsFragmentArgs.fromBundle(arguments).screenType
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_deck_details, container, false)
    }

    override fun onInitView() {
        fragment_deck_details_boxes_recyclerview.apply {
            adapter = listAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    override fun onSubscribeReactiveXObservables() {
        super.onSubscribeReactiveXObservables()

        fragment_deck_details_deckname_field
            .textChanges()
            .subscribe {
                viewModel.detailItem.value?.let { deck ->
                    deck.name = it.toString()
                }
            }
            .addToCompositeDisposable()

        fragment_deck_details_description_field
            .textChanges()
            .subscribe {
                viewModel.detailItem.value?.let { deck ->
                    deck.description = it.toString()
                }
            }
            .addToCompositeDisposable()

        viewModel.onDetailEvent
            .subscribe {
                when (it) {
                    is CreateEntityFragmentEvent,
                    is UpdateEntityFragmentEvent,
                    is DeleteEntityFragmentEvent -> {
                        if (it.isSuccess) {
                            navigateBack()
                        }
                    }
                }
            }
            .addToCompositeDisposable()

        fragment_deck_details_boxes_function_remove_image
            .clicks()
            .subscribe {
                viewModel.decreaseNumberOfLeitnerBoxRules()
            }
            .addToCompositeDisposable()

        fragment_deck_details_boxes_function_add_image
            .clicks()
            .subscribe {
                viewModel.increaseNumberOfLeitnerBoxRules()
            }
            .addToCompositeDisposable()

        listAdapter.onItemEditButtonClickedPublishSubject
            .subscribe {
                onEditLeitnerBoxRule(it)
            }
            .addToCompositeDisposable()
    }

    override fun onSubscribeViewModelObservables() {
        super.onSubscribeViewModelObservables()

        viewModel.run {
            numberOfLeitnerBoxes.observe(this@DeckDetailsFragment, Observer {
                fragment_deck_details_boxes_function_numberofrows_label.text = it.toString()
            })
        }
    }

    override fun isDetailInputsAreValid(): Boolean {
        return ValidationUtil.isLengthValidWithErrorText(
            fragment_deck_details_deckname_field_inputlayout,
            fragment_deck_details_deckname_field,
            MINIMUM_DECK_NAME_LENGTH
        )
    }

    override fun updateScreenByItem(item: DeckEntity) {
        fragment_deck_details_deckname_field.setText(item.name)
        fragment_deck_details_description_field.setText(item.description)

        updateLeitnerBoxRuleList(item.deckLeitnerBoxRules)
    }

    private fun updateLeitnerBoxRuleList(list: List<LeitnerBoxRule>) {
        listAdapter.submitList(list)
    }

    override fun updateScreenByScreenType(screenType: BaseDetailsFragmentScreenType) {
        // Title
        when (screenType) {
            BaseDetailsFragmentScreenType.VIEW -> setTitle(R.string.fragment_deck_details_view_title)
            BaseDetailsFragmentScreenType.EDIT -> setTitle(R.string.fragment_deck_details_edit_title)
            BaseDetailsFragmentScreenType.ADD -> setTitle(R.string.fragment_deck_details_add_title)
        }

        // Visibilities
        when (screenType) {
            BaseDetailsFragmentScreenType.VIEW -> {
                fragment_deck_details_boxes_function_button_group.visibility = View.INVISIBLE
                listAdapter.setNewMode(LeitnerBoxRuleListAdapter.LeitnerBoxRuleAdapterMode.VIEW)
            }
            BaseDetailsFragmentScreenType.EDIT, BaseDetailsFragmentScreenType.ADD -> {
                fragment_deck_details_boxes_function_button_group.visibility = View.VISIBLE
                listAdapter.setNewMode(LeitnerBoxRuleListAdapter.LeitnerBoxRuleAdapterMode.EDIT)
            }
        }

    }

    override fun setDetailFieldsEnable(isEnable: Boolean) {
        arrayListOf(
            fragment_deck_details_deckname_field,
            fragment_deck_details_description_field
        ).forEach {
            it.isEnabled = isEnable
        }
    }

    private fun onEditLeitnerBoxRule(rule: LeitnerBoxRule) {
        val title = getString(R.string.listitem_fragment_deck_details_leitnerboxrule_level_template_text, rule.level)

        val dialog = DomofAlertDialog.Builder(requireContext())
            .setCancelable(false)
            .setNegativeButtonText(R.string.base_general_cancel)
            .setPositiveButtonText(R.string.base_general_accept)
            .setTitle(title)
            .setDefaultTimeInMs(rule.spaceRepetitionTime)
            .build()

        dialog.onPositiveClickPublishSubject
            .subscribe {
                rule.spaceRepetitionTime = it.calculatedMilliseconds
                listAdapter.notifyDataSetChanged()
            }
            .addToCompositeDisposable()

        dialog.show()
    }

    override fun getItemId(item: DeckEntity): Long {
        return item.id
    }

    override fun getDeleteDialogTitle(): String {
        return getString(R.string.fragment_deck_details_alertdialog_delete_title_text)
    }

    override fun getDeleteDialogMessage(): String {
        return getString(R.string.fragment_deck_details_alertdialog_delete_message_text)
    }

}
