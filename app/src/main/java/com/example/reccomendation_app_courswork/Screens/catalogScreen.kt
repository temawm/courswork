package com.example.reccomendation_app_courswork.Screens

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
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

    Spacer(modifier = Modifier.height(50.dp))

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp)
    ) {
        if (topBooks.isNotEmpty()) {
            items(topBooks) { book ->
                val imageUrl = book.volumeInfo.imageLinks?.thumbnail
                Column(
                    modifier = Modifier
                        .width(200.dp)
                        .height(250.dp)
                        .padding(12.dp)
                        .background(Color.LightGray),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(LocalContext.current)
                            .asBitmap()
                            .load(imageUrl)
                            .into(object : CustomTarget<Bitmap>() {
                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                    imageBitmap = resource
                                }

                                override fun onLoadCleared(placeholder: Drawable?) {

                                }
                            })
                        imageBitmap?.let { bitmap ->
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().padding(12.dp)
                            )
                        }
                    } else {
                        Log.e("Glide", "Image URL is null or empty")
                    }
                }
            }
        }
    }
}

suspend fun fetchTopBooks(): List<BookItem> {
    return try {
        val response = createGoogleBooksService().getTopBooks(maxResults = 6, apiKey = "AIzaSyD32TSLFrd1TCuroWwl06Ts78-oY0UiF2w")
        response.items
    } catch (e: Exception) {
        Log.e("BooksError", "Error fetching books: $e")
        emptyList()
    }
}
