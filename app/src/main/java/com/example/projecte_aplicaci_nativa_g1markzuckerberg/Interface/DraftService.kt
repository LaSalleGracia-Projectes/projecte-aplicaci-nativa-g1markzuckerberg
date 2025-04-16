package com.example.projecte_aplicaci_nativa_g1markzuckerberg.api

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.*
import retrofit2.Response
import retrofit2.http.*

interface DraftService {

    @POST("api/v1/draft/create")
    suspend fun createDraft(@Body request: CreateDraftRequest): Response<CreateDraftResponse>

    @PUT("api/v1/draft/update/{ligaId}")
    suspend fun updateDraft(
        @Path("ligaId") ligaId: Int,
        @Body request: UpdateDraftRequest
    ): Response<ApiResponse>

    @POST("api/v1/draft/saveDraft")
    suspend fun saveDraft(@Body request: SaveDraftRequest): Response<ApiResponse>

    @GET("api/v1/draft/tempDraft/{ligaId}")
    suspend fun getTempDraft(
        @Path("ligaId") ligaId: Int,
        @Query("roundName") roundName: String? = null
    ): Response<GetTempDraftResponse>
}
