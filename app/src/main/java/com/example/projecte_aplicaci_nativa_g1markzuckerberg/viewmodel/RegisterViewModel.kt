package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient.authRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private lateinit var googleSignInClient: GoogleSignInClient

    fun initGoogle(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken("884876575294-4t7eid7marm9u9s2u9mb7agv5i1k4lgu.apps.googleusercontent.com") // <- Este lo sacas de la consola de Google
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getGoogleSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }
    fun handleGoogleToken(idToken: String, onSuccess: () -> Unit) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.loginWithGoogle(idToken)
            _isLoading.value = false
            result.onSuccess {
                onSuccess()
            }.onFailure {
                _errorMessage.value = it.message
            }
        }
    }

}