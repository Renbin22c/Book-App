package com.renbin.bookproject.ui.category

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.renbin.bookproject.databinding.FragmentAddCategoryPopUpBinding
import com.renbin.bookproject.core.util.Utility.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCategoryPopUpFragment : DialogFragment() {
    private lateinit var binding: FragmentAddCategoryPopUpBinding
    private var category =""
    private val viewModel: AddCategoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddCategoryPopUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            ivClose.setOnClickListener { dismiss() }
            btnAdd.setOnClickListener {
                validateData()
            }
        }

    }

    private fun validateData(){
        category = binding.etTodo.text.toString().trim()

        if(TextUtils.isEmpty(category)){
            showToast(requireContext(), "Please enter category !!!")
        } else{
            viewModel.submit(category)
            dismiss()
        }
    }
}