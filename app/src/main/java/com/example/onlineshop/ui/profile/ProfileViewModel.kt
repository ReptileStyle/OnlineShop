package com.example.onlineshop.ui.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshop.core.util.UiEvent
import com.example.onlineshop.domain.model.Response
import com.example.onlineshop.domain.repository.UserRepository
import com.example.onlineshop.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @ApplicationContext
    val context: Context
): ViewModel() {
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    var state by mutableStateOf(ProfileState())
        private set

    init {
        viewModelScope.launch {
            state = state.copy(isPhotoLoading = true)
            val profileData = userRepository.getProfileData()
            if(profileData is Response.Success && profileData.data!=null){
                state = state.copy(
                    username = profileData?.data?.username?:"",
                    imageBitmap = profileData?.data?.imageBitmap?.asImageBitmap()
                )
            }

            else{
                if(profileData is Response.Failure)
                    Log.d("profileVM","something went wrong - ${profileData.e}")
                else
                    Log.d("profileVM","something went wrong")
            }
            state = state.copy(isPhotoLoading = false)
        }
    }

    fun onEvent(event: ProfileEvent){
        when(event){
            ProfileEvent.OnLogOutClick ->{
                userRepository.logout()
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.Navigate(Route.signIn, popBackStack = true))
                }
            }
            ProfileEvent.OnPhotoClick -> {

            }
        }
    }
    private fun setImageUri(uri: Uri?){
        state = state.copy(imageUri = uri)
    }
    /** здесь хранится текущее прикрепленное изображение
     * */
    private fun setImageBitmap(imageBitmap: ImageBitmap?){
        state = state.copy(imageBitmap = imageBitmap)
    }

    fun onPhotoPicked(uri: Uri?){
        setImageUri(uri)
        var bitmap: Bitmap? = null
        state.imageUri?.let {
            bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images
                    .Media.getBitmap(context.contentResolver, it)

            } else {
                val source = ImageDecoder
                    .createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            }
        }
        setImageBitmap(bitmap?.asImageBitmap())

        //start uploading
        uploadImage(uri)
    }

    private fun uploadImage(uri: Uri?) {
        viewModelScope.launch {
            userRepository
                .uploadUserProfilePhoto(uri)
                .collect { response ->
                    when(response) {
                        is Response.Loading -> {
                            state = state.copy(isPhotoLoading = true)
                        }
                        is Response.Failure ->{
                            state = state.copy(isPhotoLoading = false)
                            Log.d("UserPhotoVM","fail")
                            Toast.makeText(context,"${response.e}", Toast.LENGTH_LONG)
//                            viewModelScope.launch {
//                                _uiEvent.send(UiEvent.ShowSnackbar(UiText.DynamicString("${response.e}")))
//                            }
                        }
                        is Response.Success ->{
                            state = state.copy(isPhotoLoading = false)
                        }
                    }
                }
        }
    }
}