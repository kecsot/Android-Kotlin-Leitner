package com.kecsot.leitnerbox.view.card

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.jakewharton.rxbinding3.widget.textChanges
import com.kecsot.basekecsot.util.ValidationUtil
import com.kecsot.basekecsot.view.AbstractViewModel
import com.kecsot.basekecsot.wrapper.imagepicker.ImagePicker
import com.kecsot.basekecsot.wrapper.permission.PermissionUtil
import com.kecsot.imanylistview.ImanyListView
import com.kecsot.imanylistview.configuration.ImanyListViewConfiguration
import com.kecsot.imanylistview.model.ImanyItem
import com.kecsot.leitnerbox.R
import com.kecsot.leitnerbox.base.detail.*
import com.kecsot.leitnerbox.repository.database.local.room.entity.CardEntity
import kotlinx.android.synthetic.main.fragment_card_details.*

class CardDetailsFragment : AbstractDetailsFragment<CardEntity, CardDetailsViewModel>() {

    private val REQUEST_CODE_FRONT_IMAGES = 11
    private val REQUEST_CODE_BACK_IMAGES = 12
    private val MINIMUM_FRONT_TEXT_LENGTH = 2
    private val MINIMUM_BACK_TEXT_LENGTH = 2

    private val MAXIMUM_FREE_IMAGE = 1
    private val MAXIMUM_PRO_IMAGE = 4

    private lateinit var frontImanyListView: ImanyListView
    private lateinit var backImanyListView: ImanyListView

    override fun createViewModel(): CardDetailsViewModel {
        return AbstractViewModel.get(this)
    }

    override fun setupArguments() {
        super.setupArguments()

        val screenType = CardDetailsFragmentArgs.fromBundle(arguments).screenType
        if (screenType == BaseDetailsFragmentScreenType.VIEW) {
            /*
            FIXME:
            Implement the HorizonScrollView's view/edit mode, than it will be available.
            Now it's disabled.
             */
            throw Throwable("View mode is not implemented.")
        }

        val deckId = CardDetailsFragmentArgs.fromBundle(arguments).deckId
        viewModel.setDeckId(deckId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_card_details, container, false)
    }

    override fun getArgumentItemId(): Long {
        return CardDetailsFragmentArgs.fromBundle(arguments).cardId
    }

    override fun getArgumentScreenType(): BaseDetailsFragmentScreenType {
        return CardDetailsFragmentArgs.fromBundle(arguments).screenType
    }

    override fun onInitView() {
        frontImanyListView = fragment_card_details_front_images_imanylistview
        backImanyListView = fragment_card_details_back_images_imanylistview

        val configuration = ImanyListViewConfiguration.Builder()
            .setNumberOfMaximumImages(MAXIMUM_PRO_IMAGE)
            .setNumberOfFreeImages(MAXIMUM_FREE_IMAGE)
            .setClientIsFreeUser(false)
            .setAddIconDrawable(R.drawable.ic_add_circle_black_24dp)
            .setRemoveIconDrawable(R.drawable.ic_remove_circle_red_24dp)
            .build()

        frontImanyListView
            .submitConfiguration(configuration)

        backImanyListView
            .submitConfiguration(configuration)
    }


    override fun onSubscribeReactiveXObservables() {
        super.onSubscribeReactiveXObservables()

        fragment_card_details_fronttext_field
            .textChanges()
            .subscribe {
                viewModel.detailItem.value?.let { card ->
                    card.frontText = it.toString()
                }
            }
            .addToCompositeDisposable()

        fragment_card_details_backtext_field
            .textChanges()
            .subscribe {
                viewModel.detailItem.value?.let { card ->
                    card.backText = it.toString()
                }
            }
            .addToCompositeDisposable()

        viewModel.onDetailEvent
            .subscribe {
                when (it) {
                    is CreateEntityFragmentEvent -> {
                        if (it.isSuccess) {
                            onEntityCreated()
                        }
                    }
                    is UpdateEntityFragmentEvent,
                    is DeleteEntityFragmentEvent -> {
                        if (it.isSuccess) {
                            navigateBack()
                        }
                    }
                }
            }
            .addToCompositeDisposable()

        viewModel.detailItemFrontImagesSubject
            .subscribe {
                frontImanyListView.submitImages(it)
            }
            .addToCompositeDisposable()

        viewModel.detailItemBackImagesSubject
            .subscribe { it ->
                backImanyListView.submitImages(it)
            }
            .addToCompositeDisposable()

        frontImanyListView.run {

            this.onPickImagePublishSubject
                .subscribe {
                    showImagePicker(REQUEST_CODE_FRONT_IMAGES, it.numberOfAvailableImage)
                }
                .addToCompositeDisposable()

            this.onItemClickPublishSubject
                .subscribe {
                    // v2: fullscreen imageview
                }

        }

        backImanyListView.run {

            this.onPickImagePublishSubject
                .subscribe {
                    showImagePicker(REQUEST_CODE_BACK_IMAGES, it.numberOfAvailableImage)
                }
                .addToCompositeDisposable()

            this.onItemClickPublishSubject
                .subscribe {
                    // v2: fullscreen imageview
                }
        }
    }

    override fun onSubscribeViewModelObservables() {
        super.onSubscribeViewModelObservables()

        viewModel.run {
            isProcessingLiveData.observe(this@CardDetailsFragment, Observer {
                fragment_card_details_loading_layout.visibility = if (it) View.VISIBLE else View.GONE
            })
        }
    }

    override fun isDetailInputsAreValid(): Boolean {
        val isFrontTextValid = ValidationUtil.isLengthValidWithErrorText(
            fragment_card_details_fronttext_field_inputlayout,
            fragment_card_details_fronttext_field,
            MINIMUM_FRONT_TEXT_LENGTH
        )

        val isBackTextValid = ValidationUtil.isLengthValidWithErrorText(
            fragment_card_details_backtext_field_inputlayout,
            fragment_card_details_backtext_field,
            MINIMUM_BACK_TEXT_LENGTH
        )

        return isFrontTextValid && isBackTextValid
    }

    override fun onCreateEntity(entity: CardEntity) {
        viewModel.createEntity(
            entity = entity,
            frontImageList = frontImanyListView.getImanyList(),
            backImageList = backImanyListView.getImanyList()
        )
    }

    private fun onEntityCreated() {
        fragment_card_details_fronttext_field.requestFocus()
        viewModel.loadDefaultItem()
    }

    override fun onUpdateEntity(entity: CardEntity) {
        viewModel.updateEntity(
            entity = entity,
            frontImageList = frontImanyListView.getImanyList(),
            backImageList = backImanyListView.getImanyList()
        )
    }

    override fun updateScreenByItem(item: CardEntity) {
        fragment_card_details_fronttext_field.setText(item.frontText)
        fragment_card_details_backtext_field.setText(item.backText)
    }

    override fun updateScreenByScreenType(screenType: BaseDetailsFragmentScreenType) {
        when (screenType) {
            BaseDetailsFragmentScreenType.VIEW -> setTitle(R.string.fragment_card_details_view_title)
            BaseDetailsFragmentScreenType.EDIT -> setTitle(R.string.fragment_card_details_edit_title)
            BaseDetailsFragmentScreenType.ADD -> setTitle(R.string.fragment_card_details_add_title)
        }
    }

    override fun setDetailFieldsEnable(isEnable: Boolean) {
        arrayListOf(
            fragment_card_details_backtext_field,
            fragment_card_details_fronttext_field,
            fragment_card_details_front_images_imanylistview,
            fragment_card_details_back_images_imanylistview
        ).forEach {
            it.isEnabled = isEnable
        }
    }

    override fun getItemId(item: CardEntity): Long {
        return item.id
    }

    override fun getDeleteDialogTitle(): String {
        return getString(R.string.fragment_card_details_alertdialog_delete_title_text)
    }

    override fun getDeleteDialogMessage(): String {
        return getString(R.string.fragment_card_details_alertdialog_delete_message_text)
    }

    override fun onStart() {
        super.onStart()
        onRestoreImanyListViewsState()
    }

    override fun onStop() {
        super.onStop()
        onSaveImanyListViewsState()
    }

    private fun onSaveImanyListViewsState() {
        viewModel.pickedFrontImageList = frontImanyListView.getImanyList()
        viewModel.pickedBackImageList = backImanyListView.getImanyList()
    }

    private fun onRestoreImanyListViewsState() {
        frontImanyListView.submitImages(viewModel.pickedFrontImageList)
        backImanyListView.submitImages(viewModel.pickedBackImageList)
    }

    private fun showImagePicker(requestCode: Int, maximumImage: Int) {
        requirePermission(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
        ) {
            if (it == PermissionUtil.Result.GRANTED) {
                ImagePicker
                    .setRequestCode(requestCode)
                    .setNumberOfMaximumSelectable(maximumImage)
                    .showWith(this@CardDetailsFragment)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val isResultOk = resultCode == RESULT_OK
        if (isResultOk) {
            data?.let { it ->
                val selectedImagesList = ImagePicker.obtainResult(data).map { ImanyItem(image = it) }

                when (requestCode) {
                    REQUEST_CODE_FRONT_IMAGES -> {
                        viewModel.pickedFrontImageList.addAll(selectedImagesList)
                    }
                    REQUEST_CODE_BACK_IMAGES -> {
                        viewModel.pickedBackImageList.addAll(selectedImagesList)
                    }
                    else -> {
                    }
                }
            }
        }
    }

    override fun onSaveMenuClicked() {
        val isLoading = viewModel.isProcessingLiveData.value ?: false

        if (!isLoading) {
            super.onSaveMenuClicked()
        }
    }

}
