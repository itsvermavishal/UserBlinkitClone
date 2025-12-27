package com.example.userblinkitclone.utils

import android.widget.Filter
import com.example.userblinkitclone.adapters.AdapterProduct
import com.example.userblinkitclone.models.Product
import java.util.Locale

class FilteringProducts(
    private val adapter: AdapterProduct,
    private val originalList: ArrayList<Product>
) : Filter() {

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val results = FilterResults()
        val filteredList = ArrayList<Product>()

        val query = constraint
            ?.toString()
            ?.trim()
            ?.lowercase(Locale.getDefault())

        if (query.isNullOrEmpty()) {
            filteredList.addAll(originalList)
        } else {
            for (product in originalList) {

                val title = product.productTitle?.lowercase() ?: ""
                val category = product.productCategory?.lowercase() ?: ""
                val type = product.productType?.lowercase() ?: ""
                val price = product.productPrice?.toString() ?: ""

                // 🔥 LETTER-BY-LETTER SEARCH
                if (
                    title.contains(query) ||
                    category.contains(query) ||
                    type.contains(query) ||
                    price.contains(query)
                ) {
                    filteredList.add(product)
                }
            }
        }

        results.values = filteredList
        results.count = filteredList.size
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        @Suppress("UNCHECKED_CAST")
        val list = results.values as ArrayList<Product>
        adapter.differ.submitList(list.toList())
    }
}
