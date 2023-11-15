package com.renbin.bookproject.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.renbin.bookproject.data.model.Category
import com.renbin.bookproject.databinding.ItemLayoutCategoryBinding

class CategoryAdapter(
    private var category: List<Category>
): RecyclerView.Adapter<CategoryAdapter.CategoryItemViewHolder>() {
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

    fun setCategories(categories: List<Category>) {
        this.category = categories
        notifyDataSetChanged()
    }

    inner class CategoryItemViewHolder(
        private val binding: ItemLayoutCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) {
            binding.run {
                tvTitle.text = category.category
                cvCategory.setOnClickListener {
                    listener?.onClick(category)
                }

                cvCategory.setOnLongClickListener {
                    ibDeleteCategory.visibility =
                        if (ibDeleteCategory.visibility == View.VISIBLE) View.GONE
                        else View.VISIBLE
                    true
                }

                ibDeleteCategory.setOnClickListener {
                    listener?.onDelete(category)
                }
            }
        }
    }

    interface Listener {
        fun onClick(category: Category)
        fun onDelete(category: Category)
    }
}