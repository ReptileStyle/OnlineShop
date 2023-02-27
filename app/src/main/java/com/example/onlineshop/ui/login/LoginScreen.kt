package com.example.onlineshop.ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onlineshop.R
import com.example.onlineshop.core.util.UiEvent
import com.example.onlineshop.ui.components.GoogleOrAppleButton
import com.example.onlineshop.ui.components.IconButtonWithCustomRippleRadius
import com.example.onlineshop.ui.components.RegistrationTextField
import com.example.onlineshop.ui.registration.RegistrationEvent
import com.example.onlineshop.ui.registration.RegistrationViewModel
import com.example.onlineshop.ui.theme.Montserrat
import com.example.onlineshop.ui.theme.OnlineShopTheme

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit = {},
    onNavigate: (route: String, popBackStack: Boolean) -> Unit = { _, _ -> }
) {
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect {
            when (it) {
                is UiEvent.Navigate -> onNavigate(it.route, it.popBackStack)
                is UiEvent.NavigateUp -> onNavigateUp()

                else -> {}
            }
        }
    }
    Scaffold(
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 42.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(105.dp))
            Text(
                text = "Sign in",
                style = TextStyle(
                    fontFamily = Montserrat,
                    fontSize = 26.sp,
                    color = Color(0xff161826),
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(78.dp))
            RegistrationTextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.state.firstname,
                onValueChange = { viewModel.onEvent(LoginEvent.OnFirstnameChange(it)) },
                placeholder = "First name",
                isError = viewModel.state.isFirstnameError
            )
            Spacer(modifier = Modifier.height(35.dp))
            ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                //to make trailing icon not change layout of RegistrationTextField
                val (field, button) = createRefs()
                RegistrationTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(field) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        },
                    value = viewModel.state.password,
                    onValueChange = { viewModel.onEvent(LoginEvent.OnPasswordChange(it)) },
                    placeholder = "Password",
                    isError = viewModel.state.isPasswordError,
                    visualTransformation = if (viewModel.state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                )
                IconButtonWithCustomRippleRadius(
                    onClick = { viewModel.onEvent(LoginEvent.OnChangePasswordVisibilityClick) },
                    modifier = Modifier
                        .size(15.dp)
                        .constrainAs(button) {
                            end.linkTo(field.end,15.dp)
                            top.linkTo(field.top)
                            bottom.linkTo(field.bottom)
                        },
                    rippleRadius = 20.dp
                ) {
                    androidx.compose.material3.Icon(
                        modifier = Modifier.size(15.dp),
                        imageVector = if (viewModel.state.isPasswordVisible) Icons.Outlined.Visibility
                        else ImageVector.vectorResource(id = R.drawable.eye_off),
                        contentDescription = "make password visible/invisible",
                        tint = Color(0xff5C5C5C)
                    )
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
            Button(
                onClick = { viewModel.onEvent(LoginEvent.OnLoginClick) },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xff4e55d7)
                ),
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier
                    .height(46.dp)
                    .fillMaxWidth()
            ) {
                if (viewModel.state.isLoading)
                    CircularProgressIndicator(
                        modifier = Modifier
                            .width(16.dp)
                            .height(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                else
                    Text(
                        text = "Sign in",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Montserrat,
                        color = Color(0xffeaeaea)
                    )
            }
        }
    }
}

@Preview
@Composable
private fun preview(){
    OnlineShopTheme {
        LoginScreen(
            viewModel = viewModel()
        )
    }
}