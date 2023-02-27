package com.example.onlineshop.ui.login

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshop.core.util.UiEvent
import com.example.onlineshop.domain.model.Response
import com.example.onlineshop.domain.repository.UserRepository
import com.example.onlineshop.navigation.Route
import com.example.onlineshop.navigation.Route.login
import com.example.onlineshop.ui.registration.RegistrationState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @ApplicationContext
    val context: Context
) : ViewModel() {
    var state by mutableStateOf(LoginState())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()


    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnFirstnameChange -> {
                state = state.copy(
                    firstname = event.value,
                    isFirstnameError = false
                )
            }
            is LoginEvent.OnPasswordChange -> {
                state = state.copy(
                    password = event.value,
                    isPasswordError = false
                )
            }
            LoginEvent.OnLoginClick -> {
                if(checkIfFieldCorrect())
                    viewModelScope.launch { login(state.firstname)}
            }
            LoginEvent.OnChangePasswordVisibilityClick->{
                state = state.copy(isPasswordVisible = !state.isPasswordVisible)
            }
        }
    }

    private fun checkIfFieldCorrect():Boolean{
        if(state.firstname.isBlank()){
            Toast.makeText(context,"First name must not be blank",Toast.LENGTH_SHORT).show()
            state = state.copy(isFirstnameError = true)
            return false
        }
        if(state.password.length<8){
            Toast.makeText(context,"Password must be at least 8 characters long",Toast.LENGTH_SHORT).show()
            state = state.copy(isPasswordError = true)
            return false
        }
        return true
    }

    private suspend fun login(firstname:String) {
//        if(!userRepository.isUsernameExist(firstname)) {
//            Toast.makeText(context ,"First name does not exist", Toast.LENGTH_LONG).show()
//            state = state.copy(isFirstnameError = true)
//            return
//        }
        userRepository.loginWithFirstname(firstname)//empty password passed
            .collect { response ->
                when (response) {
                    is Response.Loading -> {
                        state = state.copy(isLoading = true)
                    }
                    is Response.Success -> {
                        state = state.copy(isLoading = false)
                        _uiEvent.send(UiEvent.Navigate(Route.profile,popBackStack = true))
                    }
                    is Response.Failure -> {
                        state = state.copy(isLoading = false, password = "", isFirstnameError = true)
                        Toast.makeText(context ,"${response.e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

}