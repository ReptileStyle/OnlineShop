package com.example.onlineshop.ui.login

data class LoginState(
    val firstname:String = "",
    val password:String = "",
    val isFirstnameError:Boolean = false,
    val isPasswordError:Boolean = false,
    val isPasswordVisible:Boolean = false,
    val isLoading:Boolean = false
)