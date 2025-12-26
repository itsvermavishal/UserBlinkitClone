package com.example.userblinkitclone.auth

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.userblinkitclone.R
import com.example.userblinkitclone.utils.Utils
import com.example.userblinkitclone.databinding.FragmentSignBinding

class SignFragment : Fragment() {
    private lateinit var binding: FragmentSignBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignBinding.inflate(layoutInflater)
        setStatusBarColor()
        getUserNumber()
        onContinueClick()

        // 👇 Step 3: Scroll input field into view when focused
        binding.etUserInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.root.post {
                    // scroll the input field into view smoothly
                    binding.root.scrollTo(0, binding.etUserInput.bottom)
                }
            }
        }
        return binding.root
    }

    private fun onContinueClick() {
        binding.btnContinue.setOnClickListener {
            try {
                val number = binding.etUserInput.text.toString()
                if (!number.matches(Regex("^\\d{10}$"))) {
                    Utils.showToast(requireContext(), "Please enter a valid 10-digit number")
                } else {
                    val bundle = Bundle().apply {
                        putString("number", number)
                    }
                    findNavController().navigate(R.id.action_signFragment_to_OTPFragment, bundle)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Utils.showToast(requireContext(), "Navigation failed: ${e.message}")
            }
        }
    }

    private fun getUserNumber() {
        binding.etUserInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(number: CharSequence?, start: Int, before: Int, count: Int) {
                val len = number?.length

                if (len == 10) {
                    binding.btnContinue.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))

                }
                else{
                    binding.btnContinue.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.greyish_blue))
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
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
}