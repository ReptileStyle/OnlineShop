package com.example.onlineshop.ui.registration

data class RegistrationState(
    val email:String = "",
    val firstName:String = "",
    val lastName:String = "",
    val isFirstNameError:Boolean = false,
    val isLastNameError:Boolean = false,
    val isEmailError:Boolean = false,
    val isLoading:Boolean = false,
    val isFirstNameChecked:Boolean = false
)