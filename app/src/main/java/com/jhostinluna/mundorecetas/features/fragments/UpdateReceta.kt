package com.jhostinluna.mundorecetas.features.fragments

import com.jhostinluna.mundorecetas.core.exception.Failure
import com.jhostinluna.mundorecetas.core.functional.Either
import com.jhostinluna.mundorecetas.core.interactor.UseCase

class UpdateReceta(val recetasRepository: RecetaRepository): UseCase<Int, UpdateReceta.Params>() {

    override suspend fun run(params: Params): Either<Failure, Int> = recetasRepository.updateRecetaEntity(params.recetaEntity)
    class Params(val recetaEntity: RecetaEntity)
}