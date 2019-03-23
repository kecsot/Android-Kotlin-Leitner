package com.kecsot.leitnerbox.view.deck

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.kecsot.leitnerbox.application.LeitnerBoxApplication
import com.kecsot.leitnerbox.base.detail.AbstractDetailsViewModel
import com.kecsot.leitnerbox.repository.DeckRepository
import com.kecsot.leitnerbox.repository.database.local.room.entity.DeckEntity
import com.kecsot.leitnerbox.repository.database.local.room.model.LeitnerBoxRule
import io.reactivex.Single
import javax.inject.Inject

class DeckDetailsViewModel : AbstractDetailsViewModel<DeckEntity>() {

    @Inject
    protected lateinit var deckRepository: DeckRepository

    private val MINIMUM_LEITNER_BOXES = 2
    private val MAXIMUM_LEITNER_BOXES = 30


    public val numberOfLeitnerBoxes = Transformations.switchMap(detailItem) {
        val result = MutableLiveData<Int>()
        val numberOfBoxes = it.deckLeitnerBoxRules.size + 1
        result.postValue(numberOfBoxes)
        result
    }

    init {
    }

    companion object {
        private val DAY_IN_MS = 86400000L

        val DEFAULT_RULES = arrayListOf(
            LeitnerBoxRule(1, 1 * DAY_IN_MS),
            LeitnerBoxRule(2, 2 * DAY_IN_MS),
            LeitnerBoxRule(3, 7 * DAY_IN_MS),
            LeitnerBoxRule(4, 14 * DAY_IN_MS)
        )
    }

    val defaultEntity = DeckEntity(
        name = "",
        description = "",
        deckLeitnerBoxRules = DEFAULT_RULES
    )

    init {
        LeitnerBoxApplication.instance.repositoryComponent.inject(this)
    }

    override fun getLoadDefaultItem(): Single<DeckEntity> {
        return Single.just(defaultEntity)
    }

    override fun getLoadItemSingleById(id: Long): Single<DeckEntity> {
        return deckRepository.getDeckById(id)
    }

    override fun getCreateItemSingle(item: DeckEntity): Single<Boolean> {
        return deckRepository.insertDeckEntity(item)
    }

    override fun getUpdateItemSingle(item: DeckEntity): Single<Boolean> {
        return deckRepository.updateDeck(item)
    }

    override fun getDeleteItemSingleById(id: Long): Single<Boolean> {
        return deckRepository.deleteDeckById(id)
    }

    // FIXME: refact
    public fun decreaseNumberOfLeitnerBoxRules() {
        detailItem.value?.let {
            val deckLeitnerBoxRuleList = it.deckLeitnerBoxRules
            val itemCount = deckLeitnerBoxRuleList.size
            val isDecreasable = itemCount > MINIMUM_LEITNER_BOXES

            if (isDecreasable) {
                val copyList = ArrayList(deckLeitnerBoxRuleList)
                copyList.removeAt(itemCount - 1)
                it.deckLeitnerBoxRules = copyList

                detailItem.postValue(it)
            }
        }
    }

    // FIXME: refact
    public fun increaseNumberOfLeitnerBoxRules() {

        detailItem.value?.let {
            val deckLeitnerBoxRuleList = it.deckLeitnerBoxRules
            val itemCount = deckLeitnerBoxRuleList.size
            val defaultItemsCount = DEFAULT_RULES.size
            val isIncreasable = deckLeitnerBoxRuleList.size < MAXIMUM_LEITNER_BOXES

            if (isIncreasable) {
                val list = ArrayList(it.deckLeitnerBoxRules)

                var spacedRepetitionTime = 0L
                if (defaultItemsCount > itemCount) {
                    spacedRepetitionTime = DEFAULT_RULES[itemCount].spaceRepetitionTime
                }

                list.add(
                    LeitnerBoxRule(itemCount + 1, spacedRepetitionTime)
                )
                it.deckLeitnerBoxRules = list

                detailItem.postValue(it)
            }
        }
    }
}
