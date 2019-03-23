package com.kecsot.leitnerbox.repository

import android.net.Uri
import com.kecsot.leitnerbox.application.LeitnerBoxApplication
import com.kecsot.leitnerbox.repository.database.local.room.model.ImageItem
import com.kecsot.leitnerbox.repository.local.ImageLocalDataSourceInterface
import io.reactivex.Observable
import io.reactivex.Single
import java.io.File
import javax.inject.Inject


class ImageRepository(val cardRepository: CardRepository) {

    @Inject
    lateinit var imageLocalDataSourceInterface: ImageLocalDataSourceInterface


    init {
        LeitnerBoxApplication.instance.localDataSourceComponent.inject(this)
    }

    fun copyImage(imageItem: ImageItem, destinationFile: File): Single<Boolean> {
        return imageLocalDataSourceInterface.copyImage(imageItem, destinationFile)
    }

    fun saveImage(uri: Uri, folderName: String, maxSize: Int, quality: Int): Single<ImageItem> {
        return imageLocalDataSourceInterface.saveImage(uri, folderName, maxSize, quality)
    }

    fun saveImage(byteArray: ByteArray, folderName: String, maxSize: Int, quality: Int): Single<ImageItem> {
        return imageLocalDataSourceInterface.saveImage(byteArray, folderName, maxSize, quality)
    }

    fun isImageExist(image: ImageItem): Single<Boolean> {
        return imageLocalDataSourceInterface.isFileExist(image)
    }

    fun deleteImage(image: ImageItem): Single<Boolean> {
        return imageLocalDataSourceInterface.deleteFile(image)
    }

    fun deleteAll() : Single<Boolean> {
        return cardRepository.getAll()  // All image in app
            .map {
                it.map {
                    it.frontImagePathList.union(it.backImagePathList)
                }
            }
            .map {
                it.flatten()
            }
            .flatMapObservable {
                return@flatMapObservable Observable.fromIterable(it)
            }
            .flatMapSingle {
                return@flatMapSingle deleteImage(it)
            }
            .toList()
            .map {
                it.all { it }
            }
    }

}