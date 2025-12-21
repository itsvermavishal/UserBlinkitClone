package com.example.userblinkitclone.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CartProductsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCartProduct(cartProductsTable: CartProductsTable)

    @Update
    fun updateCartProduct(cartProductsTable: CartProductsTable)

    @Query("SELECT * FROM CartProductsTable") // it should be here if ew want to see the data in room DB
    fun getAllCartProducts() : LiveData<List<CartProductsTable>>



    @Query("DELETE FROM CartProductsTable WHERE productId = :productId")
    fun deleteCartProduct(productId : String)

    @Query("DELETE FROM CartProductsTable")
    suspend fun deleteCartProducts()
}