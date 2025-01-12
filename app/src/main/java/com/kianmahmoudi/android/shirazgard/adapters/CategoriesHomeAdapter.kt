package com.kianmahmoudi.android.shirazgard.adapters

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.data.Category
import com.kianmahmoudi.android.shirazgard.databinding.ItemCategoryHomeBinding

class CategoriesHomeAdapter(private val navController: NavController) :
    RecyclerView.Adapter<CategoriesHomeAdapter.ViewHolder>() {


    private val diffCallback = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    })

    fun submitData(list: List<Category>) {
        diffCallback.submitList(list)
    }

    inner class ViewHolder(val binding: ItemCategoryHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) {
            binding.itemCategoryHomeIc.setImageResource(category.image)
            binding.itemCategoryHomeTitle.setText(category.name)

            itemView.setOnClickListener {
                val bundle = bundleOf(
                    "categoryName" to category.name,
                    "categoryType" to category.type
                )
                navController.navigate(R.id.categoryPlacesFragment,bundle)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCategoryHomeBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
        return diffCallback.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = diffCallback.currentList.get(position)
        holder.bind(category)
    }
}