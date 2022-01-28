package com.jhostinluna.mundorecetas.core.dao

import androidx.room.*
import com.jhostinluna.mundorecetas.features.fragments.RecetaEntity
@Dao
interface RecetasDao {
    @Insert
    fun insertReceta(receta: RecetaEntity):Long
    @Delete
    fun deleteReceta(receta: RecetaEntity)
    @Update
    fun updateReceta(receta: RecetaEntity): Int
    @Query("SELECT * FROM RecetaEntity")
    fun getAllRecetas(): List<RecetaEntity>
    @Query("SELECT * FROM RecetaEntity WHERE id=:idReceta")
    fun getRecetaById(idReceta: String): RecetaEntity

}