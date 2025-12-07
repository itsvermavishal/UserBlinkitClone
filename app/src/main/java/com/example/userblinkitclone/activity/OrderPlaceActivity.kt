package com.example.userblinkitclone.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.userblinkitclone.R
import com.example.userblinkitclone.Utils
import com.example.userblinkitclone.adapters.AdapterCartProducts
import com.example.userblinkitclone.databinding.ActivityOrderPlaceBinding
import com.example.userblinkitclone.databinding.AddressLayoutBinding
import com.example.userblinkitclone.models.Users
import com.example.userblinkitclone.viewmodels.UserViewModel
import kotlinx.coroutines.launch
import kotlin.getValue

class OrderPlaceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderPlaceBinding
    private val viewModel : UserViewModel by viewModels()
    private lateinit var adapterCartProducts: AdapterCartProducts
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setStatusBarColor()
        enableEdgeToEdge()
        backToUserMainActivity()
        getAllCartProducts()
        onPlaceOrderClicked()
    }

    private fun onPlaceOrderClicked() {
        binding.btnPlaceOrder.setOnClickListener {
            viewModel.getAddressStatus().observe(this) { status ->
                if (status){
                    startActivity(Intent(this, UsersMainActivity::class.java))
                    finish()
                }
                else{
                    val addressLayoutBinding = AddressLayoutBinding.inflate(LayoutInflater.from(this))

                    val alertDialog = AlertDialog.Builder(this)
                        .setView(addressLayoutBinding.root)
                        .create()
                    alertDialog.show()

                    addressLayoutBinding.btnAdd.setOnClickListener {
                        saveAddress(alertDialog, addressLayoutBinding)
                    }
                }
            }
        }
    }

    private fun saveAddress(
        alertDialog: AlertDialog,
        addressLayoutBinding: AddressLayoutBinding
    ) {
        Utils.showDialog(this, "Please wait...")
        val userPinCode = addressLayoutBinding.etPinCode.text.toString()
        val userPhoneNumber = addressLayoutBinding.etPhoneNumber.text.toString()
        val userState = addressLayoutBinding.etState.text.toString()
        val userDistrict = addressLayoutBinding.etDistrict.text.toString()
        val userAddress = addressLayoutBinding.etDescriptiveAddress.text.toString()

        val address = "$userPinCode, $userState, $userDistrict, $userAddress, $userPhoneNumber"


        lifecycleScope.launch {
            viewModel.saveUserAddress(address)
            viewModel.saveAddressStatus()
            alertDialog.dismiss()
        }
        Utils.showToast(this, "Address saved successfully")
        Utils.hideDialog()

    }

    private fun backToUserMainActivity() {
        binding.tbOrderFragment.setNavigationOnClickListener {
            startActivity(Intent(this, UsersMainActivity::class.java))
            finish()
        }
    }

    private fun getAllCartProducts(){
        viewModel.getAll().observe(this){cartProductList ->
            adapterCartProducts = AdapterCartProducts()
            binding.rvproductsItems.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartProductList)

            var totalPrice = 0
            for (products in cartProductList){
                val price = products.productPrice
                    ?.replace("₹", "")
                    ?.trim()
                    ?.toIntOrNull() ?: 0

                val quantity = products.productCount ?: 1

                totalPrice += price * quantity
            }

            binding.tvSubTotal.text = totalPrice.toString()

            if (totalPrice < 200){
                binding.tvDeliveryCharges.text = "₹15"
                totalPrice += 15
            }
            else {
                binding.tvDeliveryCharges.text = "Free"
                totalPrice += 0
            }

            binding.tvGrandTotal.text = totalPrice.toString()
        }
    }

    private fun setStatusBarColor() {
        window?.apply {
            val statusBarColors = ContextCompat.getColor(this@OrderPlaceActivity, R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}
