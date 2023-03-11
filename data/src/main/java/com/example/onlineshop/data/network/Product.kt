package com.example.onlineshop.data.network

data class Latest(
    val category: String,
    val image_url: String,
    val name: String,
    val price: Int
): Product
data class FlashSale(
    val category: String,
    val discount: Int,
    val image_url: String,
    val name: String,
    val price: Double
): Product

interface Product{

}