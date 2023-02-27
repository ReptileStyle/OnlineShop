package com.example.onlineshop.ui.profile

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap

data class ProfileState(
    val username: String = "",
    val isPhotoLoading:Boolean = false,
    val imageUri: Uri? = null,
    val imageBitmap: ImageBitmap? = null,
)