package com.example.userblinkitclone.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.userblinkitclone.CartListener
import com.example.userblinkitclone.R
import com.example.userblinkitclone.adapters.AdapterProduct
import com.example.userblinkitclone.databinding.FragmentCategoryBinding
import com.example.userblinkitclone.databinding.ItemViewProductBinding
import com.example.userblinkitclone.models.Product
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
                adapterProduct = AdapterProduct(::onAddButtonClicked)
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
    }

    private fun setStatusBarColor() {
        requireActivity().window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.yellow)
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