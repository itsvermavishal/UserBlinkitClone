package com.example.userblinkitclone.utils

import com.example.userblinkitclone.R

object Constants {

    val MERCHANT_ID = "PGTESTPAYUAT"
    val SALT_KEY = "099eb0cd-02cf-4e2a-8aca-3e6c6aff0399"
    var apiEndPoint = "/pg/v1/pay"
    val merchantTransactionId= "txnId"

    val allProductCategory = arrayOf(
        "Vegetables and fruits",
        "Munchies",
        "Dairy breakfast",
        "Cold Drinks & Juices",
        "Sweet Tooth",
        "Atta Rice & Dal",
        "Tea & Coffee",
        "Bakery & Buscuits",
        "Masaala",
        "Sauces & Pickles",
        "Chicken & Fish",
        "Pan Corner",
        "Organic & Premium",
        "Baby Care",
        "Pharma & Health",
        "Cleaning Essentials",
        "Home & Offices",
        "Personal Grooming",
        "Pet care"
    )
    val allProductCategoryIcon = arrayOf(
        R.drawable.vegetable,
        R.drawable.munchies,
        R.drawable.dairy_breakfast,
        R.drawable.cold_and_juices,
        R.drawable.sweet_tooth,
        R.drawable.atta_rice,
        R.drawable.tea,
        R.drawable.bakery_biscuits,
        R.drawable.masala,
        R.drawable.sauce_spreads,
        R.drawable.chicken_meat,
        R.drawable.paan_corner,
        R.drawable.organic_premium,
        R.drawable.baby_care,
        R.drawable.pharma_wellness,
        R.drawable.cleaning,
        R.drawable.home_office,
        R.drawable.personal_care,
        R.drawable.pet_care
    )
}