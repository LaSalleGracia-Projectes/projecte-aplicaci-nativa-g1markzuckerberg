package com.example.projecte_aplicaci_nativa_g1markzuckerberg.Interface

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.ApiResponse
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.ContactFormRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ContactService {
    @POST("api/v1/contactForm/create")
    suspend fun sendContactForm(
        @Body request: ContactFormRequest
    ): Response<ApiResponse>
}