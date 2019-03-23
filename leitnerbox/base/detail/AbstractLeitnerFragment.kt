package com.kecsot.leitnerbox.base.detail

import com.kecsot.basekecsot.view.AbstractFragment
import com.kecsot.basekecsot.wrapper.permission.PermissionUtil
import com.kecsot.leitnerbox.R

open class AbstractLeitnerFragment : AbstractFragment() {

    protected fun requirePermission(vararg permissions: String, onSuccess: (result: PermissionUtil.Result) -> Unit) {
        PermissionUtil
            .requestEachPermission(
                this,
                *permissions
            ) {
                when (it) {
                    PermissionUtil.Result.DENIED -> {
                        showSnackBar(R.string.error_permission_denied_issue_message)
                    }
                    PermissionUtil.Result.DENIED_FOREVER -> {
                        showSnackBar(R.string.error_permission_denied_neverask_issue_message)
                    }
                    else -> {

                    }
                }
                onSuccess(it)
            }.addToCompositeDisposable()
    }

}