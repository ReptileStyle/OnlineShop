package com.example.onlineshop.ui.login

import com.example.onlineshop.ui.registration.RegistrationEvent

sealed class LoginEvent {
    data class OnFirstnameChange(val value:String): LoginEvent()
    data class OnPasswordChange(val value:String): LoginEvent()
    object OnLoginClick:LoginEvent()
    object OnChangePasswordVisibilityClick:LoginEvent()
}