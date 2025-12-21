package com.example.userblinkitclone.models

import com.example.userblinkitclone.roomdb.CartProductsTable

data class Orders(
    val orderId: String? = null,
    val orderList: List<CartProductsTable>? = null,
    val userAddress: String? = null,
    val orderStatus: Int? = 0,
    val orderDate: String? = null,
    val orderingUserId: String? = null
)
