package com.example.onlineshop.core.util

sealed class UiEvent {
    data class Navigate(val route: String, val popBackStack: Boolean = false): UiEvent()
    object NavigateUp: UiEvent()
}