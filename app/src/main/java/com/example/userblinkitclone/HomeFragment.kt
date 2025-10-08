package com.example.userblinkitclone

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.userblinkitclone.adapters.AdapterCategory
import com.example.userblinkitclone.databinding.FragmentHomeBinding
import com.example.userblinkitclone.models.Category

class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        setStatusBarColor()
        setAllCategories()
        return binding.root
    }

    private fun setAllCategories() {
        val categoryList = ArrayList<Category>()
        val minSize = minOf(Constants.allProductCategory.size, Constants.allProductCategoryIcon.size)

        for (i in 0 until  minSize){
            categoryList.add(Category(Constants.allProductCategory[i], Constants.allProductCategoryIcon[i]))
        }

        binding.rvCategories.adapter = AdapterCategory(categoryList)
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