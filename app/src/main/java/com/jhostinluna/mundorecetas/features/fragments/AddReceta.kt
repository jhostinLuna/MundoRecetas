package com.jhostinluna.mundorecetas.features.fragments

import com.jhostinluna.mundorecetas.core.exception.Failure
import com.jhostinluna.mundorecetas.core.functional.Either
import com.jhostinluna.mundorecetas.core.interactor.UseCase

class AddReceta(val recetasRepository: RecetaRepository): UseCase<Long, AddReceta.Params>() {
    override suspend fun run(params: Params): Either<Failure, Long> = recetasRepository.addReceta(params.recetaEntity)
    class Params(val recetaEntity: RecetaEntity)

}