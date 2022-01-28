package com.jhostinluna.mundorecetas.core.navigation

import com.jhostinluna.mundorecetas.core.functional.DialogCallback

interface PopUpDelegator {

    fun showErrorWithRetry(title: String, message: String, positiveText: String,
                           negativeText: String, callback: DialogCallback
    )
}