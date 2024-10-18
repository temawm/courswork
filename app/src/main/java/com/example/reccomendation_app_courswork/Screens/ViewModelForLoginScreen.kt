package com.example.reccomendation_app_courswork.Screens

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: Boolean = false,
    val passwordError: Boolean = false,
    val signInOrUp: Boolean = true,
    val enabledContinueButton: Boolean = true,
    val connectionInternet: Boolean = true,
    val isLoadingContext: Boolean = false,
    val showPopup: Boolean = false,
    val failedSignUp: Boolean? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {


    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun showPopup() {
        _uiState.value = _uiState.value.copy(showPopup = true)
    }

    fun dismissPopup() {
        _uiState.value = _uiState.value.copy(showPopup = false)
    }

    fun onEmailChanged(newEmail: String) {
        _uiState.value = _uiState.value.copy(email = newEmail, emailError = newEmail.isEmpty())
    }

    fun onPasswordChanged(newPassword: String) {
        _uiState.value = _uiState.value.copy(password = newPassword, passwordError = newPassword.isEmpty())
    }

    fun toggleSignInOrUp(isSignIn: Boolean) {
        _uiState.value = _uiState.value.copy(signInOrUp = isSignIn)
    }

    fun updateContinueButtonState(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(enabledContinueButton = enabled)
    }

    fun updateConnectionState(isConnected: Boolean) {
        _uiState.value = _uiState.value.copy(connectionInternet = isConnected)
    }

    fun createDatabase(firestore: FirebaseFirestore, email: String): Boolean {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val userRef = firestore.collection("Patients").document(userId!!)

            viewModelScope.launch {
                if (!userRef.get().await().exists()) {
                    val userProfile = hashMapOf(
                        "name" to "",
                        "birthDate" to "",
                        "email" to email,
                        "profileImageUrl" to ""
                    )
                    userRef.set(userProfile)
                }
            }
            true
        } catch (e: Exception) {
            Log.d("CreateDatabase", "$e")
            false
        }
    }

    suspend fun signUpAndVerifyEmail(auth: FirebaseAuth, email: String, password: String): Boolean {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            auth.currentUser?.sendEmailVerification()?.await()
            true
        } catch (e: Exception) {
            Log.d("MyAuthLog", "SignUp failed: ${e.message}")
            false
        }
    }

    fun signIn(auth: FirebaseAuth, email: String, password: String, navController: NavController) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("MyAuthLog", "SignIn is successful!")
                    navController.navigate("HomeScreen") {
                        popUpTo("LoginScreen") {
                            inclusive = true
                        }
                    }
                } else {
                    Log.d("MyAuthLog", "SignIn is failure!")
                }
            }
    }


    private fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validatePassword(password: String): Boolean {
        return password.length >= 8
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    fun onContinueButtonClick(context: Context, navController: NavController) {
        viewModelScope.launch {
            val email = _uiState.value.email
            val password = _uiState.value.password

            if (!validateEmail(email)) {
                _uiState.value = _uiState.value.copy(emailError = true)
                return@launch
            }

            if (!validatePassword(password)) {
                _uiState.value = _uiState.value.copy(passwordError = true)
                return@launch
            }

            if (!isNetworkAvailable(context)) {
                return@launch
            }

            if (_uiState.value.signInOrUp) {
                signIn(FirebaseAuth.getInstance(), email, password, navController)
            } else {
                val signUpSuccess = signUpAndVerifyEmail(FirebaseAuth.getInstance(), email, password)
                if (signUpSuccess) {
                    createDatabase(FirebaseFirestore.getInstance(), email)
                } else {
                    _uiState.value = _uiState.value.copy(failedSignUp = true)
                }
            }
        }
    }
}
