package com.example.onlineshop.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onlineshop.R
import com.example.onlineshop.ui.theme.Montserrat
import com.example.onlineshop.ui.theme.Poppins

@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    placeholder: String = "",
    value: String = "",
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChange: (String) -> Unit = {}
) {
    OutlinedTextFieldNoContentPadding(
        value = value,
        onValueChange = onValueChange,
        shape = RoundedCornerShape(15.dp),
        modifier = modifier.height(24.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            disabledBorderColor = Color.Transparent,
            backgroundColor = Color.White
        ),
        placeholder = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = placeholder,
                textAlign = TextAlign.Center,
                fontSize = 9.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = Poppins,
                color = Color(0xff5b5b5b)
            )
        },
        textStyle = TextStyle(
            textAlign = TextAlign.Center,
            fontSize = 9.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = Poppins,
            color = Color(0xff202020)
        ),
        trailingIcon = {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.search),
                contentDescription = null,
                tint = Color(0xff5b5b5b)
            )
        },
        leadingIcon = {
        },
        contentPadding = PaddingValues(top = 6.dp),
        isError = isError,
        visualTransformation = visualTransformation,
    )
}