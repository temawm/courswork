package com.example.reccomendation_app_courswork.Screens//package com.example.reccomendation_app_courswork.Screens
import android.net.Uri
import android.util.Log
import coil.compose.rememberImagePainter
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reccomendation_app_courswork.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun ProfileScreen() {

    var userName by remember { mutableStateOf<String?>(null) }
    var userEmail by remember { mutableStateOf<String?>(null) }
    var userDate by remember { mutableStateOf<String?>(null) }
    var userProfileImageUrl by remember { mutableStateOf<Uri?>(null) }
    var changeFields by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val firestore = Firebase.firestore
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val userRef = firestore.collection("Users").document(userId!!)


    LaunchedEffect(Unit) {
        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                userName = document.getString("name")
                userDate = document.getString("birthDate")
                userEmail = document.getString("email")
                val profileImageUrl = document.getString("profileImageUrl")
                Log.d("ProfileScreen", "profileImageUrl from Firestore: $profileImageUrl")
                userProfileImageUrl = profileImageUrl?.let { Uri.parse(it) }
                Log.d(
                    "ProfileScreen",
                    "User data loaded successfully: $userName, $userDate, $userEmail, $userProfileImageUrl"
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .padding(start = 12.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(32.dp)
            )
            Text(
                text = "Ваш профиль",
                modifier = Modifier
                    .wrapContentHeight()
                    .wrapContentWidth(),
                fontSize = 34.sp,
                textAlign = TextAlign.Center
            )
            Icon(
                painter = painterResource(id = R.drawable.settingsicon),
                contentDescription = "Settings Icon",
                tint = colorResource(id = R.color.authorizationMark),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Gray)
        )
        Spacer(modifier = Modifier.height(30.dp))
        if (userProfileImageUrl != null){
        SelectImageFromGallery(
            selectedImageUri = userProfileImageUrl,
            onImageSelected = { uri ->
                userProfileImageUrl = uri
                Log.d("SelectImageFromGallery Call", "Uri is null: uri = $uri")
                userProfileImageUrl?.let {
                    CoroutineScope(Dispatchers.IO).launch {
                        if (uploadImageToFirebaseStorage(it, userRef)) {
                            Log.d("uploadImageToFirebaseStorage", "Succesful")
                        } else {
                            Log.d("uploadImageToFirebaseStorage", "Failure")
                        }
                    }
                }

            }
        )
    }
        else {
            Box(
                modifier = Modifier
                    .size(196.dp)
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = colorResource(id = R.color.authorizationMark)
            )
            }
        }


        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            enabled = changeFields,
            value = if (userName != null) userName!! else "",
            onValueChange = { userName = it },
            label = { Text("Имя", color = Color.Gray) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 12.dp),
            placeholder = { Text(text = userName!!, color = Color.Black) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.LightGray

            )
        )
        TextField(
            enabled = changeFields,
            value = if (userEmail != null) userEmail!! else "",
            onValueChange = { userEmail = it },
            label = { Text("Почта", color = Color.Gray) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 12.dp),
            placeholder = { Text(text = userEmail!!, color = Color.Black) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.LightGray

            )
        )
        TextField(
            enabled = changeFields,
            value = if (userDate != null) userDate!! else "",
            onValueChange = { userDate = it },
            label = { Text("Дата рождения", color = Color.Gray) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 12.dp),
            placeholder = { Text(text = userDate!!, color = Color.Black) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.LightGray

            )
        )
        Spacer(modifier = Modifier.height(22.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (changeFields) {
                androidx.compose.material3.Button(
                    onClick = {
                        changeFields = false
                        updateInfo(userRef, userName!!, userDate!!, userEmail!!)
                    },
                    modifier = Modifier
                        .width(200.dp)
                        .height(55.dp)
                        .padding(start = 12.dp, end = 12.dp)
                        .border(
                            1.dp,
                            colorResource(id = R.color.authorizationMark),
                            RoundedCornerShape(15.dp)
                        ),
                    shape = RoundedCornerShape(15.dp),


                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.authorizationMark),
                        contentColor = Color.White,

                        )
                ) {
                    Text("Сохранить")
                }
                androidx.compose.material3.Button(
                    onClick = {
                        changeFields = false
                    },
                    modifier = Modifier
                        .width(210.dp)
                        .height(55.dp)
                        .padding(start = 12.dp, end = 12.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .border(
                            1.dp,
                            Color.LightGray,
                            RoundedCornerShape(15.dp)
                        ),
                    shape = RoundedCornerShape(15.dp),


                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.LightGray
                    )
                ) {
                    Text("Отменить")
                }
            } else {
                androidx.compose.material3.Button(
                    onClick = {
                        changeFields = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .padding(start = 12.dp, end = 12.dp)
                        .border(
                            1.dp,
                            colorResource(id = R.color.authorizationMark),
                            RoundedCornerShape(15.dp)
                        ),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = colorResource(id = R.color.authorizationMark)
                    )
                ) {
                    Text(
                        text = "Внести изменения",
                        color = colorResource(id = R.color.authorizationMark),
                        textAlign = TextAlign.Center
                    )
                }
            }

        }
        Spacer(modifier = Modifier.height(22.dp))
        Spacer(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(Color.Gray)
        )



    }
}

fun updateInfo(
    userRef: DocumentReference,
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

@Composable
fun SelectImageFromGallery(onImageSelected: (Uri?) -> Unit, selectedImageUri: Uri?) {
    Log.d("SelectImageFromGallery", "Started, uri = $selectedImageUri")
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            onImageSelected(uri)
        })
    Box(
        modifier = Modifier
            .size(196.dp)
            .clip(CircleShape)
            .padding(top = 16.dp)
            .clickable {
                launcher.launch("image/")
            }
    ) {
        if (selectedImageUri != null){
            Image(
                painter = rememberImagePainter(
                    data = selectedImageUri,
                ),
                contentDescription = "UserPhoto",
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = 1.3f,
                        scaleY = 1.3f
                    )
                    .align(Alignment.Center),
                contentScale = ContentScale.Crop
            )
        }
    }
    Log.d("SelectImageFromGallery", "Finished")

}

suspend fun uploadImageToFirebaseStorage(
    uri: Uri?,
    userRef: DocumentReference
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
