package com.kecsot.leitnerbox.view.learn

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.kecsot.basekecsot.view.AbstractViewModel
import com.kecsot.leitnerbox.application.LeitnerBoxApplication
import com.kecsot.leitnerbox.repository.CardRepository
import com.kecsot.leitnerbox.repository.DeckRepository
import com.kecsot.leitnerbox.repository.database.local.room.entity.CardEntity
import com.kecsot.leitnerbox.repository.database.local.room.entity.DeckEntity
import com.kecsot.leitnerbox.repository.database.local.room.model.ImageItem
import com.kecsot.leitnerbox.repository.database.local.room.model.LeitnerBoxRule
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class LearnViewModel : AbstractViewModel() {

    @Inject
    protected lateinit var cardRepository: CardRepository

    @Inject
    protected lateinit var deckRepository: DeckRepository

    public val onCriticalErrorHappened = PublishSubject.create<Boolean>()

    public lateinit var deckEntity: DeckEntity
    public lateinit var learnModelList: ArrayList<LearnModel>

    public var actualModelLiveData = MutableLiveData<LearnModel>()
    public var numberOfReadyCardsLiveData = MutableLiveData<Int>()
    public var numberOfRepeatCardsLiveData = MutableLiveData<Int>()
    public var numberOfDoneCardsLiveData = MutableLiveData<Int>()
    public var learnIsFinishedLiveData = MutableLiveData<Boolean>()

    public var argumentLearnMode: LearnMode = LearnMode.DUE
    private var isDatabaseUpdateNeeded = true

    init {
        LeitnerBoxApplication.instance.repositoryComponent.inject(this)
    }

    private fun getCardsByLearnMode(deckId: Long, learnMode: LearnMode): Single<List<CardEntity>> {
        return when (learnMode) {
            LearnMode.DUE -> cardRepository.getDueCardsByDeckId(deckId)
            LearnMode.DUE_WITHOUT_SAVE -> cardRepository.getDueCardsByDeckId(deckId)
            LearnMode.ALL_WITHOUT_SAVE -> cardRepository.getAllByDeckId(deckId)
        }
    }

    private fun setDatabaseUpdateByLearnMode(learnMode: LearnMode) {
        isDatabaseUpdateNeeded = when (learnMode) {
            LearnMode.DUE -> true
            LearnMode.DUE_WITHOUT_SAVE, LearnMode.ALL_WITHOUT_SAVE -> false
        }
    }

    public fun loadCardsByDeckId(deckId: Long, learnMode: LearnMode) {
        deckRepository.getDeckById(deckId)
            .flatMap {
                deckEntity = it
                setDatabaseUpdateByLearnMode(learnMode)
                return@flatMap getCardsByLearnMode(deckId, learnMode)
            }
            .map {
                it.map {
                    LearnModel(it, CardState.READY)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                learnModelList = ArrayList(it)
                loadNextItem()
                updateNumberIndicators()
            }, {
                onCriticalErrorHappened.onNext(true)
                Timber.e(it)
            })
            .addToCompositeDisposable()
    }

    public fun applyAnswer(state: CardState) {
        actualModelLiveData.value?.let { model ->
            // Remove
            learnModelList.remove(model)

            // Set states
            when (state) {
                CardState.FAILED -> {
                    model.card.leitnerBoxLevel = 0
                }
                CardState.DONE -> {
                    model.card.leitnerBoxLevel += 1
                    model.card.leitnerBoxLevelSetAt = System.currentTimeMillis()

                    if (isDatabaseUpdateNeeded) {
                        updateEntity(model.card)
                    }
                }
                else -> {
                }
            }
            model.state = state

            // Add to last position
            learnModelList.add(learnModelList.size, model)

            loadNextItem()
            updateNumberIndicators()
            checkIsGameFinished()
        }

    }

    private fun loadNextItem() {
        val indexOfItem = learnModelList.indexOfFirst { it.state != CardState.DONE }

        if (indexOfItem != -1) {
            actualModelLiveData.postValue(learnModelList[indexOfItem])
        }
    }

    private fun updateNumberIndicators() {
        numberOfReadyCardsLiveData.postValue(learnModelList.count { it.state == CardState.READY || it.state == CardState.FAILED })
        numberOfRepeatCardsLiveData.postValue(learnModelList.count { it.state == CardState.REPEAT })
        numberOfDoneCardsLiveData.postValue(learnModelList.count { it.state == CardState.DONE })
    }

    private fun checkIsGameFinished() {
        learnIsFinishedLiveData.postValue(learnModelList.all { it.state == CardState.DONE })
    }

    private fun updateEntity(entity: CardEntity) {
        runSingleOnBackground(cardRepository.updateCard(entity)) {
            if (!it) {
                Timber.e("Failed to update card $entity")
            }
        }
    }

    public fun getNextRule(model: LearnModel): LeitnerBoxRule? {
        val nextRuleLevel = model.card.leitnerBoxLevel + 1

        return deckEntity.deckLeitnerBoxRules
            .find {
                it.level == nextRuleLevel
            }
    }

    public fun mapImageItemListToUriList(imageList: List<ImageItem>): List<Uri> {
        return imageList.map { Uri.parse(it.imagePath) }
    }

}