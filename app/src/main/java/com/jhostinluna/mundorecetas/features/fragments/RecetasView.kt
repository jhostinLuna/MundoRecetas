package com.jhostinluna.mundorecetas.features.fragments

data class RecetasView(
    val id:String,
    val nombre:String?,
    val categoria:String?,
    val ingredientes:List<String>?,
    val preparacion:String?,
    val date:String?,
    val foto:String?)