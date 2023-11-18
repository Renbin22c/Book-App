package com.renbin.bookproject.ui.pdf

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import android.view.animation.AnimationUtils
import com.renbin.bookproject.R
import com.renbin.bookproject.databinding.FragmentBaseAddEditPdfBinding
import com.renbin.bookproject.ui.base.BaseFragment
import com.renbin.bookproject.ui.pdf.viewModel.BaseAddEditPdfViewModel
import kotlinx.coroutines.launch

abstract class BaseAddEditPdfFragment : BaseFragment<FragmentBaseAddEditPdfBinding>() {
    abstract override val viewModel: BaseAddEditPdfViewModel
    private lateinit var categoryAdapter: ArrayAdapter<String>
    protected var categorySelect = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBaseAddEditPdfBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupViewModelObserver() {
        lifecycleScope.launch {
            viewModel.categoryName.collect {
                categoryAdapter = ArrayAdapter(
                    requireContext(),
                    androidx.transition.R.layout.support_simple_spinner_dropdown_item,
                    it
                )
                binding.autoCompleteCategory.setAdapter(categoryAdapter)
            }
        }

        lifecycleScope.launch {
            viewModel.success.collect{
                navController.popBackStack()
            }
        }
    }

    override fun setupUIComponents() {
        super.setupUIComponents()

        categoryAdapter = ArrayAdapter(
            requireContext(),
            androidx.transition.R.layout.support_simple_spinner_dropdown_item,
            emptyList()
        )

        binding.run {
            autoCompleteCategory.addTextChangedListener {
                categorySelect = it.toString()
                Log.d("debugging", categorySelect)
            }

            ibBack.setOnClickListener {
                navController.popBackStack()
            }
        }
    }
}