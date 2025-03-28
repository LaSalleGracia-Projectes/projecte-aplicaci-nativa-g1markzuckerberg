package com.example.projecte_aplicaci_nativa_g1markzuckerberg.interfaz

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.JornadaResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SportMonksService {

    // Ejemplo: obtener una jornada concreta usando @Path
    @GET("api/v1/sportmonks/jornadas/{jornada}")
    suspend fun getJornada(
        @Path("jornada") jornada: String
    ): Response<JornadaResponse>

    // Ejemplo: obtener siempre la misma jornada (28)
    @GET("api/v1/sportmonks/jornadas/28")
    suspend fun getJornadaFixtures(): Response<JornadaResponse>
}
