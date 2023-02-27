package com.example.onlineshop.ui.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshop.core.util.UiEvent
import com.example.onlineshop.domain.repository.UserRepository
import com.example.onlineshop.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @ApplicationContext
    val context: Context
): ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init{
        if(userRepository.isLoggedIn()){
            viewModelScope.launch {
                _uiEvent.send(UiEvent.Navigate(Route.home))
            }
        }else{
            viewModelScope.launch {
                _uiEvent.send(UiEvent.Navigate(Route.signIn))
            }
        }
    }
}