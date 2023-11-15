package com.renbin.bookproject.ui.pdf

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.renbin.bookproject.ui.pdf.viewModel.AddPdfViewModel
import com.renbin.bookproject.core.util.Utility.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPdfFragment: BaseAddEditPdfFragment() {
    override val viewModel: AddPdfViewModel by viewModels()
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var pdfUri: Uri? = null

    override fun setupUIComponents() {
        super.setupUIComponents()

        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    pdfUri = data.data // Directly access the Uri
                    Log.d("PDF Upload", "Selected PDF URI: $pdfUri")
                }
            } else {
                showToast(requireContext(), "Cancelled picking pdf")
            }
        }

        binding.run {
            ibAttach.setOnClickListener {
                openFilePicker()
            }

            btnSubmit.setOnClickListener {
                val timestamp = System.currentTimeMillis()
                val filePathAndName = "Books/$timestamp.pdf"
                uploadPdfToStorage(filePathAndName)
            }
        }
    }

    private fun saveBookToFirebase(url: String, link: String){
        val title = binding.etTitle.text.toString()
        val desc = binding.etDesc.text.toString()
        val category = categorySelect

        viewModel.submit(title,desc,category,url,link)
    }

    private fun uploadPdfToStorage(filePathAndName: String) {
        pdfUri?.let { uri ->
            val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)

            // Create a ProgressDialog to show the upload progress
            val progressDialog = ProgressDialog(requireContext())
            progressDialog.setTitle("Uploading PDF")
            progressDialog.setMessage("Please wait...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            val uploadTask: UploadTask = storageReference.putFile(uri)

            // Listen for the upload progress
            uploadTask.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                progressDialog.setMessage("Uploading $progress%")
            }

            // Listen for the upload success or failure
            uploadTask.addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                uriTask.addOnSuccessListener { downloadUrl ->
                    val uploadUrl = downloadUrl.toString()
                    progressDialog.dismiss()
                    saveBookToFirebase(filePathAndName, uploadUrl)
                    showToast(requireContext(), "PDF uploaded successfully")
                }.addOnFailureListener { _ ->
                    progressDialog.dismiss()
                    showToast(requireContext(), "Failed to get download URL")
                }
            }.addOnFailureListener { _ ->
                progressDialog.dismiss()
                showToast(requireContext(), "PDF upload failed")

                // Handle the upload failure
            }
        } ?: showToast(requireContext(), "PDF file is null")
    }



    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        resultLauncher.launch(Intent.createChooser(intent, "Select Pdf"))
    }

}
