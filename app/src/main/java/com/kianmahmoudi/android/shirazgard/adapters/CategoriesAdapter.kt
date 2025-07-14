package com.kianmahmoudi.android.shirazgard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kianmahmoudi.android.shirazgard.data.Category
import com.kianmahmoudi.android.shirazgard.databinding.ItemCategoryBinding
import com.kianmahmoudi.android.shirazgard.fragments.CategoriesFragmentDirections
import com.kianmahmoudi.android.shirazgard.fragments.home.HomeFragmentDirections

class CategoriesAdapter(private val navController: NavController, private val fragment: String) :
    RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {


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

    inner class ViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) {
            binding.itemCategoryIc.setImageResource(category.image)
            binding.itemCategoryTitle.setText(category.name)

            itemView.setOnClickListener {
                if (fragment.equals("home")){
                    val action = HomeFragmentDirections.actionHomeFragmentToCategoryPlacesFragment(
                        categoryName = category.name,
                        categoryType = category.type
                    )
                    navController.navigate(action)
                }else if (fragment.equals("categories")){
                    val action = CategoriesFragmentDirections.actionCategoriesFragmentToCategoryPlacesFragment(
                        categoryName = category.name,
                        categoryType = category.type
                    )
                    navController.navigate(action)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCategoryBinding.inflate(
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