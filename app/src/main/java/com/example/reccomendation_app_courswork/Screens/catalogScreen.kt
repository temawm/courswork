package com.example.reccomendation_app_courswork.Screens

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.reccomendation_app_courswork.R
import com.example.reccomendation_app_courswork.googleBooks.BookItem
import com.example.reccomendation_app_courswork.googleBooks.createGoogleBooksService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CatalogScreen() {
    var topBooks by remember { mutableStateOf<List<BookItem>>(emptyList()) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(Unit) {
        topBooks = withContext(Dispatchers.IO) {
            fetchTopBooks()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        if (topBooks.isNotEmpty()) {
            var enabledHeart by remember { mutableStateOf(false) }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    ) {

                items(topBooks) { book ->
                    Card (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 12.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ){
                        Column(
                            modifier = Modifier
                                .width(230.dp)
                                .height(350.dp)
                                .background(Color.White),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            val imageUrl = book.volumeInfo.imageLinks?.thumbnail
                            if (!imageUrl.isNullOrEmpty()) {
                                Glide.with(LocalContext.current)
                                    .asBitmap()
                                    .load(imageUrl)
                                    .into(object : CustomTarget<Bitmap>() {
                                        override fun onResourceReady(
                                            resource: Bitmap,
                                            transition: Transition<in Bitmap>?
                                        ) {
                                            imageBitmap = resource
                                        }

                                        override fun onLoadCleared(placeholder: Drawable?) {

                                        }
                                    })
                                imageBitmap?.let { bitmap ->
                                    Image(
                                        bitmap = bitmap.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .height(250.dp)
                                            .width(180.dp)
                                            .padding(start = 10.dp, end = 10.dp, top = 10.dp)
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 5.dp, end = 5.dp, top = 7.dp),
                                    verticalArrangement = Arrangement.Top,
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        text = book.volumeInfo.title,
                                        fontSize = 16.sp,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = book.volumeInfo.authors?.get(0)!!,
                                        fontSize = 12.sp,
                                        color = Color.LightGray
                                    )
                                    Text(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                    text = book.volumeInfo.publishedDate,
                                    textAlign = TextAlign.Start,
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row (
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(32.dp)
                                            .padding(start = 2.dp, end = 2.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ){
                                        Icon(
                                            painter = painterResource(id = R.drawable.addicon) ,
                                            contentDescription = "AddIcon",
                                            tint = Color.Black,
                                            modifier = Modifier
                                                .size(32.dp)
                                        )
                                        Text(
                                            modifier = Modifier
                                                .wrapContentWidth()
                                                .padding(start = 25.dp, end = 25.dp),
                                            text = "|",
                                            textAlign = TextAlign.Center,
                                            fontSize = 35.sp,
                                            color = Color.Gray
                                        )
                                        Icon(
                                            painter = painterResource(id = R.drawable.favoriteheart),
                                            contentDescription = "Heart",
                                            tint = if (enabledHeart) Color.Red else Color.LightGray,
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clickable { enabledHeart = true }
                                        )
                                    }
                                }

                            } else {
                                Log.e("Glide", "Image URL is null or empty")
                            }
                        }
                    }
                }

            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = colorResource(id = R.color.authorizationMark)
                )
            }
        }
    }

}


suspend fun fetchTopBooks(): List<BookItem> {
    return try {
        val response = createGoogleBooksService().getTopBooks(
            maxResults = 6,
            apiKey = "AIzaSyD32TSLFrd1TCuroWwl06Ts78-oY0UiF2w"
        )
        response.items
    } catch (e: Exception) {
        Log.e("BooksError", "Error fetching books: $e")
        emptyList()
    }
}
