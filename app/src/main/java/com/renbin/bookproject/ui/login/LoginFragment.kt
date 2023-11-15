package com.renbin.bookproject.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.renbin.bookproject.R
import com.renbin.bookproject.databinding.FragmentLoginBinding
import com.renbin.bookproject.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    override val viewModel: LoginViewModel by viewModels()
    private var forgotMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupUIComponents() {
        super.setupUIComponents()

        binding.run {
            tvSignUp.setOnClickListener {
                val action = LoginFragmentDirections.actionGlobalRegister()
                navController.navigate(action)
            }

            btnLogin.setOnClickListener {
                val email = etEmail.text.toString()
                val pass = etPassword.text.toString()
                if (forgotMode){
                    viewModel.resetPassword(email)
                }else {
                    viewModel.login(email, pass)
                }
            }

            tvForgot.setOnClickListener {
                if (forgotMode){
                    tilPassword.visibility = View.VISIBLE
                    tvForgot.text = getString(R.string.forgot_password)
                    tvTitle.text = getString(R.string.please_login)
                    btnLogin.text = getString(R.string.login)

                    forgotMode = false

                } else {
                    tilPassword.visibility = View.GONE
                    tvForgot.text = getString(R.string.use_password_to_login)
                    tvTitle.text = getString(R.string.reset_password)
                    btnLogin.text = getString(R.string.send)

                    forgotMode = true
                }
            }
        }
    }

    override fun setupViewModelObserver() {
        super.setupViewModelObserver()

        lifecycleScope.launch {
            viewModel.success.collect {
                if (!forgotMode) {
                    val action = LoginFragmentDirections.actionLoginToHome()
                    navController.navigate(action)
                }
            }
        }
    }
}