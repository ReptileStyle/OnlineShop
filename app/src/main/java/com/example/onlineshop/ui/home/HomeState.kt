package com.example.onlineshop.ui.home


import androidx.compose.ui.graphics.ImageBitmap
import com.example.onlineshop.network.FlashSale
import com.example.onlineshop.network.Latest
import com.example.onlineshop.network.Product

data class HomeState(
    val searchValue:String = "",
    val latestProductList: List<com.example.onlineshop.network.Latest> = listOf(),
    val flashSaleProductList: List<com.example.onlineshop.network.FlashSale> = listOf(),
    val profilePhoto:ImageBitmap? = null,
)