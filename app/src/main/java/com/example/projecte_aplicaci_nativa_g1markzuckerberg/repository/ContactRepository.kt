package com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.Interface.ContactService
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.ContactFormRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactRepository(
    private val service: ContactService        // inyectado
) {

    suspend fun sendContactForm(message: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val res = service.sendContactForm(ContactFormRequest(message))
                if (res.isSuccessful) Result.success(Unit)
                else Result.failure(Exception(res.errorBody()?.string() ?: "Error ${res.code()}"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
