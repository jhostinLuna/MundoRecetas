package com.jhostinluna.mundorecetas

import android.app.Application
import com.jhostinluna.mundorecetas.core.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AndroidApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@AndroidApplication)
            modules(listOf(
                networkModule
            ))
        }
    }
}
