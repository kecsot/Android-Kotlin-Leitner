package com.kecsot.leitnerbox.base.detail

import androidx.annotation.CallSuper
import androidx.lifecycle.MutableLiveData
import com.kecsot.basekecsot.view.AbstractViewModel
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject

abstract class AbstractDetailsViewModel<T> : AbstractViewModel() {

    public val detailItem = MutableLiveData<T>()
    public val onDetailEvent = PublishSubject.create<BaseDetailsFragmentEvent>()
    public val screenType = MutableLiveData<BaseDetailsFragmentScreenType>()

    protected abstract fun getLoadDefaultItem(): Single<T>
    protected abstract fun getLoadItemSingleById(id: Long): Single<T>
    protected abstract fun getCreateItemSingle(item: T): Single<Boolean>
    protected abstract fun getUpdateItemSingle(item: T): Single<Boolean>
    protected abstract fun getDeleteItemSingleById(id: Long): Single<Boolean>

    public open fun loadDefaultItem() {
        runSingleOnBackground(getLoadDefaultItem()) {
            setEntity(it)
            onDetailEvent.onNext(LoadEntityFragmentEvent(true))
        }
    }

    public open fun loadEntityById(id: Long) {
        runSingleOnBackground(getLoadItemSingleById(id)) {
            setEntity(it)
            onDetailEvent.onNext(LoadEntityFragmentEvent(true))
        }
    }

    public open fun createEntity(entity: T) {
        runSingleOnBackground(getCreateItemSingle(entity)) { isCreated ->
            onDetailEvent.onNext(CreateEntityFragmentEvent(isCreated))
        }
    }

    public open fun updateEntity(entity: T) {
        runSingleOnBackground(getUpdateItemSingle(entity)) { isUpdated ->
            onDetailEvent.onNext(UpdateEntityFragmentEvent(isUpdated))
        }
    }

    public open fun deleteEntityById(id: Long) {
        runSingleOnBackground(getDeleteItemSingleById(id)) { isDeleted ->
            onDetailEvent.onNext(DeleteEntityFragmentEvent(isDeleted))
        }
    }

    @CallSuper
    public open fun setEntity(entity: T) {
        detailItem.postValue(entity)
    }

}