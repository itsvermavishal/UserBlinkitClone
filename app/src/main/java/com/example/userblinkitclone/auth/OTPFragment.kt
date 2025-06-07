package com.example.userblinkitclone.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.userblinkitclone.R
import com.example.userblinkitclone.Utils
import com.example.userblinkitclone.databinding.FragmentOTPBinding
import com.example.userblinkitclone.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class OTPFragment : Fragment() {
    private val viewModel : AuthViewModel by viewModels()
    private lateinit var binding: FragmentOTPBinding
    private lateinit var userNumber : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOTPBinding.inflate(layoutInflater)

        getUserNumber()
        customizingEnteringOTP()
        sendOTP()
        onBackButtonClicked()

        return binding.root
    }

    private fun sendOTP() {
        Utils.showDialog(requireContext(), "Sending OTP")
        viewModel.apply {
            sendOTP(userNumber, requireActivity())
            lifecycleScope.launch {
                otpSent.collect{
                    if (it){
                        Utils.hideDialog()
                        Utils.showToast(requireContext(), "OTP sent successfully")
                    }
                }

            }
        }

    }

    private fun onBackButtonClicked() {
        binding.tbOtpFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_OTPFragment_to_signFragment)
        }
    }

    private fun customizingEnteringOTP() {
        val editTexts = arrayOf(binding.etotp1, binding.etotp2, binding.etotp3, binding.etotp4, binding.etotp5, binding.etotp6)
        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1) {
                        if (i < editTexts.size - 1) {
                            editTexts[i + 1].requestFocus()
                        }
                    } else if (s?.length == 0) {
                        if (i > 0) {
                            editTexts[i - 1].requestFocus()
                        }
                    }
                }
            })
        }
    }

    private fun getUserNumber() {
        val bundle = arguments
        userNumber = bundle?.getString("number").toString()

        binding.tvUserNumber.text = userNumber
    }


}