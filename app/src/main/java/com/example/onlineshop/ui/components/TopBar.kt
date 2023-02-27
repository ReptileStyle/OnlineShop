package com.example.onlineshop.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.onlineshop.R
import com.example.onlineshop.ui.theme.Montserrat

@Composable
fun HomeTopAppBar(
    onMenuButtonClick:()->Unit = {},
    modifier: Modifier = Modifier,
    profileImageBitmap: ImageBitmap? = null,
    isPhotoLoading: Boolean = false,
) {
    TopAppBar(
        modifier = modifier,
        backgroundColor = Color.White,
        elevation = 0.dp
    ){
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val(navIcon,title,profileImage) = createRefs()
            IconButton(
                onClick = onMenuButtonClick,
                modifier = Modifier.size(22.dp).constrainAs(navIcon){
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start,15.dp)
                }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.menu_burger),
                    contentDescription = "navigation icon",
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(text = buildAnnotatedString {
                append("Trade by ")
                withStyle(SpanStyle(color = Color(0xff4E55D7))){
                    append("bata")
                }
            },
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.constrainAs(title){
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }
            )
            ProfilePhotoContainer(
                modifier = Modifier
                    .size(30.dp)
                    .constrainAs(profileImage) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end, 47.dp)
                    },
                borderSize = 1.dp,
                imageBitmap = profileImageBitmap,
                isPhotoLoading = isPhotoLoading
            )
        }
    }
}