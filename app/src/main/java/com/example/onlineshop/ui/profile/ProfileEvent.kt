package com.example.onlineshop.ui.profile

sealed class ProfileEvent {
    object OnLogOutClick:ProfileEvent()
    object OnPhotoClick:ProfileEvent()
}