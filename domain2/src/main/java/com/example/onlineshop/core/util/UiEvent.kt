package com.example.onlineshop.core.util

import android.widget.Toast

sealed class UiEvent {
    data class Navigate(val route: String, val popBackStack: Boolean = false): UiEvent()
    object NavigateUp: UiEvent()

    data class Message(val text:String, val length: Int): UiEvent()
}