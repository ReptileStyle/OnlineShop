package com.example.onlineshop.ui.registration


import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
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
import com.google.android.gms.auth.api.identity.Identity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
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
            sendUiEventMessage("first name must not be blank",Toast.LENGTH_SHORT)
            state = state.copy(isFirstNameError = true)
            return false
        }
        if(state.lastName.isBlank()){
            sendUiEventMessage("last name must not be blank",Toast.LENGTH_SHORT)
            state = state.copy(isLastNameError = true)
            return false
        }
        if(state.email.isBlank()){
            sendUiEventMessage("email must not be blank",Toast.LENGTH_SHORT)
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
                        _uiEvent.send(UiEvent.Navigate(Route.home,popBackStack = true))
                        sendUiEventMessage("successfully signed in",Toast.LENGTH_SHORT)
                    }
                    is Response.Failure ->{
                        state = state.copy(isLoading = false,isEmailError = true)
                        sendUiEventMessage("${response.e.message}",Toast.LENGTH_LONG)
                    }
                    is Response.Loading ->{
                        state = state.copy(isLoading = true)
                    }
                }
            }
        }
    }

    fun processGoogleSignInResult(result: ActivityResult, context: Context){
        viewModelScope.launch {
            userRepository.processGoogleSignInResult(result, context).collect{
                when(it){
                    is Response.Loading ->{
                        state=state.copy(isGoogleLoading = true)
                    }
                    is Response.Failure->{
                        _uiEvent.send(UiEvent.Message(it.e.message ?: "something went wrong",Toast.LENGTH_SHORT))
                        state=state.copy(isGoogleLoading = false)
                    }
                    is Response.Success -> {
                        _uiEvent.send(UiEvent.Navigate(Route.home,popBackStack = true))
                        state=state.copy(isGoogleLoading = false)
                    }
                }
            }
        }
    }

    fun signInWithGoogle(context:Context){
        viewModelScope.launch {
            userRepository.signInWithGoogle(context).collect{
                Log.d("asd",it.toString())
                when(it){
                    is Response.Success->{
                        state = state.copy(oneTapSignInResponse = it.data)
                    }
                    else -> {}
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
                            sendUiEventMessage("${e.message}",Toast.LENGTH_LONG)
                        }
                        else->{
                            sendUiEventMessage("${e.message}",Toast.LENGTH_LONG)
                        }
                    }
                }
            }
        }
    }
    private fun sendUiEventMessage(text:String,length:Int){
        viewModelScope.launch {
            _uiEvent.send(UiEvent.Message(text,length))
        }
    }
}