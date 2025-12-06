package com.example.userblinkitclone.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.userblinkitclone.CartListener
import com.example.userblinkitclone.R
import com.example.userblinkitclone.databinding.ActivityUsersMainBinding

class UsersMainActivity : AppCompatActivity() , CartListener{
    private lateinit var binding: ActivityUsersMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun showCartLayout(itemCount: Int) {
        val previousCount = binding.tvNumberOfProductCount.text.toString().toInt()
        val updatedCount = previousCount + itemCount

        if (updatedCount > 0){
            binding.llcart.visibility = View.VISIBLE
            binding.tvNumberOfProductCount.text = updatedCount.toString()
        }
        else{
            binding.llcart.visibility = View.GONE
            binding.tvNumberOfProductCount.text = "0"
        }

    }
}