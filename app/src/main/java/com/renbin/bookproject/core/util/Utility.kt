package com.renbin.bookproject.core.util

import android.content.Context
import android.text.format.DateFormat
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.storage.FirebaseStorage
import com.renbin.bookproject.data.model.Book
import java.util.Calendar
import java.util.Locale

object Utility {
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun loadPdf(book: Book, pdfView: PDFView, sizeView: TextView, progressBar: ProgressBar, pageView: TextView) {
        pdfView.recycle()
        progressBar.visibility = View.VISIBLE
        val url = book.url
        if(url.isNotEmpty()){
            FirebaseStorage.getInstance()
                .getReference(book.url)
                .getBytes(Long.MAX_VALUE)
                .addOnSuccessListener {
                    progressBar.visibility = View.GONE
                    val fileSize = it.size.toDouble()
                    val sizeText = when {
                        fileSize >= 1024 * 1024 -> "%.2f MB".format(fileSize / (1024 * 1024))
                        fileSize >= 1024 -> "%.2f KB".format(fileSize / 1024)
                        else -> "%.2f bytes".format(fileSize)
                    }
                    sizeView.text = sizeText
                    pageView.text = pdfView.pageCount.toString()
                    pdfView.fromBytes(it)
                        .enableSwipe(false)
                        .onError {  e ->
                            throw e
                        }
                        .onLoad {
                            pageView.text = "${pdfView.pageCount} page"
                        }
                        .load()
                }
        }
    }


    fun formatTimestamp(timestamp: Long): String {
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = timestamp
        return DateFormat.format("dd/MM/yyyy", cal).toString()
    }

}