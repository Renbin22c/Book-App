package com.renbin.bookproject.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.renbin.bookproject.data.model.Book
import com.renbin.bookproject.databinding.ItemLayoutBookBinding
import com.renbin.bookproject.core.util.Utility.formatTimestamp
import com.renbin.bookproject.core.util.Utility.loadPdf

class BookAdapter(
    private var books: List<Book>
): RecyclerView.Adapter<BookAdapter.BookItemViewHolder>() {
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

    fun setBooks(books: List<Book>){
        this.books = books
        notifyDataSetChanged()
    }

    inner class BookItemViewHolder(
        private val binding: ItemLayoutBookBinding
    ): RecyclerView.ViewHolder(binding.root){
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

                btnMore.setOnClickListener {
                    listener?.onItemClick(it, book)
                }

                loadPdf(book, pdfView, tvSize, progressBar, tvPage)
            }
        }
    }

    interface Listener{
        fun onClick(book: Book)
        fun onItemClick(view: View, book: Book)
    }
}