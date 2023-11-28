package com.renbin.bookproject.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.renbin.bookproject.core.service.AuthService
import com.renbin.bookproject.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var authService: AuthService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = NavHostFragment.findNavController(this)

        // Post a delayed action to navigate based on the user's authentication status
        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser = authService.getCurrentUser()
            if(currentUser != null){
                // Navigate to the Home destination if the user is authenticated
                val action = SplashFragmentDirections.actionSplashToHome()
                navController.navigate(action)
            } else{
                // Navigate to the Main destination if the user is not authenticated
                val action = SplashFragmentDirections.actionSplashToMain()
                navController.navigate(action)
            }
        }, 2000)
    }

}