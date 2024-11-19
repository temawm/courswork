package com.example.reccomendation_app_courswork.Screens
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.reccomendation_app_courswork.R
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLEncoder


@Composable

fun ProfileScreen(profileScreenViewModel: ProfileScreenViewModel) {
    val scrollState = rememberScrollState()
    val uiState by profileScreenViewModel.uiState.collectAsState()

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
        }
        Spacer(modifier = Modifier.height(12.dp))
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Gray)
        )
        Spacer(modifier = Modifier.height(30.dp))
        if (uiState.profileImageUrl != null){
        SelectImageFromGallery(
            selectedImageUri = uiState.profileImageUrl,
            onImageSelected = { uri ->
                uiState.profileImageUrl = uri
                Log.d("SelectImageFromGallery Call", "Uri is null: uri = $uri")
                uiState.profileImageUrl?.let {
                    CoroutineScope(Dispatchers.IO).launch {
                        if (profileScreenViewModel.uploadImageToFirebaseStorage(it)) {
                            Log.d("uploadImageToFirebaseStorage", "Successful")
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
            enabled = uiState.changeFields,
            value = if (uiState.userName != null) uiState.userName!! else "",
            onValueChange = { uiState.userName = it },
            label = { Text("Имя", color = Color.Gray) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 12.dp),
            placeholder = { Text(text = uiState.userName!!, color = Color.Black) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.LightGray

            )
        )
        TextField(
            enabled = uiState.changeFields,
            value = if (uiState.userEmail != null) uiState.userEmail!! else "",
            onValueChange = { uiState.userEmail = it },
            label = { Text("Почта", color = Color.Gray) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 12.dp),
            placeholder = { Text(text = uiState.userEmail!!, color = Color.Black) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.LightGray

            )
        )
        TextField(
            enabled = uiState.changeFields,
            value = if (uiState.userDate != null) uiState.userDate!! else "",
            onValueChange = { uiState.userDate = it },
            label = { Text("Дата рождения", color = Color.Gray) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 12.dp),
            placeholder = { Text(text = uiState.userDate!!, color = Color.Black) },
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
            if (uiState.changeFields) {
                androidx.compose.material3.Button(
                    onClick = {
                        uiState.changeFields = false
                        profileScreenViewModel.updateInfo(uiState.userName!!, uiState.userDate!!, uiState.userEmail!!)
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
                        uiState.changeFields = false
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
                        uiState.changeFields = true
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
        ) {

//             { * ->
//                 Card(
//                    modifier = Modifier
//                        .width(230.dp)
//                        .height(350.dp)
//                        .padding(6.dp)
//                        .clickable {
//                        },
//                    elevation = CardDefaults.cardElevation(
//                        defaultElevation = 12.dp
//                    ),
//                    colors = CardDefaults.cardColors(
//                        containerColor = Color.White
//                    )
//                ) {
//                     Image(
//                            bitmap = imageBitmap!!.asImageBitmap(),
//                            contentDescription = null,
//                            modifier = Modifier
//                                .height(220.dp)
//                                .width(180.dp)
//                                .padding(start = 10.dp, end = 10.dp, top = 10.dp)
//                        )
//                        Column(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(start = 5.dp, end = 5.dp, top = 7.dp),
//                            verticalArrangement = Arrangement.Top,
//                            horizontalAlignment = Alignment.Start
//                        ) {
//                            Text(
//                                text = ,
//                                fontSize = 16.sp,
//                                color = Color.Black,
//                                maxLines = 1
//                            )
//                            Spacer(modifier = Modifier.height(2.dp))
//                            Text(
//                                text = ?: "Автор неизвестен",
//                                fontSize = 12.sp,
//                                color = Color.LightGray,
//                                maxLines = 1
//                            )
//                            Spacer(modifier = Modifier.height(2.dp))
//                            Text(
//                                modifier = Modifier
//                                    .fillMaxWidth(),
//                                text = ?: " ",
//                                textAlign = TextAlign.Start,
//                                fontSize = 9.sp,
//                                color = Color.Gray,
//                                maxLines = 1
//                            )
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .wrapContentHeight()
//                                    .padding(top = 10.dp),
//                                horizontalArrangement = Arrangement.SpaceBetween,
//                                verticalAlignment = Alignment.CenterVertically
//                            ){
//                                Spacer(modifier = Modifier.width(8.dp))
//                                Icon(
//                                    painter = painterResource(id = R.drawable.addicon),
//                                    contentDescription = "Add a book",
//                                    tint = Color.Black,
//                                    modifier = Modifier.size(36.dp)
//                                )
//                                Text(
//                                    text = "|",
//                                    textAlign = TextAlign.Center,
//                                    color = Color.LightGray,
//                                    fontSize = 18.sp
//                                )
//                                Icon(
//                                    painter = painterResource(id = R.drawable.favoriteheart),
//                                    contentDescription = "Favorite",
//                                    tint = if(enabledHeart) Color.Red else Color.Gray,
//                                    modifier = Modifier
//                                        .size(36.dp)
//                                        .clickable {
//                                            enabledHeart = !enabledHeart
//                                            }
//                                )
//                                Spacer(modifier = Modifier.width(8.dp))
//                            }
//                        }
//
//                }
//            }
        }



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