package com.jhostinluna.mundorecetas.core.plataform

import android.app.Activity
import androidx.lifecycle.ViewModelProvider
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jhostinluna.mundorecetas.R
import com.jhostinluna.mundorecetas.core.functional.DialogCallback
import com.jhostinluna.mundorecetas.core.navigation.PopUpDelegator
import org.koin.android.ext.android.inject

abstract class BaseFragment: androidx.fragment.app.Fragment() {

    private var popUpDelegator: PopUpDelegator? = null

    abstract fun layoutId(): Int

    private val viewModelFactory: ViewModelProvider.Factory by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(layoutId(), container, false)

    open fun onBackPressed() {

    }

    internal fun showProgress() = progressStatus(View.VISIBLE)

    internal fun hideProgress() = progressStatus(View.GONE)

    private fun progressStatus(viewStatus: Int) =
        with(activity) {
        }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        if (activity is PopUpDelegator) {
            this.popUpDelegator = activity
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is PopUpDelegator) {
            this.popUpDelegator = context
        }
    }

    internal fun showErrorWithRetry(title: String, message: String, positiveText: String,
                                    negativeText: String, callback: DialogCallback
    ) {
        popUpDelegator?.showErrorWithRetry(title, message, positiveText, negativeText, callback)
    }

    internal fun showError(errorCode: Int, errorMessage: String?, dialogCallback: DialogCallback) {
        val genericErrorTitle = getString(R.string.generic_error_title)
        val genericErrorMessage = getString(R.string.generic_error_body)
        showErrorWithRetry(
            genericErrorTitle,
            genericErrorMessage,
            getString(R.string.Retry),
            getString(R.string.Cancel),
            object : DialogCallback {
                override fun onDecline() = dialogCallback.onDecline()
                override fun onAccept() = dialogCallback.onAccept()
            })
    }
}