package com.jhostinluna.mundorecetas.features.fragments

import com.jhostinluna.mundorecetas.features.fragments.RecetaEntity
import retrofit2.Call
import retrofit2.http.GET

interface RecetasApi {
    @GET("")
    fun getRecetas(): Call<List<RecetaEntity>>
}