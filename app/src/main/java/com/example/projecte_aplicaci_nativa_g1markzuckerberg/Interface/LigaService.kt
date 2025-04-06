package com.example.projecte_aplicaci_nativa_g1markzuckerberg.Interface

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.CreateLigaRequest
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.CreateLigaResponse
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.JoinLigaResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface LigaService {
    @POST("api/v1/liga/create")
    suspend fun createLiga(
        @Body request: CreateLigaRequest
    ): Response<CreateLigaResponse>

    @POST("api/v1/liga/join/{ligaCode}")
    suspend fun joinLiga(
        @Path("ligaCode") ligaCode: String
    ): Response<JoinLigaResponse>
}