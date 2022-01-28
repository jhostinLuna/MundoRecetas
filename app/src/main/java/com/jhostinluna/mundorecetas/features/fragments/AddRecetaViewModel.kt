package com.jhostinluna.mundorecetas.features.fragments

import android.util.Log
import com.jhostinluna.mundorecetas.core.plataform.BaseViewModel

class AddRecetaViewModel(val addReceta: AddReceta): BaseViewModel() {
    fun addReceta(recetaEntity: RecetaEntity) = addReceta.invoke(AddReceta.Params(recetaEntity)){
        it.fold(::handleFailure,::handleRecetaResult)
    }

    private fun handleRecetaResult(l: Long){
        Log.d("handleRecetaResult",l.toString())

    }
}