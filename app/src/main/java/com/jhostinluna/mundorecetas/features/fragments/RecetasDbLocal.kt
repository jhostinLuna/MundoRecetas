package com.jhostinluna.mundorecetas.features.fragments

interface RecetasDbLocal {
    fun addReceta(receta: RecetaEntity):Long
    fun getAllRecetas():List<RecetaEntity>
    fun updateReceta(receta: RecetaEntity):Int
}