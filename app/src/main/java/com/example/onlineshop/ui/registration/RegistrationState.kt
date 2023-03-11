package com.example.onlineshop.ui.registration

import com.example.onlineshop.domain.model.Response
import com.google.android.gms.auth.api.identity.BeginSignInResult

data class RegistrationState(
    val email:String = "",
    val firstName:String = "",
    val lastName:String = "",
    val isFirstNameError:Boolean = false,
    val isLastNameError:Boolean = false,
    val isEmailError:Boolean = false,
    val isLoading:Boolean = false,
    val isFirstNameChecked:Boolean = false,
    val oneTapSignInResponse: BeginSignInResult? = null,
    val isGoogleLoading:Boolean = false,
)