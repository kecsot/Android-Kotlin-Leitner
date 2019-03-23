package com.kecsot.leitnerbox.view.cardlist

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import com.kecsot.basekecsot.view.AbstractFragment
import com.kecsot.basekecsot.view.AbstractViewModel
import com.kecsot.leitnerbox.R
import com.kecsot.leitnerbox.base.detail.BaseDetailsFragmentScreenType
import com.kecsot.leitnerbox.view.cardlist.adapter.CardListAdapter
import kotlinx.android.synthetic.main.fragment_cardlist.*


class CardListFragment : AbstractFragment() {

    private lateinit var viewModel: CardListViewModel
    private val listAdapter = CardListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = AbstractViewModel.get(this)

    }

    override fun onStart() {
        super.onStart()

        arguments?.let {
            val args = CardListFragmentArgs.fromBundle(it)

            viewModel.deckId = args.deckId
            viewModel.loadCardListByDeckId(viewModel.deckId)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cardlist, container, false)
    }

    override fun onInitView() {
        setHasOptionsMenu(true)
        setupFloatingActionButton(true, R.drawable.ic_add_white_24dp)

        fragment_cardlist_recyclerview.apply {
            setHasFixedSize(true)
            adapter = listAdapter
        }

        fragment_cardlist_empty_infoview.apply {
            setTitle(R.string.fragment_cardlist_empty_title)
            setMessage(R.string.fragment_cardlist_empty_message)
        }
    }

    override fun onSubscribeReactiveXObservables() {
        onFabClickedPublishSubject
            .subscribe {
                navigateToCardDetailsFragment(
                    screenType = BaseDetailsFragmentScreenType.ADD,
                    deckId = viewModel.deckId
                )
            }
            .addToCompositeDisposable()

        listAdapter.onItemEditButtonClickedPublishSubject
            .subscribe {
                navigateToCardDetailsFragment(
                    screenType = BaseDetailsFragmentScreenType.EDIT,
                    cardId = it.id
                )
            }
            .addToCompositeDisposable()

        listAdapter.onAdapterEmptyPublishSubject
            .subscribe {
                onAdapterIsEmptyChanged(it)
            }
            .addToCompositeDisposable()
    }

    override fun onSubscribeViewModelObservables() {
        viewModel.run {
            cardListLiveData.observe(this@CardListFragment, Observer {
                listAdapter.submitList(it)
            })

            deckLiveData.observe(this@CardListFragment, Observer {
                setTitle(getString(R.string.fragment_cardlist_template_title, it.name))
            })

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_cardlist, menu)

        // FIXME: rx
        val searchMenu = menu.findItem(R.id.action_cardlist_search)
        val searchView = searchMenu?.actionView as SearchView
        searchView.run {
            queryHint = getString(R.string.base_general_search)

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(it: String): Boolean {
                    if (it.isEmpty()) {
                        viewModel.loadCardListByDeckId(viewModel.deckId)
                    } else {
                        viewModel.loadFilteredByNameCardList(viewModel.deckId, it)
                    }

                    return false
                }
            })
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun onAdapterIsEmptyChanged(isEmpty: Boolean) {
        if (isEmpty) {
            fragment_cardlist_empty_infoview.visibility = View.VISIBLE
            fragment_cardlist_recyclerview.visibility = View.GONE
        } else {
            fragment_cardlist_empty_infoview.visibility = View.GONE
            fragment_cardlist_recyclerview.visibility = View.VISIBLE
        }
    }

    private fun navigateToCardDetailsFragment(
        screenType: BaseDetailsFragmentScreenType,
        cardId: Long = -1L,
        deckId: Long = -1L
    ) {
        val direction = CardListFragmentDirections
            .actionCardListFragmentToCardDetailsFragment(
                cardId,
                deckId,
                screenType
            )

        navigation.navigate(direction)
    }

}
