package com.example.projecte_aplicaci_nativa_g1markzuckerberg.Interface

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface UserService {

    /* --- queries --- */
    @GET("api/v1/user/leagues")
    suspend fun getUserLeagues(): Response<UserLigaResponse>

    @GET("api/v1/user/me")
    suspend fun getMe(): Response<UserMeResponse>

    @GET("api/v1/user/get-image")
    suspend fun getUserImage(@Query("userId") userId: String): Response<ResponseBody>


    /* --- image upload --- */
    @Multipart
    @POST("api/v1/user/upload-image")
    suspend fun uploadUserImage(@Part image: MultipartBody.Part): Response<SimpleApiResponse>


    /* --- forgot password --- */
    @POST("api/v1/user/forgot-password")
    suspend fun forgotPassword(@Body req: ForgotPasswordRequest): Response<ForgotPasswordResponse>


    /* --- profile updates --- */
    @PUT("api/v1/user/update-username")
    suspend fun updateUsername(@Body req: UpdateUsernameRequest): Response<SimpleApiResponse>

    @PUT("api/v1/user/update-birthDate")
    suspend fun updateBirthDate(@Body req: UpdateBirthDateRequest): Response<SimpleApiResponse>

    @PUT("api/v1/user/update-password")
    suspend fun updatePassword(@Body req: UpdatePasswordRequest): Response<SimpleApiResponse>
}
