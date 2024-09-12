package com.example.reccomendation_app_courswork.Screens

import android.util.Log
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.reccomendation_app_courswork.googleBooks.BookItem
import com.example.reccomendation_app_courswork.googleBooks.BookResponse
import com.example.reccomendation_app_courswork.googleBooks.createGoogleBooksService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CatalogScreen() {
    var topBooks by remember { mutableStateOf<List<BookItem>>(emptyList()) }
    LaunchedEffect(Unit) {
        topBooks = withContext(Dispatchers.IO){ fetchTopBooks()
            }
    }
    LazyColumn {
        items(topBooks) { book ->

        }
    }
}


suspend fun fetchTopBooks(): List<BookItem> {
    try {
        val response = createGoogleBooksService().getTopBooks(maxResults = 10)
        return response.items
    } catch (e: Exception) {
        Log.e("BooksError", "Ошибка при получении книг: $e")
        return emptyList()
    }
}
//@Composable
//fun bookCard(book: BookItem){
//    Card(
//        modifier = Modifier
//            .fillMaxWidth(0.4f)
//            .height(100.dp)
//            .padding(12.dp)
//    ){
//
//    }
//}