package com.renbin.bookproject.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.renbin.bookproject.data.model.Book
import com.renbin.bookproject.databinding.ItemLayoutBookBinding
import com.renbin.bookproject.core.util.Utility.formatTimestamp
import com.renbin.bookproject.core.util.Utility.loadPdf

// Adapter for displaying a list of books in a RecyclerView
class BookAdapter(
    private var books: List<Book>
): RecyclerView.Adapter<BookAdapter.BookItemViewHolder>() {
    // Listener interface for item click events
    var listener: Listener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookItemViewHolder {
        val binding = ItemLayoutBookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookItemViewHolder(binding)
    }

    override fun getItemCount(): Int = books.size

    override fun onBindViewHolder(holder: BookItemViewHolder, position: Int) {
        val itemBook = books[position]
        holder.bind(itemBook)
    }

    // Set a new list of books and notify the adapter of the data change
    fun setBooks(books: List<Book>){
        this.books = books
        notifyDataSetChanged()
    }

    // View holder for each book item
    inner class BookItemViewHolder(
        private val binding: ItemLayoutBookBinding
    ): RecyclerView.ViewHolder(binding.root){
        // Bind data to the view holder
        fun bind(book: Book){
            binding.run {
                // Set book information in the respective TextViews
                tvTitle.text = book.title
                tvDesc.text = book.desc
                val time = formatTimestamp(book.timestamp)
                tvTime.text = time
                tvCategory.text = book.category

                // Set up click listener for book item
                cvBook.setOnClickListener {
                    listener?.onClick(book)
                }

                // Set up click listener for the "More" button
                btnMore.setOnClickListener {
                    listener?.onItemClick(it, book)
                }

                // Load PDF details into the PDF view
                loadPdf(book, pdfView, tvSize, progressBar, tvPage)
            }
        }
    }


    // Listener interface for item click events
    interface Listener{
        fun onClick(book: Book)
        fun onItemClick(view: View, book: Book)
    }
}