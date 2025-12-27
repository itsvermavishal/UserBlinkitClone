package com.example.userblinkitclone.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.userblinkitclone.utils.CartListener
import com.example.userblinkitclone.R
import com.example.userblinkitclone.utils.Utils
import com.example.userblinkitclone.adapters.AdapterProduct
import com.example.userblinkitclone.databinding.FragmentCategoryBinding
import com.example.userblinkitclone.databinding.ItemViewProductBinding
import com.example.userblinkitclone.models.Product
import com.example.userblinkitclone.roomdb.CartProductsTable
import com.example.userblinkitclone.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class CategoryFragment : Fragment() {
    private lateinit var binding: FragmentCategoryBinding
    private var category: String? = null
    private val viewModel : UserViewModel by viewModels()
    private lateinit var adapterProduct: AdapterProduct
    private var cartListener : CartListener ? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryBinding.inflate(layoutInflater)
        setStatusBarColor()
        getProductCategory()
        setToolbarTitle()
        fetchCategorypProduct()
        onSearchMenuClicked()
        onNavigationIconClicked()
        return binding.root
    }

    private fun onNavigationIconClicked() {
        binding.tbSearchFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_categoryFragment_to_homeFragment)
        }
    }

    private fun onSearchMenuClicked() {
        binding.tbSearchFragment.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.searchMenu ->{
                    findNavController().navigate(R.id.action_categoryFragment_to_searchFragment)
                    true
                }

                else -> {false}
            }
        }
    }

    private fun fetchCategorypProduct() {
        binding.shimmerViewContainer.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.getCategoryProduct(category!!).collect{
                if (it.isEmpty()){
                    binding.rvProducts.visibility = View.GONE
                    binding.tvText.visibility = View.VISIBLE
                }else{
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvText.visibility = View.GONE
                }
                adapterProduct = AdapterProduct(::onAddButtonClicked, ::onIncrementButtonClicked, ::onDecrementButtonClicked)
                binding.rvProducts.adapter = adapterProduct
                adapterProduct.differ.submitList(it)
                binding.shimmerViewContainer.visibility = View.GONE
            }
        }
    }

    private fun setToolbarTitle() {
        binding.tbSearchFragment.title = category
    }

    private fun getProductCategory() {
        val bundle = arguments
        category = bundle?.getString("category")
    }

    private fun onAddButtonClicked(product: Product, productBinding: ItemViewProductBinding){
        productBinding.tvAdd.visibility = View.GONE
        productBinding.llProductCount.visibility = View.VISIBLE

        //Step - 1
        var itemCount = productBinding.tvProductCount.text.toString().toInt()
        itemCount++
        productBinding.tvProductCount.text = itemCount.toString()
        cartListener?.showCartLayout(1)

        //Step - 2
        product.itemCount = itemCount
        lifecycleScope.launch {
            cartListener?.savingCartItemCount(1)
            saveProductInRoomDb(product)
            viewModel.updateItemCount(product, itemCount)
        }

    }

    private fun onIncrementButtonClicked(
        product: Product,
        productBinding: ItemViewProductBinding
    ) {
        val itemCountInc = productBinding.tvProductCount.text.toString().toIntOrNull()?.plus(1) ?: 1
        val stock = product.productStock ?: 0  // Null-safe

        if (itemCountInc <= stock) {
            productBinding.tvProductCount.text = itemCountInc.toString()
            cartListener?.showCartLayout(1)

            product.itemCount = itemCountInc
            lifecycleScope.launch {
                cartListener?.savingCartItemCount(1)
                saveProductInRoomDb(product)
                viewModel.updateItemCount(product, itemCountInc)
            }
        } else {
            Utils.showToast(requireContext(), "Out of Stock")
        }
    }

    private fun onDecrementButtonClicked(
        product: Product,
        productBinding: ItemViewProductBinding
    ) {
        val itemCountDec = productBinding.tvProductCount.text.toString().toIntOrNull()?.minus(1) ?: 0
        product.itemCount = itemCountDec

        lifecycleScope.launch {
            cartListener?.savingCartItemCount(-1)
            saveProductInRoomDb(product)
            viewModel.updateItemCount(product, itemCountDec)
        }

        if (itemCountDec > 0) {
            productBinding.tvProductCount.text = itemCountDec.toString()
        } else {
            product.productRandomId?.let {
                lifecycleScope.launch {
                    viewModel.deleteCartProduct(it)
                }
                Log.d("VV", it)
            }
            productBinding.tvAdd.visibility = View.VISIBLE
            productBinding.llProductCount.visibility = View.GONE
            productBinding.tvProductCount.text = "0"
        }

        cartListener?.showCartLayout(-1)
    }


    private fun saveProductInRoomDb(product: Product) {

        val id = product.productRandomId
        if (id.isNullOrEmpty()) {
            Log.e("Cart", "❌ Cannot save: productRandomId is NULL")
            return
        }

        val quantityText = buildString {
            product.productQuantity?.let { append(it.toString()) }
            if (!product.productUnit.isNullOrBlank()) {
                append(product.productUnit)
            }
        }

        val priceText = product.productPrice?.let { "₹$it" } ?: "₹0"

        val imageUrl = product.productImageUris?.firstOrNull() ?: ""   // SAFE

        val count = product.itemCount ?: 1

        val cartProduct = CartProductsTable(
            productId = id,
            productTitle = product.productTitle ?: "",
            productQuantity = quantityText,
            productPrice = priceText,
            productCount = count,
            productStock = product.productStock ?: 0,
            productImage = imageUrl,      // SAFE
            productCategory = product.productCategory ?: "",
            adminUID = product.adminUID ?: "",
            productType = product.productType ?: ""
        )

        lifecycleScope.launch {
            viewModel.insertCartProduct(cartProduct)
        }
    }


    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.orange)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CartListener){
            cartListener = context
        }
        else{
            throw RuntimeException("$context must implement CartListener")
        }
    }

}