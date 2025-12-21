package com.example.userblinkitclone.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.userblinkitclone.CartListener
import com.example.userblinkitclone.Constants
import com.example.userblinkitclone.R
import com.example.userblinkitclone.Utils
import com.example.userblinkitclone.adapters.AdapterCartProducts
import com.example.userblinkitclone.databinding.ActivityOrderPlaceBinding
import com.example.userblinkitclone.databinding.AddressLayoutBinding
import com.example.userblinkitclone.models.Orders
import com.example.userblinkitclone.viewmodels.UserViewModel
import com.phonepe.intent.sdk.api.PhonePeInitException
import com.phonepe.intent.sdk.api.PhonePeKt
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.security.MessageDigest

class OrderPlaceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderPlaceBinding
    private val viewModel : UserViewModel by viewModels()
    private lateinit var adapterCartProducts: AdapterCartProducts
    private lateinit var b2BPGRequest: B2BPGRequest
    private var cartListener : CartListener ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setStatusBarColor()
        enableEdgeToEdge()
        backToUserMainActivity()
        getAllCartProducts()
        initializePhonePe()
        onPlaceOrderClicked()
    }

    private fun initializePhonePe() {

        val data = JSONObject()
        // Initialize PhonePe SDK
        PhonePeKt.init(this, PhonePeEnvironment.UAT, Constants.MERCHANT_ID, "")

        data.put("merchantId", Constants.MERCHANT_ID)
        data.put("merchantTransactionId", Constants.merchantTransactionId)
        data.put("amount", 200) //Long. manadatory
        data.put("mobileNumber", "9919350336") // String. manadatory
        data.put("callbackUrl", "https://webhook.site/callback-ur") //String. Manadatory

        val paymentInstrument = JSONObject()
        paymentInstrument.put("type", "UPI_INTENT")
        paymentInstrument.put("targetApp", "com.phonepe.simulator")
        data.put("paymentInstrument", paymentInstrument)

        val deviceContext = JSONObject()
        deviceContext.put("deviceType", "ANDROID")
        data.put("deviceContext", deviceContext)

        val payloadBase64 = Base64.encodeToString(
            data.toString().toByteArray(Charsets.UTF_8),
            Base64.NO_WRAP
        )

        val checksum = sha256(payloadBase64 + Constants.apiEndPoint + Constants.SALT_KEY) + "###1";

        b2BPGRequest = B2BPGRequestBuilder()
            .setData(payloadBase64)
            .setChecksum(checksum)
            .setUrl(Constants.apiEndPoint)
            .build()

    }

    private fun sha256(input: String): String{
        val bytes= input.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold (""){ str, it -> str + "%02x".format(it) }
    }

    private fun onPlaceOrderClicked() {
        binding.btnPlaceOrder.setOnClickListener {
            viewModel.getAddressStatus().observe(this) { status ->
                if (status){
                    getPaymentView()
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

    val phonePeView = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == RESULT_OK){
            checkPaymentStatus()
        }
    }

    private fun checkPaymentStatus() {
        val xVerify =sha256("/pg/v1/status/${Constants.MERCHANT_ID}/${Constants.merchantTransactionId}${Constants.SALT_KEY}")+"###1"
        val headers = mapOf(
            "Content-Type" to "application/json",
            "x-verify" to xVerify,
            "X-MERCHANT-ID" to Constants.MERCHANT_ID,
        )

        lifecycleScope.launch {
            viewModel.checkPayment(headers)
            viewModel.paymentStatus.collect { status ->
                if (status){
                    Utils.showToast(this@OrderPlaceActivity, "Payment Successful")
                    // order save, delete products
                    saveOrder()
                    deleteCartProducts()
                    viewModel.savingCartItemCount(0)
                    cartListener?.hideCartlayout()
                    Utils.hideDialog()
                    startActivity(Intent(this@OrderPlaceActivity, UsersMainActivity::class.java))
                    finish()
                }
                else{
                    Utils.showToast(this@OrderPlaceActivity, "Payment Failed")
                }
            }
        }
    }

    private fun deleteCartProducts(){
        viewModel.deleteCartProducts()
    }

    private fun saveOrder(){
        viewModel.getAll().observe(this) { cartProductsList ->
            if(cartProductsList.isNotEmpty()){
                viewModel.getUserAddress { address ->
                    val order = Orders(
                        orderId = Utils.getRandomId(), orderList = cartProductsList,
                        userAddress = address, orderStatus = 0, orderDate = Utils.getCurrentDate(),
                        orderingUserId = Utils.getCurrentUserId()
                    )
                    viewModel.saveOderproducts(order)
                }
                for (products in cartProductsList){
                    val count = products.productCount
                    val stock = products.productStock?.minus(count!!)
                    if (stock != null){
                        viewModel.saveProductsAfterOrder(stock, products)
                    }
                }
            }
        }
    }

    private fun getPaymentView(){
        try {
            PhonePeKt.getImplicitIntent(this, b2BPGRequest, "com.phonepe.simulator")
                .let{
                    phonePeView.launch(it)
                }
        }
        catch (e : PhonePeInitException){
            Utils.showToast(this, e.message.toString())
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

        getPaymentView()

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
