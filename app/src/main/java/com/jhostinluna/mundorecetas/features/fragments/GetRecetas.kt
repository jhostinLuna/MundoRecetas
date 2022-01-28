package com.jhostinluna.mundorecetas.features.fragments

import com.jhostinluna.mundorecetas.core.exception.Failure
import com.jhostinluna.mundorecetas.core.functional.Either
import com.jhostinluna.mundorecetas.interactor.UseCase

class GetRecetas(val recetasRepository: RecetaRepository): UseCase<List<Receta>,GetRecetas.Params>() {
    override suspend fun run(params: Params): Either<Failure, List<Receta>> = recetasRepository.getRecetas()
    class Params
}