package com.renbin.bookproject.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.renbin.bookproject.data.model.Category
import com.renbin.bookproject.databinding.ItemLayoutCategoryBinding

// Adapter for displaying a list of categories in a RecyclerView
class CategoryAdapter(
    private var category: List<Category>
): RecyclerView.Adapter<CategoryAdapter.CategoryItemViewHolder>() {
    // Listener interface for item click events
    var listener: Listener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryItemViewHolder {
        val binding = ItemLayoutCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryItemViewHolder(binding)
    }

    override fun getItemCount() = category.size

    override fun onBindViewHolder(holder: CategoryItemViewHolder, position: Int) {
        val itemCategory = category[position]
        holder.bind(itemCategory)
    }

    // Set a new list of categories and notify the adapter of the data change
    fun setCategories(categories: List<Category>) {
        this.category = categories
        notifyDataSetChanged()
    }

    // View holder for each category item
    inner class CategoryItemViewHolder(
        private val binding: ItemLayoutCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        // Bind data to the view holder
        fun bind(category: Category) {
            binding.run {
                // Set the category name in the TextView
                tvTitle.text = category.category

                // Set up click listener for category item
                cvCategory.setOnClickListener {
                    listener?.onClick(category)
                }

                // Set up long click listener for category item
                cvCategory.setOnLongClickListener {
                    // Toggle visibility of delete button on long click
                    ibDeleteCategory.visibility =
                        if (ibDeleteCategory.visibility == View.VISIBLE) View.GONE
                        else View.VISIBLE
                    true
                }

                // Set up click listener for the delete button
                ibDeleteCategory.setOnClickListener {
                    // Notify the listener when delete button is clicked and hide the button
                    listener?.onDelete(category)
                    ibDeleteCategory.visibility = View.GONE
                }
            }
        }
    }

    // Listener interface for item click events
    interface Listener {
        fun onClick(category: Category)
        fun onDelete(category: Category)
    }
}