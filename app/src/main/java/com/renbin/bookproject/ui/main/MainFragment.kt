package com.renbin.bookproject.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.renbin.bookproject.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Finding the NavController associated with this fragment
        navController = NavHostFragment.findNavController(this)

        binding.run {
            btnLogin.setOnClickListener {
                // Navigate to the login destination when the login button is clicked
                val action = MainFragmentDirections.actionGlobalLogin()
                navController.navigate(action)
            }

            btnRegister.setOnClickListener {
                // Navigate to the register destination when the register button is clicked
                val action = MainFragmentDirections.actionGlobalRegister()
                navController.navigate(action)
            }
        }
    }

}