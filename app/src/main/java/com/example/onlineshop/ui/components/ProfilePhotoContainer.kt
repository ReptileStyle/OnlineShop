package com.example.onlineshop.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Surface
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.onlineshop.R

@Composable
fun ProfilePhotoContainer(
    modifier: Modifier = Modifier,
    imageBitmap: ImageBitmap? = null,
    isPhotoLoading: Boolean = false,
    borderSize: Dp? = null,
    onClick: () -> Unit = {}
) {
    ElevatedButton(
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
        border =  borderSize?.let { BorderStroke(borderSize,Color.Black)},
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colors.background,
            contentColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.Transparent
        ),
        elevation = ButtonDefaults.buttonElevation(4.dp),
        onClick = onClick
    ) {
        if (imageBitmap == null) {
            if(isPhotoLoading)
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(16.dp)
                        .height(16.dp),
                    strokeWidth = 2.dp,
                    color = Color(0xFF252525)
                )
            else
                Icon(
                    modifier = Modifier
                        .size(30.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.no_photo),
                    contentDescription = "Camera",
                    tint = Color.Black
                )
        }
        else {
            Surface(
                modifier = Modifier.fillMaxSize(),
            ) {
                Image(
                    bitmap = imageBitmap,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentDescription = "Camera",
                    contentScale = ContentScale.Crop
                )
                if(isPhotoLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(16.dp)
                            .height(16.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFF252525)
                    )
                    androidx.compose.material3.Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.White.copy(alpha = 0.6f)
                    ) {

                    }
                }
            }
        }
    }
}