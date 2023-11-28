package com.renbin.bookproject.ui

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.renbin.bookproject.R
import com.renbin.bookproject.core.util.NetworkManager
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        dialog = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_Rounded)
            .setView(R.layout.network_dialog)
            .setCancelable(false)
            .create()

        val networkManager = NetworkManager(this)

        // Set up the network connectivity observer
        networkManager.observe(this){
            if(!it){
                // Show the network dialog if there is no network connectivity
                if (!dialog.isShowing) dialog.show()
            } else{
                // Hide the network dialog if there is network connectivity
                if(dialog.isShowing) dialog.hide()
            }
        }

        window.statusBarColor = Color.BLACK;
    }
}