package com.renbin.bookproject.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
        networkManager.observe(this){
            if(!it){
                if (!dialog.isShowing) dialog.show()
            } else{
                if(dialog.isShowing) dialog.hide()
            }
        }
    }
}