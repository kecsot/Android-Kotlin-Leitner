package com.kecsot.leitnerbox.view.learn

import androidx.lifecycle.MutableLiveData
import com.kecsot.basekecsot.view.AbstractViewModel
import com.kecsot.leitnerbox.application.LeitnerBoxApplication
import com.kecsot.leitnerbox.repository.CardRepository
import com.kecsot.leitnerbox.repository.DeckRepository
import com.kecsot.leitnerbox.repository.database.local.room.entity.DeckEntity
import javax.inject.Inject

class ChooseModeViewModel : AbstractViewModel() {

    @Inject
    lateinit var cardRepository: CardRepository

    @Inject
    lateinit var deckRepository: DeckRepository

    var argumentDeckId : Long = 0L
    val deckLiveData = MutableLiveData<DeckEntity>()
    val onDueDatedCardIsAvailableLiveData = MutableLiveData<Boolean>()
    val onAtLeastOneCardIsAvailableLiveData = MutableLiveData<Boolean>()

    init {
        LeitnerBoxApplication.instance.repositoryComponent.inject(this)

        onDueDatedCardIsAvailableLiveData.postValue(false)
        onAtLeastOneCardIsAvailableLiveData.postValue(false)
    }

    fun loadByDeckId(deckId: Long) {
        loadDeck(deckId)
        loadDueDatedState(deckId)
        loadAtLeastOneState(deckId)
    }

    private fun loadDeck(deckId: Long){
        runSingleOnBackground(deckRepository.getDeckById(deckId)){
            deckLiveData.postValue(it)
        }
    }

    private fun loadDueDatedState(deckId: Long) {
        val single = cardRepository.countDueCardsByDeckId(deckId)
            .map {
                it > 0
            }
        runSingleOnBackground(single) {
            onDueDatedCardIsAvailableLiveData.postValue(it)
        }
    }

    private fun loadAtLeastOneState(deckId: Long) {
        val single = cardRepository.countCardsByDeckId(deckId)
            .map {
                it > 0
            }
        runSingleOnBackground(single) {
            onAtLeastOneCardIsAvailableLiveData.postValue(it)
        }
    }

}