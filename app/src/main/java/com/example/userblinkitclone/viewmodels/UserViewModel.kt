package com.example.userblinkitclone.viewmodels

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.userblinkitclone.Constants
import com.example.userblinkitclone.Utils
import com.example.userblinkitclone.api.ApiUtilities
import com.example.userblinkitclone.models.Orders
import com.example.userblinkitclone.models.Product
import com.example.userblinkitclone.roomdb.CartProductsDao
import com.example.userblinkitclone.roomdb.CartProductsDatabase
import com.example.userblinkitclone.roomdb.CartProductsTable
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow

class UserViewModel(application: Application) : AndroidViewModel(application){

    //initializations
    val sharedPreferences : SharedPreferences = application.getSharedPreferences("My_Pref", MODE_PRIVATE)
    val cartProductsDao : CartProductsDao = CartProductsDatabase.getDatabaseInstance(application).cartProductsDao()

    private val _paymentStatus = MutableStateFlow<Boolean>(false)
    val paymentStatus = _paymentStatus

    //Room DB
    suspend fun insertCartProduct(products: CartProductsTable){
        cartProductsDao.insertCartProduct(products)
    }

    fun getAll() : LiveData<List<CartProductsTable>>{
        return cartProductsDao.getAllCartProducts()
    }

    suspend fun deleteCartProducts(){
        cartProductsDao.deleteCartProducts()
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

    fun getAllProducts(): Flow<List<Orders>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("Orders").orderByChild("orderStatus")

        val eventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderList = ArrayList<Orders>()
                for (orders in snapshot.children){
                    val order = orders.getValue(Orders::class.java)
                    if (order?.orderingUserId == Utils.getCurrentUserId()){
                        orderList.add(order!!)
                    }
                }
                trySend(orderList).isSuccess
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

    fun getOrderdProducts(orderId : String): Flow<List<CartProductsTable>> = callbackFlow{
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("Orders").child(orderId)
        val eventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val order = snapshot.getValue(Orders::class.java)
                trySend(order?.orderList!!)
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

    fun saveProductsAfterOrder(stock : Int, product: CartProductsTable){
        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts/${product.productId}").child("itemCount").setValue(0)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory/${product.productCategory}/${product.productId}").child("itemCount").setValue(0)
       FirebaseDatabase.getInstance().getReference("Admins").child("ProductType/${product.productType}/${product.productId}").child("itemCount").setValue(0)

        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts/${product.productId}").child("productStock").setValue(stock)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory/${product.productCategory}/${product.productId}").child("productStock").setValue(stock)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductType/${product.productType}/${product.productId}").child("productStock").setValue(stock)


    }

    fun saveUserAddress(address: String){
        FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(Utils.getCurrentUserId()!!).child("userAddress").setValue(address)
    }

    fun getUserAddress(callback: (String?) -> Unit){
        val db = FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(Utils.getCurrentUserId()!!).child("userAddress")
        db.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val address = snapshot.getValue(String::class.java)
                    callback(address)
                }
                else{
                    callback(null)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                callback(null)
            }

        })
    }

    fun saveOderproducts(orders: Orders){
        FirebaseDatabase.getInstance().getReference("Admins").child("Orders").child(orders.orderId!!).setValue(orders)

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

    fun saveAddressStatus(){
        sharedPreferences.edit().putBoolean("addressStatus", true).apply()
    }

    fun getAddressStatus() : MutableLiveData<Boolean>{
        val addressStatus = MutableLiveData<Boolean>()
        addressStatus.value = sharedPreferences.getBoolean("addressStatus", false)
        return addressStatus
    }

    //Retrofit
    suspend fun checkPayment(headers: Map<String, String>){
        val res = ApiUtilities.statusApi.getPaymentStatus(headers, Constants.MERCHANT_ID, Constants.merchantTransactionId)
        if (res.body() != null && res.body()!!.success){
            _paymentStatus.value = true
        }
        else{
            _paymentStatus.value = false
        }
    }
}