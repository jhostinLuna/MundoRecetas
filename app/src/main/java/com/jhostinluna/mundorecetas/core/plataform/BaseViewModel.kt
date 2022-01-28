package com.jhostinluna.mundorecetas.core.plataform

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jhostinluna.mundorecetas.core.exception.Failure

abstract class BaseViewModel : ViewModel() {

    var failure: MutableLiveData<Failure> = MutableLiveData()

    protected fun handleFailure(failure: Failure) {
        this.failure.value = failure
    }
}