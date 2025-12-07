package com.example.userblinkitclone.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.DialogTitle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.userblinkitclone.Constants
import com.example.userblinkitclone.R
import com.example.userblinkitclone.adapters.AdapterCategory
import com.example.userblinkitclone.databinding.FragmentHomeBinding
import com.example.userblinkitclone.models.Category
import com.example.userblinkitclone.viewmodels.UserViewModel

class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private val viewModel: UserViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        setStatusBarColor()
        setAllCategories()
        navigatingToSearchFragment()
        get()
        return binding.root
    }

    private fun get(){
        viewModel.getAll().observe(viewLifecycleOwner){
            for (i in it){
                Log.d("vvv", i.productTitle.toString())
                Log.d("vvv", i.productCount.toString())
            }
        }
    }

    private fun navigatingToSearchFragment() {
        binding.searchCv.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun setAllCategories() {
        val categoryList = ArrayList<Category>()
        val minSize = minOf(Constants.allProductCategory.size, Constants.allProductCategoryIcon.size)

        for (i in 0 until  minSize){
            categoryList.add(
                Category(
                    Constants.allProductCategory[i],
                    Constants.allProductCategoryIcon[i]
                )
            )
        }

        binding.rvCategories.adapter = AdapterCategory(categoryList, ::onCategoryIconClicked)
    }

    fun onCategoryIconClicked(category: Category){
        val bundle = Bundle()
        bundle.putString("category", category.title)
        findNavController().navigate(R.id.action_homeFragment_to_categoryFragment, bundle)
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