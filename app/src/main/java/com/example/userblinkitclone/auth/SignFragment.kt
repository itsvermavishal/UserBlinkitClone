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
import com.example.userblinkitclone.Utils
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
        return binding.root
    }

    private fun onContinueClick() {
        binding.btnContinue.setOnClickListener {
            val number = binding.etUserInput.text.toString()

            if (number.isEmpty() || number.length != 10) {
                Utils.showToast(requireContext(), "Please enter a valid number")
            }
            else{
                val bundle = Bundle()
                bundle.putString("number", number)
                findNavController().navigate(R.id.action_signFragment_to_OTPFragment, bundle)

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
        activity?.window?.apply {
            val statusBarColors = resources.getColor(R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}