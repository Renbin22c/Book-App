package com.renbin.bookproject.ui.pdf

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.google.firebase.storage.FirebaseStorage
import com.renbin.bookproject.databinding.FragmentPdfDetailsBinding

class PdfDetailsFragment : Fragment() {
    private val args: PdfDetailsFragmentArgs by navArgs()
    private lateinit var binding: FragmentPdfDetailsBinding
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPdfDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = NavHostFragment.findNavController(this)

        binding.run {
            // Navigate back when the back button is clicked
            ibBack.setOnClickListener {
                navController.popBackStack()
            }
        }

        // Load PDF from the provided URL
        loadPdfFromUrl(args.bookUrl)

    }

    // Function to load PDF from the given URL
    private fun loadPdfFromUrl(pdfUrl: String){
        FirebaseStorage.getInstance()
            .getReference(pdfUrl)
            .getBytes(Long.MAX_VALUE)
            .addOnSuccessListener {
                // Hide progress bar once the PDF is loaded
                binding.progressBar.visibility = View.GONE
                // Load PDF content into the PDFView
                binding.pdfView.fromBytes(it)
                    .swipeHorizontal(false)
                    .onPageChange{ page, pageCount ->
                        // Update the page count text when the page changes
                        val currentPage = page + 1
                        binding.tvPage.text = "$currentPage / $pageCount"
                    }
                    .onError{  e ->
                        // Handle errors, throw an exception for simplicity
                        throw e
                    }
                    .load()
            }
    }
}