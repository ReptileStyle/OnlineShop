package com.example.onlineshop.ui.registration

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshop.core.util.UiEvent
import com.example.onlineshop.domain.model.InternetNotAvailableException
import com.example.onlineshop.domain.model.Response
import com.example.onlineshop.domain.repository.UserRepository
import com.example.onlineshop.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @ApplicationContext
    val context: Context
):ViewModel() {

    var state by mutableStateOf(RegistrationState())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var checkIfUsernameExistsJob: Job? = null


    fun onEvent(event: RegistrationEvent){
        when(event){
            is RegistrationEvent.OnEmailChange -> {
                state = state.copy(email = event.value, isEmailError = false)
            }
            is RegistrationEvent.OnLastNameChange -> {
                state = state.copy(lastName = event.value, isLastNameError = false)
            }
            is RegistrationEvent.OnFirstNameChange -> {
                state = state.copy(firstName = event.value, isFirstNameError = false, isFirstNameChecked = false)
                isUsernameExists(event.value)
            }
            RegistrationEvent.OnLogInClick ->{
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.Navigate(Route.login))
                }
            }
            RegistrationEvent.OnSignInClick ->{
                if(checkIfFieldsCorrect()) {
                    viewModelScope.launch {
                        if (!state.isFirstNameChecked) {
                            //wait to check for username
                            checkIfUsernameExistsJob?.join()
                        }
                        registerUser()
                    }
                }
            }
            RegistrationEvent.OnSignInWithGoogleClick -> {}
        }
    }
    private fun checkIfFieldsCorrect():Boolean{
        if(state.firstName.isBlank()){
            Toast.makeText(context,"first name must not be blank",Toast.LENGTH_SHORT).show()
            state = state.copy(isFirstNameError = true)
            return false
        }
        if(state.lastName.isBlank()){
            Toast.makeText(context,"last name must not be blank",Toast.LENGTH_SHORT).show()
            state = state.copy(isLastNameError = true)
            return false
        }
        if(state.email.isBlank()){
            Toast.makeText(context,"email must not be blank",Toast.LENGTH_SHORT).show()
            state = state.copy(isEmailError = true)
            return false
        }
        return true
    }
    private fun registerUser(){
        viewModelScope.launch {
            val responseFlow = userRepository.createUser(state.email, "111111",state.firstName,state.lastName)//password??
            responseFlow.collect{response->
                when(response){
                    is Response.Success ->{
                        state = state.copy(isLoading = false)
                        _uiEvent.send(UiEvent.Navigate(Route.profile,popBackStack = true))
                        Toast.makeText(context,"successfully signed in",Toast.LENGTH_SHORT).show()
                    }
                    is Response.Failure ->{
                        state = state.copy(isLoading = false,isEmailError = true)
                        Toast.makeText(context ,"${response.e.message}",Toast.LENGTH_LONG).show()
                    }
                    is Response.Loading ->{
                        state = state.copy(isLoading = true)
                    }
                }
            }
        }
    }


    private fun isUsernameExists(username: String) {
        checkIfUsernameExistsJob?.cancel()
        if (state.firstName.length >= 6) {
            checkIfUsernameExistsJob = viewModelScope.launch {
                delay(500)
                try {
                    state =
                        if (userRepository.isUsernameExist(state.firstName))
                            state.copy(isFirstNameChecked = true, isFirstNameError = true)
                        else
                            state.copy(isFirstNameChecked = true, isFirstNameError = false)
                } catch (e: Exception) {
                    when(e){
                        is CancellationException -> {}//it is ok
                        is InternetNotAvailableException -> {
                            Toast.makeText(context ,"${e.message}",Toast.LENGTH_LONG).show()
                        }
                        else->{
                            Toast.makeText(context ,"${e.message}",Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
}