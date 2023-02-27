package com.example.onlineshop.ui.registration

sealed class RegistrationEvent {
    data class OnFirstNameChange(val value:String):RegistrationEvent()
    data class OnLastNameChange(val value:String):RegistrationEvent()
    data class OnEmailChange(val value:String):RegistrationEvent()
    object OnSignInClick:RegistrationEvent()
    object OnLogInClick:RegistrationEvent()
    object OnSignInWithGoogleClick:RegistrationEvent()
}