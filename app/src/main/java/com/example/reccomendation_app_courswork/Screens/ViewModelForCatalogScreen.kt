package com.example.reccomendation_app_courswork.Screens


import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reccomendation_app_courswork.googleBooks.BookItem
import com.example.reccomendation_app_courswork.googleBooks.createGoogleBooksService
import com.example.reccomendation_app_courswork.roomInterface.BookDao
import com.example.reccomendation_app_courswork.roomInterface.BookEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CatalogScreenViewModel @Inject constructor(
    private val bookDao: BookDao
): ViewModel() {
    var topBooks by mutableStateOf<List<BookItem>>(emptyList())
        private set
    var isLoadingNextPage by mutableStateOf(false)
        private set
    private var startIndex by mutableIntStateOf(0)


    init {
        loadMoreBooks()
    }

    fun loadMoreBooks() {
        if (isLoadingNextPage) return
        isLoadingNextPage = true
        viewModelScope.launch {
            val newBooks = withContext(Dispatchers.IO) {
                fetchTopBooks(startIndex = startIndex)
            }
            topBooks += newBooks
            startIndex += 20
            isLoadingNextPage = false
        }
    }
    private suspend fun fetchTopBooks(startIndex: Int): List<BookItem> {
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

    fun saveBookToDatabase(book: BookItem, bitmap: Bitmap?) {
        viewModelScope.launch {
            insertBookDetailsInDatabase(book, bitmap)
        }
    }

    suspend fun insertBookDetailsInDatabase(book: BookItem, bitmap: Bitmap?) {
        val entities = BookEntity(
                id = book.id,
                title = book.volumeInfo.title,
                authors = book.volumeInfo.authors,
                publishedDate = book.volumeInfo.publishedDate,
                description = book.volumeInfo.description,
                thumbnail = bitmap
            )
        withContext(Dispatchers.IO) {
            bookDao.insertBook(entities)
        }
    }
}