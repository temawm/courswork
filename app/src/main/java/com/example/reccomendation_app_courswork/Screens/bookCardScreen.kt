package com.example.reccomendation_app_courswork.Screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.reccomendation_app_courswork.googleBooks.BookItem
@Composable
fun BookCardScreen(bookItem: BookItem) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImageLoader(imageUrl = bookItem.volumeInfo.imageLinks?.thumbnail ?: "")
        TextLoader(title = "Название", info = bookItem.volumeInfo.title )
        TextLoader(title = "Авторы", info = if (bookItem.volumeInfo.authors?.isNotEmpty() == true) bookItem.volumeInfo.authors.toString() else "Неизвестны")
        TextLoader(title = "Дата выпуска", info = bookItem.volumeInfo.publishedDate?: "Неизвестна")
        TextLoader(title = "Описание", info = bookItem.volumeInfo.description?: "Нет")
        Log.d("BookItemInsideInfo", bookItem.volumeInfo.title)
    }
}

@Composable
fun ImageLoader(imageUrl: String) {
    val painter = rememberImagePainter(
        data = (imageUrl)
    )
    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .wrapContentHeight()
            .padding(16.dp),
        contentScale = ContentScale.Crop
    )
    Spacer(modifier = Modifier.height(20.dp))
    Spacer(modifier = Modifier.height(1.dp).background(Color.Gray))


}
@Composable
fun TextLoader(title: String, info: String){
    Text(
        fontSize = 18.sp,
        text = "$title: $info",
        textAlign = TextAlign.Left,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(
                start = 12.dp, top = 10.dp, bottom = 10.dp)
            )
    Spacer(modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
        .background(Color.LightGray))

}

