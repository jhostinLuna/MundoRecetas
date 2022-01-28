package com.jhostinluna.mundorecetas.features.fragments

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.util.*

@Entity
data class RecetaEntity(
    @PrimaryKey(autoGenerate = false) var id:String = UUID.randomUUID().toString(),
    @ColumnInfo var nombre:String? = "",
    @ColumnInfo var categoria:String? = "",
    @ColumnInfo var ingredientes:String? = "",
    @ColumnInfo var preparacion:String? = "",
    @ColumnInfo var date:String? = "",
    @ColumnInfo var foto:String? = ""
    ){
    fun toReceta():Receta {
        val gson = GsonBuilder().create()
        val lisType: TypeToken<List<String?>?> = object : TypeToken<List<String?>?>() {}
        var lista:List<String> = listOf()
        if (!ingredientes.isNullOrEmpty())
            lista = gson.fromJson<List<String>>(ingredientes, lisType.type).toList()

        return Receta(id,nombre,categoria, lista,preparacion,date,foto)
    }
}