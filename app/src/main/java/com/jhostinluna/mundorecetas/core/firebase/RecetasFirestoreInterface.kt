package com.jhostinluna.mundorecetas.core.firebase

import com.jhostinluna.mundorecetas.features.fragments.Receta

interface RecetasFirestoreInterface {
    fun getRecetas():List<Receta>
    fun addReceta():Any
}