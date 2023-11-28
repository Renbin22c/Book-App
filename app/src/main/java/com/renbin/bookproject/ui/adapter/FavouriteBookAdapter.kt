package com.renbin.bookproject.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.renbin.bookproject.data.model.Book
import com.renbin.bookproject.databinding.ItemLayoutFavouriteBookBinding
import com.renbin.bookproject.core.util.Utility.formatTimestamp
import com.renbin.bookproject.core.util.Utility.loadPdf

// Adapter for displaying a list of favorite books in a RecyclerView
class FavouriteBookAdapter(
    private var books: List<Book>
): RecyclerView.Adapter<FavouriteBookAdapter.FavouriteBookItemViewHolder>() {
    // Listener interface for item click events
    var listener: Listener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteBookItemViewHolder {
        val binding = ItemLayoutFavouriteBookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavouriteBookItemViewHolder(binding)
    }

    override fun getItemCount(): Int  = books.size

    override fun onBindViewHolder(holder: FavouriteBookItemViewHolder, position: Int) {
        val itemBook = books[position]
        holder.bind(itemBook)
    }

    // Set a new list of favorite books and notify the adapter of the data change
    fun setFavouriteBooks(books: List<Book>){
        this.books = books
        notifyDataSetChanged()
    }

    // View holder for each favorite book item
    inner class FavouriteBookItemViewHolder(
        private val binding: ItemLayoutFavouriteBookBinding
    ): RecyclerView.ViewHolder(binding.root) {
        // Bind data to the view holder
        fun bind(book: Book){
            binding.run {
                // Set book information in the respective TextViews
                tvTitle.text = book.title
                tvDesc.text = book.desc
                val time = formatTimestamp(book.timestamp)
                tvTime.text = time
                tvCategory.text = book.category

                // Set up click listener for favorite book item
                cvBook.setOnClickListener {
                    listener?.onClick(book)
                }

                // Set up click listener for the "Favourite" button
                ibFavourite.setOnClickListener {
                    listener?.onItemClick(book)
                }

                // Load PDF details into the PDF view
                loadPdf(book, pdfView, tvSize, progressBar, tvPage)
            }
        }
    }

    // Listener interface for item click events
    interface Listener{
        fun onClick(book: Book)
        fun onItemClick(book: Book)
    }
}