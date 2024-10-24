package com.example.reccomendation_app_courswork.Screens

import android.net.Uri
import android.provider.ContactsContract.Profile
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import coil.compose.rememberImagePainter
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class ProfileUiState(
    var userName: String? = "",
    var userEmail: String? = "",
    var userDate: String? = "",
    var profileImageUrl: Uri? = null,
    var changeFields: Boolean = false,
)

@HiltViewModel
class ProfileScreenViewModel @Inject constructor() : ViewModel() {
    val firestore = Firebase.firestore

    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val userRef = firestore.collection("Users").document(userId!!)
    private val _UiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _UiState

    init {
        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                _UiState.value = _UiState.value.copy(
                    userName = document.getString("name"),
                    userEmail = document.getString("email"),
                    userDate = document.getString("birthDate")
                )
                val profileImageUrl = document.getString("profileImageUrl")
                Log.d("ProfileScreen", "profileImageUrl from Firestore: $profileImageUrl")
                _UiState.value = _UiState.value.copy(
                    profileImageUrl = profileImageUrl?.let { Uri.parse(it) }
                )
                Log.d(
                    "ProfileScreen",
                    "User data loaded successfully: ${_UiState.value.userName}, ${_UiState.value.userEmail}, ${_UiState.value.userDate}, ${_UiState.value.profileImageUrl}"
                )

            } else {
                Log.d("ProfileScreen", "User data not found, initializing new user data")

                val userProfile = hashMapOf(
                    "name" to "",
                    "birthDate" to "",
                    "email" to "",
                    "profileImageUrl" to ""
                )
                userRef.set(userProfile)
            }
        }

    }

    suspend fun uploadImageToFirebaseStorage(
        uri: Uri?,
    ): Boolean {
        return try {
            if (uri != null) {
                val storageRef = Firebase.storage.reference
                val imageRef = storageRef.child("images/${uri.lastPathSegment}")
                imageRef.putFile(uri).await()
                val downloadUrl = getDownloadUrlForImage("images/${uri.lastPathSegment}")
                if (downloadUrl != null) {
                    userRef.update("profileImageUrl", downloadUrl).await()
                    Log.d("FirebaseStorage", "Image URL updated successfully: $downloadUrl")
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("FirebaseUpload", "Upload failed", e)
            false
        }
    }

    suspend fun getDownloadUrlForImage(imagePath: String): String? {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val imageRef = storageRef.child(imagePath)

        return try {
            val downloadUrl = imageRef.downloadUrl.await()
            downloadUrl.toString()
        } catch (e: Exception) {
            Log.e("FirebaseStorage", "Failed to get download URL", e)
            null
        }
    }

    fun updateInfo(
        userName: String,
        userDate: String,
        userEmail: String
    ): Boolean {
        return try {
            userRef.get().addOnSuccessListener { document ->
                val userProfile = hashMapOf(
                    "name" to userName,
                    "birthDate" to userDate,
                    "email" to userEmail,
                    "profileImageUrl" to ""
                )
                userRef.set(userProfile)
                Log.d("updateInfo", "User info updated successfully")

            }

            true
        } catch (e: Exception) {
            Log.d("updateInfo", "$e")
            false
        }
    }
}