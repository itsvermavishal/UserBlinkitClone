package com.example.userblinkitclone.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CartProductsTable")
data class CartProductsTable(
    @PrimaryKey()
    val productId : String = "random", // can't apply nullability check here

    val productTitle : String ?= null,
    val productQuantity : String ?= null,
    val productPrice : String ?= null,
    var productCount : Int ?= null,
    var productStock : Int ?= null,
    var productImage : String ?= null,
    var productCategory : String ?= null,
    var adminUID : String ?= null,
    var productType : String ?= null
)
