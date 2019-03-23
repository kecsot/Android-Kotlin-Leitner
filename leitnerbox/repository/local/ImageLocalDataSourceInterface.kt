package com.kecsot.leitnerbox.repository.local

import android.net.Uri
import com.kecsot.basekecsot.repository.BaseFileLocalDataSourceInterface
import com.kecsot.leitnerbox.repository.database.local.room.model.ImageItem
import io.reactivex.Single
import java.io.File

interface ImageLocalDataSourceInterface : BaseFileLocalDataSourceInterface<ImageItem> {

    fun copyImage(imageItem: ImageItem, destinationFile: File): Single<Boolean>

    fun saveImage(uri: Uri, folderName: String, maxSize: Int, quality: Int): Single<ImageItem>

    fun saveImage(byteArray: ByteArray, folderName: String, maxSize: Int, quality: Int): Single<ImageItem>

}