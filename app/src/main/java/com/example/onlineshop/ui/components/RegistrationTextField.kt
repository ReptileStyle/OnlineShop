package com.example.onlineshop.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onlineshop.ui.theme.Montserrat

@Composable
fun RegistrationTextField(
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
        modifier = modifier.height(30.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            disabledBorderColor = Color.Transparent,
            backgroundColor = Color(0xffe8e8e8)
        ),
        placeholder = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = placeholder,
                textAlign = TextAlign.Center,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Montserrat,
                color = Color(0xff7b7b7b)
            )
        },
        textStyle = TextStyle(
            textAlign = TextAlign.Center,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = Montserrat,
            color = Color(0xff202020)
        ),
        isError = isError,
        visualTransformation = visualTransformation,
    )
}