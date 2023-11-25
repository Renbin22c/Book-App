package com.renbin.bookproject.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.renbin.bookproject.core.util.Utility.loadRecycleBookPdf
import com.renbin.bookproject.data.model.RecycleBook
import com.renbin.bookproject.databinding.ItemLayoutRecycleBookBinding

class RecycleBookAdapter(
    private var recycleBooks: List<RecycleBook>
): RecyclerView.Adapter<RecycleBookAdapter.RecycleBookItemViewHolder>() {
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

    fun setRecycleBooks(recycleBooks: List<RecycleBook>) {
        this.recycleBooks = recycleBooks
        notifyDataSetChanged()
    }

    inner class RecycleBookItemViewHolder(
        private val binding: ItemLayoutRecycleBookBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recycleBook: RecycleBook) {
            binding.run {
                tvTitle.text = recycleBook.title
                tvDesc.text = recycleBook.desc
                tvTime.text = recycleBook.timestamp.toString()
                tvCategory.text = recycleBook.category
                btnMore.setOnClickListener {
                    listener?.onItemClick(it, recycleBook)
                }

                loadRecycleBookPdf(recycleBook, pdfView, tvSize, progressBar, tvPage)
            }
        }
    }

    interface Listener {
        fun onItemClick(view: View, recycleBook: RecycleBook)
    }
}