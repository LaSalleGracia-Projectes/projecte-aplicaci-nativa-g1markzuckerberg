package com.example.projecte_aplicaci_nativa_g1markzuckerberg.api

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.*
import retrofit2.Response
import retrofit2.http.*

interface DraftService {

    @POST("api/v1/draft/create")
    suspend fun createDraft(@Body request: CreateDraftRequest): Response<CreateDraftResponse>

    @GET("api/v1/draft/tempDraft/{plantillaId}")
    suspend fun getTempDraft(@Path("plantillaId") plantillaId: Int): Response<CreateDraftResponse>

    @POST("api/v1/draft/saveDraft")
    suspend fun saveDraft(@Body request: SaveDraftRequest): Response<ApiResponse>
}
