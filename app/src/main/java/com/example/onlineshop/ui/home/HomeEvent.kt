package com.example.onlineshop.ui.home

sealed class HomeEvent {
    data class OnSearchValueChange(val value:String):HomeEvent()
}