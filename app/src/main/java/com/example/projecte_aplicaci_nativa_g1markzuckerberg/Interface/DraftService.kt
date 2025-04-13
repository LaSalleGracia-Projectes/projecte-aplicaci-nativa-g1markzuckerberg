package com.example.projecte_aplicaci_nativa_g1markzuckerberg.api

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.*
import retrofit2.Response
import retrofit2.http.*

interface DraftService {

    @POST("api/v1/draft/create")
    suspend fun createDraft(@Body request: CreateDraftRequest): Response<TempPlantillaResponse>

    @PUT("api/v1/draft/update")
    suspend fun updateDraft(@Body request: UpdateDraftRequest): Response<ApiResponse>

    @POST("api/v1/draft/saveDraft")
    suspend fun saveDraft(@Body request: SaveDraftRequest): Response<ApiResponse>

    @GET("api/v1/draft/getuserDraft")
    suspend fun getUserDraft(@Query("roundName") roundName: String? = null): Response<GetDraftResponse>
}
