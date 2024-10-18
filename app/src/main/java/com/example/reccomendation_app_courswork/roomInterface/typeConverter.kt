package com.example.reccomendation_app_courswork.roomInterface

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converters  {
    @TypeConverter
    fun fromAuthorsList(authors: List<String>?): String? {
        return authors?.joinToString("; ")
    }
    @TypeConverter
    fun toAuthorsList(authorsList: String?): List<String>? {
        return if (authorsList.isNullOrEmpty()) null else authorsList.split(",")
    }

    @TypeConverter
    fun fromBitmap(bitmap: Bitmap?): ByteArray? {
        if (bitmap == null) {
            return null
        }
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream) // Вы можете выбрать другой формат и качество
        return stream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray?): Bitmap? {
        return if (byteArray == null) {
            null
        } else {
            BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }
    }
}