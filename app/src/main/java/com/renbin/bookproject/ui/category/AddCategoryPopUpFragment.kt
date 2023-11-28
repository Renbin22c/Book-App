package com.renbin.bookproject.ui.category

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.renbin.bookproject.R
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
            // Close the dialog when the close button is clicked
            ivClose.setOnClickListener { dismiss() }

            // Validate and submit the category when the add button is clicked
            btnAdd.setOnClickListener { validateData() }
        }

    }

    // Validate user input and submit the category
    private fun validateData(){
        category = binding.etTodo.text.toString().trim()

        if(TextUtils.isEmpty(category)){
            showToast(requireContext(), "Please enter category !!!", R.drawable.ic_pdf)
        } else{
            // Submit the category to the ViewModel for processing
            viewModel.submit(category)

            // Close the dialog after submission
            dismiss()
        }
    }
}