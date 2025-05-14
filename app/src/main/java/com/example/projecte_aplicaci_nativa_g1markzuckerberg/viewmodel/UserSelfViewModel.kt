package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.Interface.UserService
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File

/* ---------- estados UI ---------- */
sealed interface UserSelfUiState {
    object Loading : UserSelfUiState
    data class Error(val message: String) : UserSelfUiState
    data class Ready(
        val user: UserInfo,
        val leagues: List<LigaConPuntos>,
        val selectedLeague: LigaConPuntos,
        val avatarStamp: Long          // ⬅ cambia al subir nueva imagen
    ) : UserSelfUiState
}

sealed interface UserEditState {
    object Idle : UserEditState
    object Loading : UserEditState
    data class Error(val message: String) : UserEditState
    object Done : UserEditState
}

class UserSelfViewModel(private val api: UserService) : ViewModel() {

    private val _ui   = MutableStateFlow<UserSelfUiState>(UserSelfUiState.Loading)
    val    state: StateFlow<UserSelfUiState> = _ui

    private val _edit = MutableStateFlow<UserEditState>(UserEditState.Idle)
    val edit: StateFlow<UserEditState>      = _edit

    init { refreshAll() }

    /* ---------------- refresh ---------------- */
    private fun refreshAll() = viewModelScope.launch {
        try {
            val user    = api.getMe().body()!!.user
            val leagues = api.getUserLeagues().body()!!.leagues
            _ui.value = UserSelfUiState.Ready(
                user,
                leagues,
                leagues.firstOrNull() ?: LigaConPuntos(-1,"Sin ligas",0,"",0,"","0","0"),
                System.currentTimeMillis()      // primera carga
            )
        } catch (e: Exception) {
            _ui.value = UserSelfUiState.Error(e.localizedMessage ?: "Error")
        }
    }

    /* ---------------- helpers ---------------- */
    private fun runEdit(block: suspend () -> Unit) = viewModelScope.launch {
        _edit.value = UserEditState.Loading
        try {
            block()
            _edit.value = UserEditState.Done
            refreshAll()
        } catch (e: HttpException) {
            val errorMsg = e.response()?.errorBody()?.string() ?: e.message()
            _edit.value = UserEditState.Error(errorMsg)
        } catch (e: Exception) {
            _edit.value = UserEditState.Error(e.localizedMessage ?: "Error desconocido")
        }
    }

    /* limpiar error manualmente */
    fun clearEditError() { _edit.value = UserEditState.Idle }


    /* ---------------- acciones externas ---------------- */
    fun selectLeague(l: LigaConPuntos) = _ui.update {
        if (it is UserSelfUiState.Ready) it.copy(selectedLeague = l) else it
    }

    fun updateUsername(name: String) = runEdit {
        api.updateUsername(UpdateUsernameRequest(name))
    }

    fun updateBirth(dateIso: String) = runEdit {
        api.updateBirthDate(UpdateBirthDateRequest(dateIso))
    }

    class PasswordUpdateException(val code: String) : Exception(code)

    fun updatePassword(old: String, new: String, confirm: String) = runEdit {
        try {
            val response = api.updatePassword(UpdatePasswordRequest(old, new, confirm))

            if (!response.isSuccessful) {
                val body = response.errorBody()?.string().orEmpty()
                val code = when {
                    "Incorrect current password" in body       -> "INCORRECT_CURRENT_PASSWORD"
                    "All password fields are required" in body -> "PASSWORD_FIELDS_REQUIRED"
                    "do not match" in body                     -> "PASSWORDS_DO_NOT_MATCH"
                    else                                       -> "DATABASE_ERROR_UPDATING_PASSWORD"
                }
                throw PasswordUpdateException(code)
            }
        } catch (e: HttpException) {
            val body = e.response()?.errorBody()?.string().orEmpty()
            val code = when {
                "Incorrect current password" in body       -> "INCORRECT_CURRENT_PASSWORD"
                "All password fields are required" in body -> "PASSWORD_FIELDS_REQUIRED"
                "do not match" in body                     -> "PASSWORDS_DO_NOT_MATCH"
                else                                       -> "DATABASE_ERROR_UPDATING_PASSWORD"
            }
            throw PasswordUpdateException(code)
        } catch (e: PasswordUpdateException) {
            // re-lanzamos tal cual para que runEdit lo capture con el código
            throw e
        } catch (e: Exception) {
            // cualquier otro error
            throw PasswordUpdateException("DATABASE_ERROR_UPDATING_PASSWORD")
        }
    }

    fun uploadImage(file: File) = runEdit {
        val part = MultipartBody.Part.createFormData(
            "image", file.name, file.asRequestBody("image/*".toMediaTypeOrNull())
        )
        api.uploadUserImage(part)
        /* Fuerza recarga de la imagen cacheando con timestamp */
        _ui.update { cur ->
            if (cur is UserSelfUiState.Ready)
                cur.copy(avatarStamp = System.currentTimeMillis())
            else cur
        }
    }

    /* ---------------- factory ---------------- */
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(c: Class<T>): T =
            UserSelfViewModel(RetrofitClient.userService) as T
    }

}
