package com.example.onlineshop.ui.home


import androidx.compose.ui.graphics.ImageBitmap
import com.example.onlineshop.data.network.FlashSale
import com.example.onlineshop.data.network.Latest

data class HomeState(
    val searchValue:String = "",
    val latestProductList: List<Latest> = listOf(),
    val flashSaleProductList: List<FlashSale> = listOf(),
    val profilePhoto:ImageBitmap? = null,
)