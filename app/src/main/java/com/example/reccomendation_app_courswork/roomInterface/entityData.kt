package com.example.reccomendation_app_courswork.roomInterface

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.reccomendation_app_courswork.googleBooks.ImageLinks

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val authors: List<String>?,
    val publishedDate: String?,
    val description: String?,
    val thumbnail: Bitmap?,
)