package com.kecsot.leitnerbox.view.learn

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.jakewharton.rxbinding3.view.clicks
import com.kecsot.basekecsot.view.AbstractFragment
import com.kecsot.basekecsot.view.AbstractViewModel
import com.kecsot.leitnerbox.R
import com.kecsot.leitnerbox.common.imageviewpager.ImageViewPagerAdapter
import com.kecsot.leitnerbox.common.util.LeitnerBoxTimeFormatUtil
import kotlinx.android.synthetic.main.fragment_learn.*
import kotlinx.android.synthetic.main.view_learn_card.view.*
import me.relex.circleindicator.CircleIndicator
import timber.log.Timber

class LearnFragment : AbstractFragment() {

    private lateinit var viewModel: LearnViewModel
    private lateinit var frontCardView: View
    private lateinit var backCardView: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = AbstractViewModel.get(this)
        setupArguments()
    }

    private fun setupArguments() {
        val args = LearnFragmentArgs.fromBundle(arguments)
        viewModel.argumentLearnMode = args.learnMode
        viewModel.loadCardsByDeckId(args.deckId, args.learnMode)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_learn, container, false)
    }

    override fun onInitView() {
        frontCardView = fragment_learn_frontcard
        backCardView = fragment_learn_backcard
    }

    override fun onSubscribeReactiveXObservables() {
        fragment_learn_dontknow_button
            .clicks()
            .subscribe {
                viewModel.applyAnswer(CardState.FAILED)
            }
            .addToCompositeDisposable()

        fragment_learn_repeat_button
            .clicks()
            .subscribe {
                viewModel.applyAnswer(CardState.REPEAT)
            }
            .addToCompositeDisposable()

        fragment_learn_done_button
            .clicks()
            .subscribe {
                viewModel.applyAnswer(CardState.DONE)
            }
            .addToCompositeDisposable()

        viewModel.onCriticalErrorHappened
            .subscribe {
                Timber.e(Throwable("Critical error happened"))
                navigateBack()
            }
            .addToCompositeDisposable()

        fragment_learn_show_backcard_button
            .clicks()
            .subscribe {
                enableInspection()
            }
            .addToCompositeDisposable()
    }

    override fun onSubscribeViewModelObservables() {
        viewModel.run {

            numberOfReadyCardsLiveData.observe(this@LearnFragment, Observer {
                fragment_learn_numberofcards_ready.text = it.toString()
            })

            numberOfRepeatCardsLiveData.observe(this@LearnFragment, Observer {
                fragment_learn_numberofcards_repeat.text = it.toString()
            })

            numberOfDoneCardsLiveData.observe(this@LearnFragment, Observer {
                fragment_learn_numberofcards_done.text = it.toString()
            })

            actualModelLiveData.observe(this@LearnFragment, Observer {
                updateCardDetails(it)
            })

            learnIsFinishedLiveData.observe(this@LearnFragment, Observer { isFinished ->
                updateIsGameFinished(isFinished)
            })

        }
    }

    private fun updateIsGameFinished(isFinished: Boolean) {
        fragment_learn_finish_layout.visibility = if (isFinished) View.VISIBLE else View.GONE
        fragment_learn_layout.visibility = if (isFinished) View.GONE else View.VISIBLE
    }

    private fun getViewPagerAdapterSize(viewPager: ViewPager): Int {
        return viewPager.adapter?.count ?: 0
    }

    private fun setupImageViewPagerIndicator(indicator: CircleIndicator, viewPager: ViewPager) {
        val isMoreItemFound = getViewPagerAdapterSize(viewPager) > 1

        indicator.visibility = if (isMoreItemFound) View.VISIBLE else View.GONE
        indicator.setViewPager(viewPager)
    }

    private fun isViewPagerEmpty(viewPager: ViewPager): Boolean {
        return getViewPagerAdapterSize(viewPager) == 0
    }

    private fun setupGallery(galleryView: View, indicator: CircleIndicator, viewPager: ViewPager) {
        val isGalleryNeeded = !isViewPagerEmpty(viewPager)

        galleryView.visibility = if (isGalleryNeeded) View.VISIBLE else View.GONE

        if (isGalleryNeeded) {
            setupImageViewPagerIndicator(
                indicator = indicator,
                viewPager = viewPager
            )
        }
    }

    private fun setupGalleries() {
        setupGallery(
            galleryView = frontCardView.view_learn_card_gallery,
            indicator = frontCardView.view_learn_card_viewpager_indicator,
            viewPager = frontCardView.view_learn_card_viewpager
        )

        setupGallery(
            galleryView = backCardView.view_learn_card_gallery,
            indicator = backCardView.view_learn_card_viewpager_indicator,
            viewPager = backCardView.view_learn_card_viewpager
        )
    }

    private fun recreateViewPagerImageAdapters(viewPager: ViewPager, images: List<Uri>) {
        viewPager.adapter = ImageViewPagerAdapter(requireContext(), ArrayList(images))
    }

    private fun recreateImageViewPagersByModel(model: LearnModel) {
        val frontImageUriList = viewModel.mapImageItemListToUriList(model.card.frontImagePathList)
        val backImageUriList = viewModel.mapImageItemListToUriList(model.card.backImagePathList)

        recreateViewPagerImageAdapters(frontCardView.view_learn_card_viewpager, frontImageUriList)
        recreateViewPagerImageAdapters(backCardView.view_learn_card_viewpager, backImageUriList)
    }

    private fun updateAnswerButtonsVisiblitiesByModel(model: LearnModel) {
        model.state.run {
            when (this) {
                CardState.READY, CardState.FAILED -> {
                    fragment_learn_repeat_button.visibility = View.VISIBLE
                }
                CardState.REPEAT -> {
                    fragment_learn_repeat_button.visibility = View.INVISIBLE
                }
                CardState.DONE -> {
                }
            }
        }
    }

    private fun updateEasyButtonDueDateTextByModel(model: LearnModel) {
        val nextRule = viewModel.getNextRule(model)
        val isCardWillFinish = nextRule == null

        fragment_learn_done_due_text.text = if (isCardWillFinish) {
            getString(R.string.fragment_learn_due_never)
        } else {
            LeitnerBoxTimeFormatUtil.formatRuleTime(requireContext(), nextRule!!.spaceRepetitionTime, true)
        }
        fragment_learn_done_due_text.visibility =
            if (viewModel.argumentLearnMode == LearnMode.DUE) View.VISIBLE else View.GONE
    }

    private fun updateCardTextsByModel(model: LearnModel) {
        frontCardView.view_learn_card_text.text = model.card.frontText
        backCardView.view_learn_card_text.text = model.card.backText
    }

    private fun showAnswerButtonsLayout() {
        fragment_learn_answer_buttons_layout.visibility = View.VISIBLE
    }

    private fun hideAnswerButtonsLayout() {
        fragment_learn_answer_buttons_layout.visibility = View.GONE
    }

    private fun showShowBackCardButtonLayout() {
        fragment_learn_show_backcard_button.visibility = View.VISIBLE
    }

    private fun hideShowBackCardButtonLayout() {
        fragment_learn_show_backcard_button.visibility = View.GONE
    }

    private fun showFrontCard() {
        frontCardView.visibility = View.VISIBLE
    }

    private fun showBackCard() {
        backCardView.visibility = View.VISIBLE
    }

    private fun hideBackCard() {
        backCardView.visibility = View.GONE
    }

    private fun enableInspection() {
        hideShowBackCardButtonLayout()
        showAnswerButtonsLayout()
        showBackCard()
        // scrollToBackCard()
    }

    private fun disableInspection() {
        hideBackCard()
        hideAnswerButtonsLayout()
        showShowBackCardButtonLayout()
    }

    private fun updateCardDetails(model: LearnModel) {
        disableInspection()
        showFrontCard()
        updateCardTextsByModel(model)
        updateEasyButtonDueDateTextByModel(model)
        recreateImageViewPagersByModel(model)
        setupGalleries()
        updateAnswerButtonsVisiblitiesByModel(model)
    }

}
