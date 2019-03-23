package com.kecsot.leitnerbox.view.decklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.kecsot.basekecsot.view.AbstractFragment
import com.kecsot.basekecsot.view.AbstractViewModel
import com.kecsot.leitnerbox.R
import com.kecsot.leitnerbox.base.detail.BaseDetailsFragmentScreenType
import com.kecsot.leitnerbox.view.decklist.adapter.DeckListAdapter
import kotlinx.android.synthetic.main.fragment_decklist.*

class DeckListFragment : AbstractFragment() {

    private lateinit var viewModel: DeckListViewModel
    private val listAdapter = DeckListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = AbstractViewModel.get(this)
    }

    override fun onStart() {
        super.onStart()
        viewModel.updateDeckList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_decklist, container, false)
    }

    override fun onInitView() {
        setupFloatingActionButton(true, R.drawable.ic_add_white_24dp)
        setTitle(R.string.fragment_decklist_title)

        fragment_decklist_recyclerview.apply {
            setHasFixedSize(true)
            adapter = listAdapter
        }

        fragment_decklist_empty_infoview.apply {
            setTitle(R.string.fragment_decklist_empty_title)
            setMessage(R.string.fragment_decklist_empty_message)
        }
    }

    override fun onSubscribeReactiveXObservables() {
        onFabClickedPublishSubject
            .subscribe {
                navigateToDeckDetailsFragment(BaseDetailsFragmentScreenType.ADD)
            }
            .addToCompositeDisposable()

        listAdapter.onItemClickPublishSubject
            .subscribe {
                navigateToCardListFragment(it.id)
            }
            .addToCompositeDisposable()

        listAdapter.onItemEditButtonClickedPublishSubject
            .subscribe {
                navigateToDeckDetailsFragment(BaseDetailsFragmentScreenType.VIEW, it.id)
            }
            .addToCompositeDisposable()

        listAdapter.onAdapterEmptyPublishSubject
            .subscribe {
                onAdapterIsEmptyChanged(it)
            }
            .addToCompositeDisposable()

        listAdapter
            .onLearnButtonClickedPublishSubject
            .subscribe {
                navigateToLearnChooseModeFragment(it.id)
            }
            .addToCompositeDisposable()
    }

    override fun onSubscribeViewModelObservables() {
        viewModel.run {
            deckListLiveData.observe(this@DeckListFragment, Observer {
                listAdapter.submitList(it)
            })
        }
    }

    private fun onAdapterIsEmptyChanged(isEmpty: Boolean) {
        if (isEmpty) {
            fragment_decklist_empty_infoview.visibility = View.VISIBLE
            fragment_decklist_recyclerview.visibility = View.GONE
        } else {
            fragment_decklist_empty_infoview.visibility = View.GONE
            fragment_decklist_recyclerview.visibility = View.VISIBLE
        }
    }

    private fun navigateToDeckDetailsFragment(screenType: BaseDetailsFragmentScreenType, deckId: Long = -1L) {
        val direction = DeckListFragmentDirections
            .actionDeckListFragmentToDeckDetailsFragment(
                deckId,
                screenType
            )

        navigation.navigate(direction)
    }

    private fun navigateToCardListFragment(deckId: Long) {
        val direction = DeckListFragmentDirections
            .actionDeckListFragmentToCardListFragment(
                deckId
            )

        navigation.navigate(direction)
    }

    private fun navigateToLearnChooseModeFragment(deckId: Long) {
        val direction = DeckListFragmentDirections
            .actionDeckListFragmentToChooseModeFragment(deckId)

        navigation.navigate(direction)
    }

}
