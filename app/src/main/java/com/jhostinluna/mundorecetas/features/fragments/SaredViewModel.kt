package com.jhostinluna.mundorecetas.features.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jhostinluna.mundorecetas.core.plataform.BaseViewModel

class SaredViewModel(val repository: RecetaRepository): BaseViewModel() {
    val updateReceta:UpdateReceta = UpdateReceta(repository)
    val addReceta:AddReceta = AddReceta(repository)
    val getRecetas:GetRecetas = GetRecetas(repository)
    val recetas: MutableLiveData<List<Receta>> by lazy {
        MutableLiveData<List<Receta>>().also { loadRecetas() }
    }
    var listRecetas:List<Receta> = emptyList()
    fun getlistRecetas():LiveData<List<Receta>> = recetas
    fun addReceta(recetaEntity: RecetaEntity) = addReceta.invoke(
        AddReceta.Params(recetaEntity)){
        it.fold(::handleFailure,::handleRecetaResult)
    }
    private fun handleRecetaResult(l: Long){
        Log.d("handleRecetaUpdate",l.toString())
    }
    private fun handleRecetaUpdate(i: Int){
        Log.d("handleRecetaResult",i.toString())
    }
    fun loadRecetas() = getRecetas.invoke(
        GetRecetas.Params()){
        it.fold(::handleFailure,::handleRecetasList)
    }

    private fun handleRecetasList(list: List<Receta>){
        list.forEach {
            Log.d("handleRecetasList",it.toString())
        }

        listRecetas = list
        recetas.postValue(list)
    }

    fun getRecetaById(idReceta:String): Receta {
        return listRecetas.filter { it-> it.id == idReceta }.single()
    }

    fun updateReceta(recetaEntity: RecetaEntity) = updateReceta.invoke(
        UpdateReceta.Params(recetaEntity)){
        it.fold(::handleFailure,::handleRecetaUpdate)
    }


}