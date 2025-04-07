package com.example.projecte_aplicaci_nativa_g1markzuckerberg.interfaz

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.JornadaActualResponse
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

    // Endpoint que devuelve la jornada actual
    @GET("api/v1/sportmonks/jornadaActual")
    suspend fun getJornadaActual(): Response<JornadaActualResponse>
}
