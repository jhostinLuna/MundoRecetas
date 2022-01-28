package com.jhostinluna.mundorecetas.features.fragments

import android.util.Log
import com.jhostinluna.mundorecetas.core.database.AppDatabase
import com.jhostinluna.mundorecetas.core.platform.ContextHandler
import com.jhostinluna.mundorecetas.features.fragments.RecetasDbLocal
import com.jhostinluna.mundorecetas.features.fragments.RecetaEntity

class RecetasLocal(val contextHandler: ContextHandler): RecetasDbLocal {
    private val recetaDao by lazy {
        AppDatabase.getAppDataBase(contextHandler.appContext)?.recetasDao()
    }
    override fun addReceta(receta: RecetaEntity): Long {
        val a =recetaDao?.insertReceta(receta)!!
        Log.d("addReceta",a.toString())
        return a
    }
    override fun getAllRecetas(): List<RecetaEntity> =recetaDao?.getAllRecetas()!!
    override fun updateReceta(receta: RecetaEntity):Int = recetaDao?.updateReceta(receta)!!

}