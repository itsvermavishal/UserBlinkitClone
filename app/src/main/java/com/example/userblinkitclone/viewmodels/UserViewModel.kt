package com.example.userblinkitclone.viewmodels

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.userblinkitclone.models.Product
import com.example.userblinkitclone.roomdb.CartProductsTable
import com.example.userblinkitclone.roomdb.CartProductsDao
import com.example.userblinkitclone.roomdb.CartProductsDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserViewModel(application: Application) : AndroidViewModel(application){

    //initializations
    val sharedPreferences : SharedPreferences = application.getSharedPreferences("My_Pref", MODE_PRIVATE)
    val cartProductsDao : CartProductsDao = CartProductsDatabase.getDatabaseInstance(application).cartProductsDao()

    //Room DB
    suspend fun insertCartProduct(products: CartProductsTable){
        cartProductsDao.insertCartProduct(products)
    }

    fun getAll() : LiveData<List<CartProductsTable>>{
        return cartProductsDao.getAllCartProducts()
    }

    suspend fun updateCartProduct(products: CartProductsTable){
        cartProductsDao.updateCartProduct(products)
    }

    suspend fun deleteCartProduct(productId : String){
        cartProductsDao.deleteCartProduct(productId)
    }

    //Firebase Call
    fun fetchAllProducts(): Flow<java.util.ArrayList<Product>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts")

        val eventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for (product in snapshot.children){
                    val prod = product.getValue(Product::class.java)
                    products.add(prod!!)
                }
                trySend(products).isSuccess
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        db.addValueEventListener(eventListener)

        awaitClose {
            db.removeEventListener(eventListener)
        }
    }

    fun getCategoryProduct(category: String) : Flow<List<Product>> = callbackFlow{
        val db =FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory/${category}")

        val eventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for (product in snapshot.children){
                    val prod = product.getValue(Product::class.java)
                    products.add(prod!!)
                }
                trySend(products).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        db.addValueEventListener(eventListener)

        awaitClose {
            db.removeEventListener(eventListener)
        }
    }

    fun updateItemCount(product: Product, itemCount: Int){
        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts/${product.productRandomId}").child("itemCount").setValue(itemCount)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory/${product.productCategory}/${product.productRandomId}").child("itemCount").setValue(itemCount)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductType/${product.productType}/${product.productRandomId}").child("itemCount").setValue(itemCount)
    }

    //Shared Preferences
    fun savingCartItemCount(itemCount : Int){
        sharedPreferences.edit().putInt("itemCount", itemCount).apply()
    }

    fun fetchTotalCartItemCount() : MutableLiveData<Int>{
        val totalItemCount = MutableLiveData<Int>()
        totalItemCount.value = sharedPreferences.getInt("itemCount", 0)
        return totalItemCount
    }
}