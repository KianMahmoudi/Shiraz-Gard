package com.kianmahmoudi.android.shirazgard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.databinding.ItemCategoryPlacesBinding
import com.parse.ParseObject
import timber.log.Timber
import java.util.ArrayList
import java.util.Locale

class CategoryPlacesAdapter(private val onItemClick: (ParseObject, List<String>?) -> Unit) :
    RecyclerView.Adapter<CategoryPlacesAdapter.ViewHolder>() {

    private val places = AsyncListDiffer(this, object : DiffUtil.ItemCallback<ParseObject>() {
        override fun areItemsTheSame(oldItem: ParseObject, newItem: ParseObject): Boolean {
            return oldItem.get("objectId") == newItem.get("objectId") // Use placeId for comparison
        }

        override fun areContentsTheSame(oldItem: ParseObject, newItem: ParseObject): Boolean {
            return oldItem.getString("faName") == newItem.getString("faName") &&
                    oldItem.getString("enName") == newItem.getString("enName") &&
                    oldItem.getString("address") == newItem.getString("address")
        }
    })

    private val images = ArrayList<ParseObject>()

    inner class ViewHolder(val binding: ItemCategoryPlacesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ParseObject, images: List<String>?) {
            binding.placeName.text = when (Locale.getDefault().language) {
                "en" -> item.getString("enName")
                "fa" -> item.getString("faName")
                else -> item.getString("enName")
            }
            binding.placeAddressItemCategoryPlaces.text = when (Locale.getDefault().language) {
                "en" -> item.getString("enAddress")
                "fa" -> item.getString("faAddress")
                else -> item.getString("enAddress")
            }
            Timber.i(images.toString())
            if (!images.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(images.firstOrNull())
                    .into(binding.placeImage)
            } else {
                binding.placeImage.setImageResource(R.drawable.image_place_holder)
            }
            itemView.setOnClickListener {
                onItemClick(item,images)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCategoryPlacesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return places.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = places.currentList[position]
        val filteredImage =
            images.find { it.getString("placeId") == place.objectId }?.getList<String>("photosUrl")
        holder.bind(place, filteredImage)
    }

    fun clearData() {
        places.submitList(emptyList())
        images.clear()
        notifyDataSetChanged()
    }

    fun addPlaces(list: List<ParseObject>) {
        places.submitList(list)
    }

    fun addImages(list: List<ParseObject>) {
        images.clear()
        images.addAll(list)
        notifyDataSetChanged()
    }
}