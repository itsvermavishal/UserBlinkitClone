package com.example.userblinkitclone.utils

import android.widget.Filter
import com.example.userblinkitclone.adapters.AdapterProduct
import com.example.userblinkitclone.models.Product
import java.util.Locale
import kotlin.collections.filter
import kotlin.text.contains
import kotlin.text.isNullOrEmpty
import kotlin.text.trim
import kotlin.text.uppercase

class FilteringProducts(
    val adapter: AdapterProduct,
    val filter: ArrayList<Product>
) : Filter() {
    override fun performFiltering(constraint: CharSequence?): FilterResults? {
        val result = FilterResults()
        if (!constraint.isNullOrEmpty()) {
            val query = constraint.toString().trim().uppercase(Locale.getDefault())
            val filteredList = filter.filter { product ->
                val title = product.productTitle?.uppercase(Locale.getDefault()) ?: ""
                val category = product.productCategory?.uppercase(Locale.getDefault()) ?: ""
                val type = product.productType?.uppercase(Locale.getDefault()) ?: ""
                val price = product.productPrice?.toString()?.uppercase(Locale.getDefault()) ?: ""

                title.contains(query) ||
                category.contains(query) ||
                type.contains(query) ||
                price.contains(query)
            }


            result.values = filteredList
            result.count = filteredList.size
        } else {
            result.values = filter
            result.count = filter.size
        }

        return result
    }

    override fun publishResults(
        constraint: CharSequence?,
        results: FilterResults
    ) {
        val list = results.values as List<Product>
        adapter.differ.submitList(list)
    }
}