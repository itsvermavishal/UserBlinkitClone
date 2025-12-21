package com.example.userblinkitclone.fragments

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
import com.example.userblinkitclone.R
import com.example.userblinkitclone.adapters.AdapterCartProducts
import com.example.userblinkitclone.databinding.FragmentOrderDetailBinding
import com.example.userblinkitclone.viewmodels.UserViewModel
import kotlinx.coroutines.launch
import kotlin.getValue

class OrderDetailFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailBinding
    private val viewModel : UserViewModel by viewModels()
    private lateinit var adapterCartProducts: AdapterCartProducts
    private var status = 0
    private var orderId = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrderDetailBinding.inflate(layoutInflater)
        setStatusBarColor()
        getValues()
        settingStatus()
        lifecycleScope.launch {
            getOrderdProducts()
        }
        onBackButtonClicked()
        return binding.root
    }

    suspend fun getOrderdProducts() {
        viewModel.getOrderdProducts(orderId).collect {cartList ->
            adapterCartProducts = AdapterCartProducts()
            binding.rvproductsItems.adapter = adapterCartProducts
            adapterCartProducts.differ.submitList(cartList)
        }
    }

    private fun settingStatus() {
       /* when(status){
            0 -> {
                binding.iv1.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue)
            }
            1 -> {
                binding.iv1.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue)
                binding.iv2.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue)
                binding.view1.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue)
            }
            2 -> {
                binding.iv1.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue)
                binding.iv2.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue)
                binding.view1.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue)
                binding.iv3.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue)
                binding.view2.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue)
            }
            3 -> {
                binding.iv1.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue)
                binding.iv2.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue)
                binding.view1.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue)
                binding.iv3.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue)
                binding.view2.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue)
                binding.iv4.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue)
                binding.view3.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue)
            }
        }*/

        // 0r use this for above code
        val statusToViews = mapOf(
            0 to listOf(binding.iv1),
            1 to listOf(binding.iv1, binding.iv2, binding.view1),
            2 to listOf(binding.iv1, binding.iv2, binding.iv3, binding.view1, binding.view2),
            3 to listOf(binding.iv1, binding.iv2, binding.iv3, binding.iv4, binding.view1, binding.view2, binding.view3)
        )
        val viewsToTint = statusToViews.getOrDefault(status, emptyList())

        for (view in viewsToTint){
            view.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue)
        }
    }

    private fun getValues() {
        val bundle = arguments
        status = bundle?.getInt("status")!!
        orderId = bundle.getString("orderId").toString()
    }

    private fun onBackButtonClicked() {
        binding.tbOrderDetailFragment.setOnClickListener {
            findNavController().navigate(R.id.action_orderDetailFragment_to_orderFragment)
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

}