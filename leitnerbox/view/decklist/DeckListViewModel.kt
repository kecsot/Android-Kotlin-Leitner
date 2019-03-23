package com.kecsot.leitnerbox.view.decklist

import androidx.lifecycle.MutableLiveData
import com.kecsot.basekecsot.view.AbstractViewModel
import com.kecsot.leitnerbox.application.LeitnerBoxApplication
import com.kecsot.leitnerbox.repository.CardRepository
import com.kecsot.leitnerbox.repository.DeckRepository
import com.kecsot.leitnerbox.view.decklist.adapter.DeckListItem
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class DeckListViewModel : AbstractViewModel() {

    @Inject
    protected lateinit var deckRepository: DeckRepository

    @Inject
    protected lateinit var cardRepository: CardRepository

    public val deckListLiveData = MutableLiveData<List<DeckListItem>>()

    init {
        LeitnerBoxApplication.instance.repositoryComponent.inject(this)
    }

    public fun updateDeckList() {
        deckRepository.getAll()
            .flatMapObservable {
                return@flatMapObservable Observable.fromIterable(it)
            }
            .flatMapSingle { deckEntity ->
                val countAllCardSingle = cardRepository.countCardsByDeckId(deckEntity.id)
                val countDueDatedCardSingle = cardRepository.countDueCardsByDeckId(deckEntity.id)

                return@flatMapSingle Single.zip(countAllCardSingle, countDueDatedCardSingle,
                    BiFunction<Int, Int, DeckListItem> { allCardSize, dueDatedCardSize ->

                        DeckListItem(
                            id = deckEntity.id,
                            deckName = deckEntity.name,
                            description = deckEntity.description,
                            allCardCount = allCardSize,
                            allDueCardCount = dueDatedCardSize
                        )
                    })
            }
            .toList()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                deckListLiveData.postValue(it)
            }, {
                Timber.e(it)
            })
            .addToCompositeDisposable()
    }

}
