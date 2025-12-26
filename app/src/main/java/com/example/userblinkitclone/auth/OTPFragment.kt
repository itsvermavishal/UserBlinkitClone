package com.example.userblinkitclone.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.userblinkitclone.R
import com.example.userblinkitclone.utils.Utils
import com.example.userblinkitclone.activity.UsersMainActivity
import com.example.userblinkitclone.databinding.FragmentOTPBinding
import com.example.userblinkitclone.models.Users
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
        onLoginButtonClicked()
        onBackButtonClicked()

        return binding.root
    }

    private fun onLoginButtonClicked() {
        binding.btnLogin.setOnClickListener {
            Utils.showDialog(requireContext(), "Signing you")
            val editTexts = arrayOf(binding.etotp1, binding.etotp2, binding.etotp3, binding.etotp4, binding.etotp5, binding.etotp6)
            val otp = editTexts.joinToString("") { it.text.toString() }

            if (otp.length < editTexts.size){
                Utils.showToast(requireContext(), "Please enter a valid OTP")
            }
            else{
                editTexts.forEach { it.text?.clear(); it.clearFocus() }
            }
            verifyOTP(otp)
        }
    }

    private fun verifyOTP(otp: String) {


        viewModel.signInWithPhoneAuthCredential(otp, userNumber)

        lifecycleScope.launch {
            viewModel.isSignedInSuccessfully.collect {success ->
                if (success){
                    val uid = Utils.getCurrentUserId()
                    val user = Users(uid = uid, userPhoneNumber = userNumber, userAddress = " ")

                    // Save user in Firebase
                    Utils.showDialog(requireContext(), "Saving user info...")
                    viewModel.createOrUpdateUser(user)
                    Utils.hideDialog()

                    Utils.showToast(requireContext(), "Logged In...")
                    startActivity(Intent(requireContext(), UsersMainActivity::class.java))
                    requireActivity().finish()
                }
            }
        }
    }

    private fun sendOTP() {
        Utils.showDialog(requireContext(), "Sending OTP")
        viewModel.apply {
            sendOTP(userNumber, requireActivity())
            lifecycleScope.launch {
                viewModel.otpSent.collect { sent ->
                    if (sent == true) {
                        Utils.hideDialog()
                        Utils.showToast(requireContext(), "OTP sent successfully")
                    } else if (sent == false) {
                        Utils.hideDialog()
                        // Error will be shown by otpError collector
                    }
                }
            }
            lifecycleScope.launch {
                viewModel.otpError.collect { errorMsg ->
                    if (!errorMsg.isNullOrEmpty()) {
                        Utils.hideDialog()
                        Utils.showToast(requireContext(), errorMsg)
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
        val number = arguments?.getString("number")

        if (number.isNullOrEmpty()) {
            Utils.showToast(requireContext(), "Phone number missing. Navigating back.")
            findNavController().popBackStack()
        } else {
            userNumber = number
            binding.tvUserNumber.text = number
        }
    }



}