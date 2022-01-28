package com.jhostinluna.mundorecetas.core.di

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jhostinluna.mundorecetas.BuildConfig
import com.jhostinluna.mundorecetas.Data
import com.jhostinluna.mundorecetas.core.platform.ContextHandler
import com.jhostinluna.mundorecetas.core.platform.NetworkHandler
import com.jhostinluna.mundorecetas.features.fragments.RecetasLocal
import com.jhostinluna.mundorecetas.features.fragments.*
import com.jhostinluna.mundorecetas.features.fragments.RecetaService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    factory { ContextHandler(get()) }
    factory { NetworkHandler(get()) }
    single {
        Retrofit.Builder()
            .baseUrl(Data.URL_BASE)
            .client(createClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    //Si necesitamos el builder para proporcionarle otra urlbase
    single {
        Retrofit.Builder()
            .client(createClient())
            .addConverterFactory(GsonConverterFactory.create())
    }
}
val databaseModule = module {
    /*
    fun provideDataBase(application: Application): AppDatabase {
        return Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "recetasDB"
        ).build()
    }

    fun provideDao(dataBase: AppDatabase): RecetasDao {
        return dataBase.recetasDao()
    }
    single { provideDataBase(get()) }
    single { provideDao(get())}

     */
    factory { RecetasLocal(get()) }
    }
val useCaseModule = module {
    factory { AddReceta(get()) }
}
val dataServiceModule = module {
    factory { RecetaService(get()) }
}
val googleSignModule = module {
    scope(named<Loggin>()) {
        factory {

        }
    }
}
val viewModelModule = module {
    viewModel {
        SaredViewModel(get())
    }

}
val firestoreModule = module {
    single { Firebase.firestore }
}
val repositoryModule = module {
    factory<RecetaRepository> { RecetaRepository.Network(get(), get(),get()) }
}
val viewModelCompartidoModule = module {
    scope(named<CreateRecetaFragment>()){
        viewModel {
            AddRecetaViewModel(get())
        }
    }
}
val myRecetasAdapterModule = module {
    scope(named<MyRecetasFragment>()){
        factory { MyRecetasFragmentAdapter(androidContext()) }
    }
}


private fun createClient(): OkHttpClient {
    val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
    if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        okHttpClientBuilder.addInterceptor(loggingInterceptor)
    }
    return okHttpClientBuilder.build()
}


