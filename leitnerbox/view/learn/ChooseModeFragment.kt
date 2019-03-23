package com.kecsot.leitnerbox.view.learn


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.jakewharton.rxbinding3.view.clicks
import com.kecsot.basekecsot.view.AbstractFragment
import com.kecsot.basekecsot.view.AbstractViewModel
import com.kecsot.leitnerbox.R
import kotlinx.android.synthetic.main.fragment_learn_choosemode.*


class ChooseModeFragment : AbstractFragment() {

    private lateinit var viewModel: ChooseModeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_learn_choosemode, container, false)
    }

    override fun onStart() {
        super.onStart()

        arguments?.let {
            val deckId = ChooseModeFragmentArgs.fromBundle(it).deckId

            viewModel.argumentDeckId = deckId
            viewModel.loadByDeckId(deckId)
        }
    }

    override fun onSubscribeReactiveXObservables() {
        fragment_learn_choosemode_due
            .clicks()
            .subscribe {
                navigateToLearn(LearnMode.DUE)
            }
            .addToCompositeDisposable()

        fragment_learn_choosemode_due_withoutsave
            .clicks()
            .subscribe {
                navigateToLearn(LearnMode.DUE_WITHOUT_SAVE)
            }
            .addToCompositeDisposable()

        fragment_learn_choosemode_all_withoutsave
            .clicks()
            .subscribe {
                navigateToLearn(LearnMode.ALL_WITHOUT_SAVE)
            }
            .addToCompositeDisposable()

    }

    override fun onSubscribeViewModelObservables() {
        viewModel = AbstractViewModel.get(this)

        viewModel.run {
            onDueDatedCardIsAvailableLiveData.observe(this@ChooseModeFragment, Observer {
                fragment_learn_choosemode_due.isEnabled = it
                fragment_learn_choosemode_due_withoutsave.isEnabled = it
            })
            onAtLeastOneCardIsAvailableLiveData.observe(this@ChooseModeFragment, Observer {
                fragment_learn_choosemode_all_withoutsave.isEnabled = it
            })
            deckLiveData.observe(this@ChooseModeFragment, Observer {
                setTitle(it.name)
            })
        }
    }

    private fun navigateToLearn(mode: LearnMode) {
        val direction = ChooseModeFragmentDirections
            .actionChooseModeFragmentToLearnFragment(
                viewModel.argumentDeckId,
                mode
            )
        navigation.navigate(direction)
    }
}
