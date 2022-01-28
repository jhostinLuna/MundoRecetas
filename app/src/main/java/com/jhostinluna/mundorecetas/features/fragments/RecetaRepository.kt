package com.jhostinluna.mundorecetas.features.fragments

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.jhostinluna.mundorecetas.core.exception.Failure
import com.jhostinluna.mundorecetas.core.functional.Either
import com.jhostinluna.mundorecetas.core.platform.NetworkHandler
import com.jhostinluna.mundorecetas.core.platform.ServiceKOs
import okhttp3.internal.wait
import java.lang.Exception

interface RecetaRepository {
    fun getRecetas(): Either<Failure, List<Receta>>
    fun addReceta(receta:RecetaEntity): Either<Failure, Long>
    fun addRecetaFirestore(receta:Receta): Either<Failure,Boolean>
    fun getRemoteRecetas(): Either<Failure,List<Receta>>
    fun updateRecetaEntity(receta:RecetaEntity): Either<Failure, Int>
    class Network(
        private val networkHandler: NetworkHandler,
        private val firestore: FirebaseFirestore,
        private val recetasLocal: RecetasLocal
        ):RecetaRepository {
        val firestoreReference = firestore.collection("users")

        override fun getRecetas(): Either<Failure, List<Receta>> {
            return try {
                val lista = recetasLocal.getAllRecetas()
                if (lista.isNullOrEmpty()){
                    getRemoteRecetas()
                }else{
                    Either.Right(lista.map {
                        it.toReceta()
                    })
                }


            }catch (e: Exception){
                Either.Left(Failure.CustomError(ServiceKOs.DATABASE_ACCESS_ERROR, e.message))
            }
        }

        override fun getRemoteRecetas(): Either<Failure, List<Receta>> {
            val uid = Firebase.auth.currentUser!!.uid
            val collectionRecetas = firestoreReference.document(uid).collection("recetas")
            when(networkHandler.isConnected){
                true ->
                    return try {
                        val listReceta = ArrayList<Receta>()
                        /*
                        collectionRecetas.get().addOnSuccessListener { documents ->
                            for (document in documents){
                                listReceta.add(document.toObject())
                            }
                            Either.Right(listReceta)
                        }
                        */
                        val taskQuerySnapshot = collectionRecetas.get()
                        if (taskQuerySnapshot.isSuccessful){
                            val querySnapshot = Tasks.await(taskQuerySnapshot)
                        }
                        val querySnapshot = Tasks.await(taskQuerySnapshot)
                        for (document in querySnapshot){
                            listReceta.add(document.toObject())
                        }
                        val lista:List<Receta> = listReceta.toList()
                        listReceta.addAll(lista)
                        if (lista.isNotEmpty()){
                            addAllRecetas(lista.map { it.toRecetaEntity() })
                            Either.Right(lista)
                        }else{
                            Either.Right(emptyList())
                        }

                    }catch (e: Exception){
                        Either.Left(Failure.CustomError(ServiceKOs.DATABASE_ACCESS_ERROR, e.message))
                    }

                false, null ->return Either.Left(Failure.CustomError(ServiceKOs.DATABASE_ACCESS_ERROR,"ERROR FIRESTORE"))
            }
        }

        override fun updateRecetaEntity(receta: RecetaEntity): Either<Failure, Int> {
            return try {
                Either.Right(recetasLocal.updateReceta(receta))
            }catch (e: Exception){
                Either.Left(Failure.ServerError())
            }
        }

        private fun addAllRecetas(map: List<RecetaEntity>) {
            map.forEach {
                recetasLocal.addReceta(it)
            }
        }

        override fun addReceta(receta:RecetaEntity): Either<Failure, Long> {
            return try {

                addRecetaFirestore(receta.toReceta())
                Either.Right(recetasLocal.addReceta(receta))
            }catch (e: Exception){
                Either.Left(Failure.CustomError(ServiceKOs.DATABASE_ACCESS_ERROR, e.message))
            }
        }

        override fun addRecetaFirestore(receta: Receta): Either<Failure,Boolean> {
            val uid = Firebase.auth.currentUser!!.uid
            val docRecetas = firestoreReference.document(uid).collection("recetas")
            return try {
                if (docRecetas.add(receta).isSuccessful){
                    Either.Right(true)
                }else{
                    Either.Right(false)
                }
            }catch (e:Exception){
                Either.Left(Failure.ServerError())
            }

        }


    }
}