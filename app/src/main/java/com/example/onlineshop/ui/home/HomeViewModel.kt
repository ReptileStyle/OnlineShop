package com.example.onlineshop.ui.home

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshop.core.util.UiEvent
import com.example.onlineshop.domain.model.Response
import com.example.onlineshop.domain.repository.UserRepository
import com.example.onlineshop.data.network.FlashSale
import com.example.onlineshop.data.network.Latest
import com.example.onlineshop.data.network.ProductsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    var state by mutableStateOf(HomeState())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            getProducts()
        }
        viewModelScope.launch {
            val response = userRepository.getProfileData()
            if(response is Response.Success){
                state=state.copy(profilePhoto = response.data?.imageBitmap?.asImageBitmap())
            }

        }
    }

    fun onEvent(event: HomeEvent){
        when(event){
            is HomeEvent.OnSearchValueChange->{
                state = state.copy(searchValue = event.value)
            }
        }
    }

    private suspend fun getProducts() {
        var latestProductList: List<Latest>? = null
        var flashSaleProductList: List<FlashSale>? = null
        val a = viewModelScope.launch {
            try {
                latestProductList = ProductsApi.retrofitService.getLatestProducts().latest
            } catch (e: Exception) {
                Log.d("HomeVM", "${e.message}")
            }
        }
        val b = viewModelScope.launch {
            try {
                flashSaleProductList = ProductsApi.retrofitService.getFlashSaleProducts().flash_sale
            } catch (e: Exception) {
                Log.d("HomeVM", "${e.message}")
            }
        }
        a.join()
        b.join()
        if (latestProductList != null && flashSaleProductList != null) {
            state = state.copy(
                flashSaleProductList = flashSaleProductList ?: listOf(),
                latestProductList = latestProductList ?: listOf()
            )
        }
    }
    private fun sendUiEventMessage(text:String,length:Int){
        viewModelScope.launch {
            _uiEvent.send(UiEvent.Message(text,length))
        }
    }
}