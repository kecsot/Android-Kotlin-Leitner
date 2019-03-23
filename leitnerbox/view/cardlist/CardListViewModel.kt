package com.kecsot.leitnerbox.view.cardlist

import androidx.lifecycle.MutableLiveData
import com.kecsot.basekecsot.view.AbstractViewModel
import com.kecsot.leitnerbox.application.LeitnerBoxApplication
import com.kecsot.leitnerbox.repository.CardRepository
import com.kecsot.leitnerbox.repository.DeckRepository
import com.kecsot.leitnerbox.repository.database.local.room.entity.CardEntity
import com.kecsot.leitnerbox.repository.database.local.room.entity.DeckEntity
import com.kecsot.leitnerbox.view.cardlist.adapter.CardListItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CardListViewModel : AbstractViewModel() {

    @Inject
    protected lateinit var cardRepository: CardRepository

    @Inject
    protected lateinit var deckRepository: DeckRepository

    public var deckId: Long = 0L
    public val cardListLiveData = MutableLiveData<List<CardListItem>>()
    public val deckLiveData = MutableLiveData<DeckEntity>()
    private lateinit var deckEntity: DeckEntity

    init {
        LeitnerBoxApplication.instance.repositoryComponent.inject(this)
    }

    public fun loadCardListByDeckId(deckId: Long) {
        deckRepository.getDeckById(deckId)
            .flatMap {
                deckLiveData.postValue(it)
                deckEntity = it
                return@flatMap cardRepository.getAllByDeckId(it.id)
            }
            .map {
                it.map {
                    mapCardEntityToCardListItem(it)
                }
            }
            .map {
                sortList(it)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                cardListLiveData.postValue(it)
            }, {
                throw it
            })
            .addToCompositeDisposable()
    }

    public fun loadFilteredByNameCardList(deckId: Long, value: String) {
        cardRepository.getAllByDeckId(deckId)
            .map {
                it.map {
                    mapCardEntityToCardListItem(it)
                }.filter {
                    it.backText.contains(value) || it.frontText.contains(value)
                }
            }
            .map {
                sortList(it)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                cardListLiveData.postValue(it)
            }, {
                throw it
            })
            .addToCompositeDisposable()
    }

    private fun sortList(list: List<CardListItem>): List<CardListItem> {
        return list.sortedBy {
            it.dueDate
        }
    }

    // refact
    private fun mapCardEntityToCardListItem(cardEntity: CardEntity): CardListItem {
        val rules = deckEntity.deckLeitnerBoxRules
        val lastRule = rules.last()
        val finalBoxLevel = lastRule.level + 1
        val isCardFinished = cardEntity.leitnerBoxLevel >= finalBoxLevel
        val actualRule = rules.find {
            it.level == cardEntity.leitnerBoxLevel
        }

        var dueDate = 0L
        if (isCardFinished) {
            dueDate = Long.MAX_VALUE
        } else {
            actualRule?.let {
                dueDate = cardEntity.leitnerBoxLevelSetAt + it.spaceRepetitionTime
            }
        }

        return CardListItem(
            cardEntity.id,
            cardEntity.frontText,
            cardEntity.backText,
            cardEntity.leitnerBoxLevel,
            dueDate,
            isCardFinished
        )
    }

}
