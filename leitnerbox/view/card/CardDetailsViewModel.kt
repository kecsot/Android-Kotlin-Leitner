package com.kecsot.leitnerbox.view.card

import android.net.Uri
import com.kecsot.basekecsot.util.StringUtil
import com.kecsot.imanylistview.model.ImanyItem
import com.kecsot.leitnerbox.application.LeitnerBoxApplication
import com.kecsot.leitnerbox.base.detail.AbstractDetailsViewModel
import com.kecsot.leitnerbox.base.detail.UpdateEntityFragmentEvent
import com.kecsot.leitnerbox.repository.CardRepository
import com.kecsot.leitnerbox.repository.ImageRepository
import com.kecsot.leitnerbox.repository.database.local.room.entity.CardEntity
import com.kecsot.leitnerbox.repository.database.local.room.model.ImageItem
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class CardDetailsViewModel : AbstractDetailsViewModel<CardEntity>() {

    @Inject
    protected lateinit var cardRepository: CardRepository

    @Inject
    protected lateinit var imageRepository: ImageRepository

    public val detailItemFrontImagesSubject = PublishSubject.create<List<ImanyItem>>()
    public val detailItemBackImagesSubject = PublishSubject.create<List<ImanyItem>>()

    public var pickedFrontImageList = ArrayList<ImanyItem>()
    public var pickedBackImageList = ArrayList<ImanyItem>()
    private var deckId = 0L

    companion object {
        val CARD_IMAGE_SIZE = 2048
        val CARD_IMAGE_QUALITY = 70
        val CARD_IMAGE_FOLDER_NAME = "card_images"
    }

    init {
        LeitnerBoxApplication.instance.repositoryComponent.inject(this)
    }

    override fun setEntity(entity: CardEntity) {
        super.setEntity(entity)
        loadImages(entity.frontImagePathList, detailItemFrontImagesSubject)
        loadImages(entity.backImagePathList, detailItemBackImagesSubject)
    }

    override fun getLoadDefaultItem(): Single<CardEntity> {
        val defaultEntity = CardEntity(
            frontText = StringUtil.Empty,
            backText = StringUtil.Empty,
            frontImagePathList = arrayListOf(),
            backImagePathList = arrayListOf(),
            deckId = 0L, // Later will be re-setted
            leitnerBoxLevel = 1,
            leitnerBoxLevelSetAt = 0L,
            created = 0L
        )

        return Single.just(defaultEntity)
    }

    override fun getLoadItemSingleById(id: Long): Single<CardEntity> {
        return cardRepository.getCardById(id)
    }

    fun createEntity(entity: CardEntity, frontImageList: List<ImanyItem>, backImageList: List<ImanyItem>) {
        entity.apply {
            deckId = this@CardDetailsViewModel.deckId
            created = System.currentTimeMillis()
        }

        val newFrontImages = getNewImageUriList(frontImageList)
        val newBackImages = getNewImageUriList(backImageList)

        val frontImageTask = getUploadImagesSingle(newFrontImages)
        val backImageTask = getUploadImagesSingle(newBackImages)

        val task = Single.zip(frontImageTask, backImageTask,
            BiFunction<List<ImageItem>, List<ImageItem>, CardEntity> { frontImagePathList, backImagePathList ->
                entity.frontImagePathList = ArrayList(frontImagePathList)
                entity.backImagePathList = ArrayList(backImagePathList)
                entity
            })

        runSingleOnBackground(task) {
            super.createEntity(entity)
        }
    }

    fun updateEntity(entity: CardEntity, frontImageList: List<ImanyItem>, backImageList: List<ImanyItem>) {
        val newFrontImages = getNewImageUriList(frontImageList)
        val newBackImages = getNewImageUriList(backImageList)

        val frontImageTask = getUploadImagesSingle(newFrontImages)
        val backImageTask = getUploadImagesSingle(newBackImages)

        val markedAsRemovedFrontImages = getRemovedImagePathList(entity.frontImagePathList, frontImageList)
        val markedAsRemovedBackImages = getRemovedImagePathList(entity.backImagePathList, backImageList)
        val allMarkedAsRemovedImage = markedAsRemovedFrontImages.union(markedAsRemovedBackImages).toList()


        val task = Single.zip(frontImageTask, backImageTask,
            BiFunction<List<ImageItem>, List<ImageItem>, CardEntity> { newlyAddedFrontImagePathList, newlyAddedBackImagePathList ->

                // Delete removed connections
                val remnantFrontImagePathList =
                    entity.frontImagePathList.filter { !markedAsRemovedFrontImages.contains(it) }
                val remnantBackImagePathList =
                    entity.backImagePathList.filter { !markedAsRemovedBackImages.contains(it) }

                // Add new connections
                val newFrontImagePathList = remnantFrontImagePathList.union(newlyAddedFrontImagePathList).toList()
                val newBackImagePathList = remnantBackImagePathList.union(newlyAddedBackImagePathList).toList()

                entity.frontImagePathList = ArrayList(newFrontImagePathList)
                entity.backImagePathList = ArrayList(newBackImagePathList)

                entity
            })
            .flatMap {
                getUpdateItemSingle(it)
            }

        runSingleOnBackground(task) { isUpdated ->
            if (isUpdated) {
                removeImages(allMarkedAsRemovedImage)
            }

            onDetailEvent.onNext(UpdateEntityFragmentEvent(isUpdated))
        }
    }

    private fun removeImages(imagePathList: List<ImageItem>) {
        Observable.fromIterable(imagePathList)
            .flatMapSingle {
                return@flatMapSingle imageRepository.deleteImage(it)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ isDeletedSuccessfully ->
                Timber.i("ImageItem is deleted or job subscribed")
            }, {
                Timber.e(it)
            })
            .addToCompositeDisposable()
    }

    override fun getCreateItemSingle(item: CardEntity): Single<Boolean> {
        return cardRepository.insertCard(item)
    }

    override fun getUpdateItemSingle(item: CardEntity): Single<Boolean> {
        return cardRepository.updateCard(item)
    }

    override fun getDeleteItemSingleById(id: Long): Single<Boolean> {
        return cardRepository.deleteCardById(id)
    }

    public fun setDeckId(id: Long) {
        deckId = id
    }

    private fun loadImages(imagePathList: List<ImageItem>, subject: PublishSubject<List<ImanyItem>>) {
        val task = Observable.fromIterable(imagePathList)
            .map {
                ImanyItem(
                    id = it.uid,
                    image = it.imagePath
                )
            }
            .toList()

        runSingleOnBackground(task) {
            subject.onNext(it)
        }
    }

    private fun getUploadImagesSingle(uriList: List<Uri>): Single<List<ImageItem>> {
        return Observable.fromIterable(uriList)
            .flatMapSingle { uri ->
                return@flatMapSingle imageRepository.saveImage(
                    uri,
                    CARD_IMAGE_FOLDER_NAME,
                    CARD_IMAGE_SIZE,
                    CARD_IMAGE_QUALITY
                )
            }
            .toList()
    }

    private fun getNewImageUriList(list: List<ImanyItem>): List<Uri> {
        return list.filter { it.image is Uri }.map { it.image as Uri }
    }

    private fun getRemovedImagePathList(oldImages: ArrayList<ImageItem>, newImages: List<ImanyItem>): List<ImageItem> {
        return oldImages
            .filter { oldImage ->
                !newImages.any {
                    oldImage.uid == it.id
                }
            }

    }

}
