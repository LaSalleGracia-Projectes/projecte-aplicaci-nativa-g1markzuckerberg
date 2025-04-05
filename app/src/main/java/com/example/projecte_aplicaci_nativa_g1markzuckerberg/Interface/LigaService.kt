package com.example.projecte_aplicaci_nativa_g1markzuckerberg.Interface

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.CreateLigaRequest
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.CreateLigaResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LigaService {
    @POST("api/v1/liga/create")
    suspend fun createLiga(
        @Body request: CreateLigaRequest
    ): Response<CreateLigaResponse>
}