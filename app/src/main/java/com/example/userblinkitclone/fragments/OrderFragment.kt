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
import com.example.userblinkitclone.adapters.AdapterOrders
import com.example.userblinkitclone.databinding.FragmentOrderBinding
import com.example.userblinkitclone.models.OrderedItem
import com.example.userblinkitclone.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class OrderFragment : Fragment() {
    private lateinit var binding: FragmentOrderBinding
    private val viewModel : UserViewModel by viewModels()
    private lateinit var adapterOrders: AdapterOrders

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrderBinding.inflate(layoutInflater)
        setStatusBarColor()
        onBackButtonClicked()
        getAllOrders()
        return binding.root

    }

    private fun getAllOrders() {
        binding.shimmerViewContainer.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.getAllProducts().collect{orderList ->
                if (orderList.isNotEmpty()){
                    val orderedList = ArrayList<OrderedItem>()
                    for (orders in orderList){
                        val title = StringBuilder()
                        var totalPrice = 0
                        for (products in orders.orderList!!){
                            val price = products.productPrice
                                ?.replace("₹", "")
                                ?.trim()
                                ?.toIntOrNull() ?: 0

                            val quantity = products.productCount ?: 1

                            totalPrice += price * quantity

                            title.append("${products.productCategory}, ")
                        }

                        val orderedItem = OrderedItem(orders.orderId, title.toString(), totalPrice, orders.orderDate, orders.orderStatus)
                        orderedList.add(orderedItem)
                    }
                    adapterOrders = AdapterOrders(requireContext(), ::onOrderItemViewClicked)
                    binding.rvOrders.adapter = adapterOrders
                    adapterOrders.differ.submitList(orderedList)
                    binding.shimmerViewContainer.visibility = View.GONE
                }
            }
        }
    }

    fun onOrderItemViewClicked(orderedItems: OrderedItem){
        val bundle = Bundle()
        bundle.putInt("status", orderedItems.itemStatus!!)
        bundle.putString("orderId", orderedItems.orderId)

        findNavController().navigate(R.id.action_orderFragment_to_orderDetailFragment, bundle)

    }

    private fun onBackButtonClicked() {
        binding.tbOrderFragment.setOnClickListener {
            findNavController().navigate(R.id.action_orderFragment_to_profileFragment)
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