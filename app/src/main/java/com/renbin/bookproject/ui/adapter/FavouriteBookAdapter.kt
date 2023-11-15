package com.renbin.bookproject.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.renbin.bookproject.data.model.Book
import com.renbin.bookproject.databinding.ItemLayoutFavouriteBookBinding
import com.renbin.bookproject.core.util.Utility.formatTimestamp
import com.renbin.bookproject.core.util.Utility.loadPdf

class FavouriteBookAdapter(
    private var books: List<Book>
): RecyclerView.Adapter<FavouriteBookAdapter.FavouriteBookItemViewHolder>() {
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

    fun setFavouriteBooks(books: List<Book>){
        this.books = books
        notifyDataSetChanged()
    }

    inner class FavouriteBookItemViewHolder(
        private val binding: ItemLayoutFavouriteBookBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(book: Book){
            binding.run {
                tvTitle.text = book.title
                tvDesc.text = book.desc
                val time = formatTimestamp(book.timestamp)
                tvTime.text = time
                tvCategory.text = book.category
                cvBook.setOnClickListener {
                    listener?.onClick(book)
                }

                ibFavourite.setOnClickListener {
                    listener?.onItemClick(book)
                }


                loadPdf(book, pdfView, tvSize, progressBar, tvPage)
            }
        }
    }

    interface Listener{
        fun onClick(book: Book)
        fun onItemClick(book: Book)
    }
}