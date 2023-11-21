package com.renbin.bookproject.ui.pdf

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.renbin.bookproject.R
import com.renbin.bookproject.core.util.Utility.showToast
import com.renbin.bookproject.ui.pdf.viewModel.EditPdfViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditPdfFragment: BaseAddEditPdfFragment() {
    override val viewModel: EditPdfViewModel by viewModels()
    private val args: EditPdfFragmentArgs by navArgs()

    override fun setupUIComponents() {
        super.setupUIComponents()
        binding.tvTitle.setText(R.string.update_book)
        binding.btnSubmit.setText(R.string.update)
        binding.ibAttach.visibility = View.GONE
        viewModel.getBook(args.bookId)

        binding.btnSubmit.setOnClickListener {
            saveBookToFirebase()
        }
    }

    override fun setupViewModelObserver() {
        super.setupViewModelObserver()
        lifecycleScope.launch {
            viewModel.book.collect {
                binding.etTitle.setText(it.title)
                binding.etDesc.setText(it.desc)
                binding.autoCompleteCategory.setText(it.category)
            }
        }
    }

    private fun saveBookToFirebase(){
        val title = binding.etTitle.text.toString().trim()
        val desc = binding.etDesc.text.toString().trim()
        val category = categorySelect

        val error = validate(title, desc, category)

        if(!error.isNullOrEmpty()){
            showToast(requireContext(), error.toString(), R.drawable.ic_pdf)
        } else{
            viewModel.submit(title,desc,category)
        }
    }
}