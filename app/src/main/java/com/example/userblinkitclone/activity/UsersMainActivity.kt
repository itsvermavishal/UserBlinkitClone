package com.example.userblinkitclone.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import com.example.userblinkitclone.CartListener
import com.example.userblinkitclone.R
import com.example.userblinkitclone.adapters.AdapterCartProducts
import com.example.userblinkitclone.databinding.ActivityUsersMainBinding
import com.example.userblinkitclone.databinding.BsCartProductsBinding
import com.example.userblinkitclone.roomdb.CartProductsTable
import com.example.userblinkitclone.viewmodels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.getValue

class UsersMainActivity : AppCompatActivity() , CartListener{
    private lateinit var binding: ActivityUsersMainBinding
    private val viewModel : UserViewModel by viewModels()
    private lateinit var cartProductList: List<CartProductsTable>
    private lateinit var adapterCartProducts: AdapterCartProducts
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getAllCartProducts()
        getTotalItemCountInCart()
        onCartClicked()
        onNextButtonClicked()
    }

    private fun onNextButtonClicked() {
        binding.btnNext.setOnClickListener {
            startActivity(Intent(this, OrderPlaceActivity::class.java))
        }
    }

    private fun getAllCartProducts(){
        viewModel.getAll().observe(this){
            cartProductList = it
        }
    }

    private fun onCartClicked() {
        binding.llcart.setOnClickListener {
            val bsCartProductsBinding = BsCartProductsBinding.inflate(LayoutInflater.from(this))

            val bottomSheetDialog = BottomSheetDialog(this)
            bottomSheetDialog.setContentView(bsCartProductsBinding.root)

            bsCartProductsBinding.tvNumberOfProductCount.text = binding.tvNumberOfProductCount.text
            bsCartProductsBinding.btnNext.setOnClickListener {
                startActivity(Intent(this, OrderPlaceActivity::class.java))
            }
            adapterCartProducts = AdapterCartProducts()
            bsCartProductsBinding.rvproductsItems.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartProductList)

            bottomSheetDialog.show()
        }

    }

    private fun getTotalItemCountInCart() {
        viewModel.fetchTotalCartItemCount().observe(this) {
            if (it > 0){
                binding.llcart.visibility = View.VISIBLE
                binding.tvNumberOfProductCount.text = it.toString()
            }
            else{
                binding.llcart.visibility = View.GONE
                binding.tvNumberOfProductCount.text = "0"
            }
        }
    }

    override fun showCartLayout(itemCount: Int) {
        val previousCount = binding.tvNumberOfProductCount.text.toString().toInt()
        val updatedCount = previousCount + itemCount

        if (updatedCount > 0){
            binding.llcart.visibility = View.VISIBLE
            binding.tvNumberOfProductCount.text = updatedCount.toString()
        }
        else{
            binding.llcart.visibility = View.GONE
            binding.tvNumberOfProductCount.text = "0"
        }

    }

    override fun savingCartItemCount(itemCount: Int) {
        viewModel.fetchTotalCartItemCount().observe(this) {
            viewModel.savingCartItemCount(it + itemCount)
        }
    }
}