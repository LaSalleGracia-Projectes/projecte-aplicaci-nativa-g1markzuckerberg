package com.example.projecte_aplicaci_nativa_g1markzuckerberg.Interface

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.CreateLigaRequest
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.CreateLigaResponse
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.JoinLigaResponse
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.LigaUsersResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface LigaService {
    @POST("api/v1/liga/create")
    suspend fun createLiga(
        @Body request: CreateLigaRequest
    ): Response<CreateLigaResponse>

    @POST("api/v1/liga/join/{ligaCode}")
    suspend fun joinLiga(
        @Path("ligaCode") ligaCode: String
    ): Response<JoinLigaResponse>

    @GET("api/v1/liga/users/{ligaCode}")
    suspend fun getUsersByLiga(
        @Path("ligaCode") ligaCode: String,
        @Query("jornada") jornada: Int? = null
    ): Response<LigaUsersResponse>
    @GET("api/v1/liga/image/{ligaId}")
    suspend fun getLeagueImage(
        @Path("ligaId") ligaId: String
    ): Response<ResponseBody>

    @DELETE("api/v1/liga/leave/{ligaId}")
    suspend fun leaveLiga(
        @Path("ligaId") ligaId: String
    ): Response<ResponseBody>

    @Multipart
    @PUT("api/v1/liga/{ligaId}/upload-image")
    suspend fun uploadLeagueImage(
        @Path("ligaId") ligaId: String,
        @Part image: MultipartBody.Part
    ): Response<ResponseBody>

}