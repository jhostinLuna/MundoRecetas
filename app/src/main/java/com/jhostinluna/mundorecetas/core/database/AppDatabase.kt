package com.jhostinluna.mundorecetas.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jhostinluna.mundorecetas.core.dao.RecetasDao
import com.jhostinluna.mundorecetas.features.fragments.RecetaEntity

@Database(entities = arrayOf(RecetaEntity::class), version = 2)
abstract class AppDatabase: RoomDatabase() {
    abstract fun recetasDao(): RecetasDao
    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getAppDataBase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java,
                        "Recetas")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase() {
            INSTANCE = null
        }
    }
}