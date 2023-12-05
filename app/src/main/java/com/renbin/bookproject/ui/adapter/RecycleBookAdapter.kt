package com.renbin.bookproject.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.renbin.bookproject.core.util.Utility
import com.renbin.bookproject.core.util.Utility.loadRecycleBookPdf
import com.renbin.bookproject.data.model.RecycleBook
import com.renbin.bookproject.databinding.ItemLayoutRecycleBookBinding

// Adapter for displaying a list of recycled books in a RecyclerView
class RecycleBookAdapter(
    private var recycleBooks: List<RecycleBook>
): RecyclerView.Adapter<RecycleBookAdapter.RecycleBookItemViewHolder>() {
    // Listener interface for item click events
    var listener: Listener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecycleBookItemViewHolder {
        val binding = ItemLayoutRecycleBookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecycleBookItemViewHolder(binding)
    }

    override fun getItemCount(): Int = recycleBooks.size

    override fun onBindViewHolder(holder: RecycleBookItemViewHolder, position: Int) {
        val itemRecycleBook = recycleBooks[position]
        holder.bind(itemRecycleBook)
    }

    // Set a new list of recycled books and notify the adapter of the data change
    fun setRecycleBooks(recycleBooks: List<RecycleBook>) {
        this.recycleBooks = recycleBooks
        notifyDataSetChanged()
    }

    // View holder for each recycled book item
    inner class RecycleBookItemViewHolder(
        private val binding: ItemLayoutRecycleBookBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        // Bind data to the view holder
        fun bind(recycleBook: RecycleBook) {
            binding.run {
                // Set recycled book information in the respective TextViews
                tvTitle.text = recycleBook.title
                tvDesc.text = recycleBook.desc
                val time = Utility.formatTimestamp(recycleBook.timestamp)
                tvTime.text = time
                tvCategory.text = recycleBook.category

                // Set up click listener for the "More" button
                btnMore.setOnClickListener {
                    listener?.onItemClick(it, recycleBook)
                }

                // Load recycled book details into the PDF view
                loadRecycleBookPdf(recycleBook, pdfView, tvSize, progressBar, tvPage)
            }
        }
    }

    // Listener interface for item click events
    interface Listener {
        fun onItemClick(view: View, recycleBook: RecycleBook)
    }
}