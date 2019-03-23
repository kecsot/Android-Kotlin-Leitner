package com.kecsot.leitnerbox.repository.database.local

import android.net.Uri
import com.kecsot.basekecsot.util.FileUtil
import com.kecsot.basekecsot.util.StorageUtil
import com.kecsot.basekecsot.wrapper.imagestorage.ImageStorage
import com.kecsot.leitnerbox.application.LeitnerBoxApplication
import com.kecsot.leitnerbox.repository.database.local.room.model.ImageItem
import com.kecsot.leitnerbox.repository.local.ImageLocalDataSourceInterface
import io.reactivex.Single
import java.io.File

class ImageLocalDataSource : ImageLocalDataSourceInterface {

    private val IMAGE_PREFIX = "img"
    private val IMAGE_TYPE = ".jpeg"

    val contentResolver = LeitnerBoxApplication.instance.contentResolver    // FIXME inject
    val context = LeitnerBoxApplication.instance.applicationContext  // FIXME inject

    override fun copyImage(imageItem: ImageItem, destinationFile: File): Single<Boolean> {
        return Single.create {
            val image = File(imageItem.imagePath)

            try {
                FileUtil.copyFile(image, destinationFile)
                it.onSuccess(true)
            } catch (e: Exception) {
                it.onError(e)
            }

        }
    }

    override fun saveImage(uri: Uri, folderName: String, maxSize: Int, quality: Int): Single<ImageItem> {
        val filename = FileUtil.generateFilenameWithDate(IMAGE_PREFIX, IMAGE_TYPE)
        val generateOutputPath = generateImageOutPutPath(folderName, filename)

        val imageStorage = ImageStorage.Builder()
            .setMaxHeight(maxSize)
            .setMaxWidth(maxSize)
            .setQuality(quality)
            .build()

        return generateOutputPath
            .flatMap { outputFilePath ->
                return@flatMap imageStorage.saveImage(
                    contentResolver,
                    uri,
                    outputFilePath
                )
            }
            .map {
                ImageItem(it, filename, System.currentTimeMillis())
            }
    }

    override fun saveImage(byteArray: ByteArray, folderName: String, maxSize: Int, quality: Int): Single<ImageItem> {
        val filename = FileUtil.generateFilenameWithDate(IMAGE_PREFIX, IMAGE_TYPE)
        val generateOutputPath = generateImageOutPutPath(folderName, filename)

        val imageStorage = ImageStorage.Builder()
            .setMaxHeight(maxSize)
            .setMaxWidth(maxSize)
            .setQuality(quality)
            .build()

        return generateOutputPath
            .flatMap { outputFilePath ->
                return@flatMap imageStorage.saveImage(
                    byteArray,
                    outputFilePath
                )
            }
            .map {
                ImageItem(it, filename, System.currentTimeMillis())
            }
    }

    private fun generateImageOutPutPath(folderName: String, fileName: String): Single<String> {
        return Single.create<String> { emitter ->
            val outputDirectoryPath = StorageUtil.getAppDirectoryWithFolder(context, folderName)
            val isDirectoryCreated = StorageUtil.createDirectoryIfNeeded(outputDirectoryPath)

            if (isDirectoryCreated) {
                val outputFilePath = StorageUtil.getFilenameAppendedToPath(outputDirectoryPath, fileName)

                emitter.onSuccess(outputFilePath)
            } else {
                emitter.onError(Throwable("Can't create directory: $outputDirectoryPath"))
            }
        }
    }

    override fun isFileExist(item: ImageItem): Single<Boolean> {
        val isExist = File(item.imagePath).exists()
        return Single.just(isExist)
    }

    override fun deleteFile(item: ImageItem): Single<Boolean> {
        return Single.create {
            val file = File(item.imagePath)
            it.onSuccess(file.delete())
        }
    }

}