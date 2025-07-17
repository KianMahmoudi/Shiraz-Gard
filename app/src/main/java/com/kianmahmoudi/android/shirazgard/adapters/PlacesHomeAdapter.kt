package com.kianmahmoudi.android.shirazgard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.databinding.ItemPlaceBinding
import com.parse.ParseObject
import java.util.Locale

class PlacesHomeAdapter(
    private val onItemClick: (ParseObject, List<String>?) -> Unit
) : RecyclerView.Adapter<PlacesHomeAdapter.ViewHolder>() {

    private val places = AsyncListDiffer(this, object : DiffUtil.ItemCallback<ParseObject>() {
        override fun areItemsTheSame(oldItem: ParseObject, newItem: ParseObject): Boolean {
            return oldItem.objectId == newItem.objectId
        }

        override fun areContentsTheSame(oldItem: ParseObject, newItem: ParseObject): Boolean {
            return oldItem.getString("faName") == newItem.getString("faName") &&
                    oldItem.getString("address") == newItem.getString("address")
        }
    })

    private val images = ArrayList<ParseObject>()

    inner class ViewHolder(val binding: ItemPlaceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ParseObject, images: List<String>?) {
            binding.textPlaceName.text = when (Locale.getDefault().language) {
                "en" -> item.getString("enName")
                "fa" -> item.getString("faName")
                else -> item.getString("enName")
            }

            if (!images.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(images.firstOrNull())
                    .into(binding.imagePlace)
            } else {
                binding.imagePlace.setImageResource(R.drawable.image_place_holder)
            }
            itemView.setOnClickListener {
                onItemClick(item, images)
            }
        }
    }

    fun addPlaces(list: List<ParseObject>) {
        places.submitList(list)
    }

    fun addImages(list: List<ParseObject>) {
        images.clear()
        images.addAll(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPlaceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = 3

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = places.currentList[position]
        val filteredImage =
            images.find { it.getString("placeId") == item.objectId }?.getList<String>("photosUrl")
        holder.bind(item, filteredImage)
    }
}