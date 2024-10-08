package com.example.reccomendation_app_courswork.Screens

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.reccomendation_app_courswork.R
import com.example.reccomendation_app_courswork.googleBooks.BookItem
import com.example.reccomendation_app_courswork.googleBooks.createGoogleBooksService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.net.URLEncoder

@Composable
fun CatalogScreen(navHostController: NavHostController) {
    var topBooks by remember { mutableStateOf<List<BookItem>>(emptyList()) }
    var isLoadingNextPage by remember { mutableStateOf(false) }
    var startIndex by remember { mutableIntStateOf(0) }

   LaunchedEffect(Unit) {
       isLoadingNextPage = true
        val newBooks = withContext(Dispatchers.IO) {
            fetchTopBooks(startIndex = startIndex)
        }
        topBooks = newBooks
       startIndex += 10
       isLoadingNextPage = false
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

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
            ) {

                items(topBooks,  key = { it.id }) { book ->
                    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
                    if ((topBooks.lastIndex == topBooks.indexOf(book))) {
                        LaunchedEffect(Unit) {
                            isLoadingNextPage = true
                            startIndex += 20
                            val newBooks = withContext(Dispatchers.IO) {
                                fetchTopBooks(startIndex)
                            }
                            topBooks = topBooks + newBooks
                            delay(3000)
                            isLoadingNextPage = false
                        }
                    }

                    Card(
                        modifier = Modifier
                            .width(230.dp)
                            .height(350.dp)
                            .padding(6.dp)
                            .clickable {
                                val gson = Gson()
                                val bookJson = URLEncoder.encode(gson.toJson(book), "UTF-8")
                                navHostController.navigate("BookCardScreen/$bookJson")
                            },
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 12.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        val imageUrl = book.volumeInfo.imageLinks?.thumbnail ?: ""
                        if (imageUrl.isNotEmpty()) {
                            Glide.with(LocalContext.current)
                                .asBitmap()
                                .load(imageUrl)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(object : CustomTarget<Bitmap>() {
                                    override fun onResourceReady(
                                        resource: Bitmap,
                                        transition: Transition<in Bitmap>?
                                    ) {
                                        imageBitmap = resource
                                    }

                                    override fun onLoadCleared(placeholder: Drawable?) {}
                                })
                        } else {
                            Log.e("Glide", "Image URL is null or empty")
                        }
                        if (imageBitmap!= null) {
                            Image(
                            bitmap = imageBitmap!!.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .height(250.dp)
                                .width(180.dp)
                                .padding(start = 10.dp, end = 10.dp, top = 10.dp)
                        )
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
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = book.volumeInfo.authors?.joinToString(", ")?: "Автор неизвестен",
                                    fontSize = 12.sp,
                                    color = Color.LightGray
                                )
                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    text = book.volumeInfo.publishedDate?: " ",
                                    textAlign = TextAlign.Start,
                                    fontSize = 9.sp,
                                    color = Color.Gray
                                )
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

suspend fun fetchTopBooks(startIndex: Int): List<BookItem> {
    return try {
        val response = createGoogleBooksService().getTopBooks(
            maxResults = 20,
            startIndex = startIndex,
            apiKey = "AIzaSyD32TSLFrd1TCuroWwl06Ts78-oY0UiF2w"
        )
        response.items
    } catch (e: Exception) {
        Log.e("BooksError", "Error fetching books: $e")
        emptyList()
    }
}
