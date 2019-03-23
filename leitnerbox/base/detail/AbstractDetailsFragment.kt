package com.kecsot.leitnerbox.base.detail

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.annotation.CallSuper
import androidx.lifecycle.Observer
import com.kecsot.basekecsot.R
import com.kecsot.basekecsot.view.alertdialog.BaseAlertDialog
import com.kecsot.basekecsot.view.alertdialog.BaseAlertDialogEvent
import com.kecsot.basekecsot.view.alertdialog.PositiveClickAlertDialogEvent
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

abstract class AbstractDetailsFragment<T, VM : AbstractDetailsViewModel<T>> : AbstractLeitnerFragment() {

    protected lateinit var viewModel: VM

    private var deleteDialog: BaseAlertDialog? = null
    private var onDeleteDialogEventPublishSubject = PublishSubject.create<BaseAlertDialogEvent>()
    private var onDeleteDialogVisibilityBehaviorSubject = BehaviorSubject.create<Boolean>()


    abstract fun createViewModel(): VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel()

        setupArguments()
        setHasOptionsMenu(true)
    }

    abstract fun getArgumentItemId(): Long
    abstract fun getArgumentScreenType(): BaseDetailsFragmentScreenType

    @CallSuper
    public open fun setupArguments() {

        val argumentItemId = getArgumentItemId()
        val argumentScreenType = getArgumentScreenType()

        viewModel.screenType.run {
            if (value == null) {
                viewModel.screenType.value = argumentScreenType
            }
        }

        when (argumentScreenType) {
            BaseDetailsFragmentScreenType.EDIT,
            BaseDetailsFragmentScreenType.VIEW -> {
                checkNotNull(argumentItemId)

                if (viewModel.detailItem.value == null) {
                    viewModel.loadEntityById(argumentItemId)
                }
            }
            BaseDetailsFragmentScreenType.ADD -> {
                viewModel.loadDefaultItem()
            }
        }
    }

    @CallSuper
    override fun onSubscribeReactiveXObservables() {
        onDeleteDialogEventPublishSubject
            .ofType(PositiveClickAlertDialogEvent::class.java)
            .subscribe {
                viewModel.detailItem.value?.let { item ->
                    viewModel.deleteEntityById(getItemId(item))
                }
            }
            .addToCompositeDisposable()

        // It will emit when user rotate screen, and it will reshow dialog if needed
        onDeleteDialogVisibilityBehaviorSubject
            .subscribe { isLastStateVisible ->
                val isDeleteDialogVisible = deleteDialog?.isShowing() == true

                if (isLastStateVisible && !isDeleteDialogVisible) {
                    showDeleteDialog()
                }
            }
            .addToCompositeDisposable()

        viewModel.onDetailEvent
            .subscribe {
                when (it) {
                    is CreateEntityFragmentEvent,
                    is UpdateEntityFragmentEvent,
                    is DeleteEntityFragmentEvent -> {
                        if (it.isSuccess) {
                            showSnackBar(R.string.base_general_success)
                        }
                    }
                    is LoadEntityFragmentEvent -> {
                        if (!it.isSuccess) {
                            navigateBack()
                        }
                    }
                }

                if (!it.isSuccess) {
                    showSnackBar(R.string.base_general_error)
                }
            }
            .addToCompositeDisposable()
    }

    abstract fun updateScreenByItem(item: T)
    open fun updateScreenByScreenType(screenType: BaseDetailsFragmentScreenType) {}
    abstract fun setDetailFieldsEnable(isEnable: Boolean)

    // FIXME: remove it with it: BaseEntity with id
    abstract fun getItemId(item: T): Long

    @CallSuper
    override fun onSubscribeViewModelObservables() {
        viewModel.run {
            detailItem.observe(this@AbstractDetailsFragment, Observer {
                updateScreenByItem(it)
            })
            screenType.observe(this@AbstractDetailsFragment, Observer {
                when (it) {
                    BaseDetailsFragmentScreenType.VIEW -> {
                        setDetailFieldsEnable(false)
                    }
                    BaseDetailsFragmentScreenType.EDIT,
                    BaseDetailsFragmentScreenType.ADD -> {
                        setDetailFieldsEnable(true)
                    }
                }
                // Rebuild Menu
                activity?.invalidateOptionsMenu()
                updateScreenByScreenType(it)
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.base_menu_fragment_details, menu)

        val saveMenu = menu.findItem(R.id.base_action_details_save)
        val editMenu = menu.findItem(R.id.base_action_details_edit)
        val deleteMenu = menu.findItem(R.id.base_action_details_delete)

        viewModel.screenType.value?.let {
            val isAddMode = it == BaseDetailsFragmentScreenType.ADD
            val isViewMode = it == BaseDetailsFragmentScreenType.VIEW
            val isEditMode = it == BaseDetailsFragmentScreenType.EDIT

            // Save Menu
            saveMenu.isVisible = (isAddMode || isEditMode)

            // Edit Menu
            editMenu.isVisible = (isViewMode)

            // Delete Menu
            deleteMenu.isVisible = (isViewMode || isEditMode)
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.base_action_details_save -> {
                onSaveMenuClicked()
            }
            R.id.base_action_details_edit -> {
                onEditMenuClicked()
            }
            R.id.base_action_details_delete -> {
                onDeleteMenuClicked()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    abstract fun isDetailInputsAreValid() : Boolean

    protected open fun onSaveMenuClicked() {
        val isValid = isDetailInputsAreValid()

        if(isValid){
            viewModel.detailItem.value?.let {
                val isNewDeck = getItemId(it) == 0L

                if (isNewDeck) {
                    onCreateEntity(it)
                } else {
                    onUpdateEntity(it)
                }
            }
        }
    }

    protected open fun onCreateEntity(entity: T) {
        viewModel.createEntity(entity)
    }

    protected open fun onUpdateEntity(entity: T) {
        viewModel.updateEntity(entity)
    }

    protected fun onEditMenuClicked() {
        viewModel.screenType.postValue(BaseDetailsFragmentScreenType.EDIT)
    }

    protected fun onDeleteMenuClicked() {
        showDeleteDialog()
    }

    abstract fun getDeleteDialogTitle() : String
    abstract fun getDeleteDialogMessage() : String

    private fun showDeleteDialog() {
        if (deleteDialog == null) {
            deleteDialog = BaseAlertDialog.Builder(requireContext(), BaseAlertDialog.ButtonsType.POSITIVE_NEGATIVE)
                .setTitle(getDeleteDialogTitle())
                .setMessage(getDeleteDialogMessage())
                .setPositiveButtonText(R.string.base_general_delete)
                .setNegativeButtonText(R.string.base_general_cancel)
                .build()
        }
        deleteDialog!!.run {
            onAlertDialogPublishSubject
                .subscribe(onDeleteDialogEventPublishSubject)

            onAlertDialogVisibilityPublishSubject
                .subscribe(onDeleteDialogVisibilityBehaviorSubject)

            if (!isShowing()) {
                show()
            }
        }
    }

}