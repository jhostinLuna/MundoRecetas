package com.jhostinluna.mundorecetas.features.fragments

import retrofit2.Call
import retrofit2.Retrofit

class RecetaService constructor(val retrofit: Retrofit) : RecetasApi {
    private val recetasApi by lazy {retrofit.create(RecetasApi::class.java)}
    override fun getRecetas(): Call<List<RecetaEntity>> {
        return recetasApi.getRecetas()
    }
}