package com.renbin.bookproject.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.renbin.bookproject.R
import com.renbin.bookproject.databinding.FragmentRegisterBinding
import com.renbin.bookproject.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {
    override val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupUIComponents() {
        super.setupUIComponents()

        binding.run {
            tvSignUp.setOnClickListener {
                val action = RegisterFragmentDirections.actionGlobalLogin()
                navController.navigate(action)
            }

            btnRegister.setOnClickListener {
                val email = etEmail.text.toString()
                val pass = etPassword.text.toString()
                val conPass = etPassword2.text.toString()
                viewModel.register(email, pass, conPass)
            }
        }
    }

    override fun setupViewModelObserver() {
        super.setupViewModelObserver()

        lifecycleScope.launch {
            viewModel.success.collect{
                val action = RegisterFragmentDirections.actionGlobalLogin()
                navController.navigate(action)
            }
        }
    }
}