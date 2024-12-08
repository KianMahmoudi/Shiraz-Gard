package com.kianmahmoudi.android.shirazgard.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kianmahmoudi.android.shirazgard.databinding.ItemPlaceBinding
import com.kianmahmoudi.android.shirazgard.repository.HomeRepository
import com.parse.ParseObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class RestaurantsHomeAdapter(private val homeRepository: HomeRepository) : RecyclerView.Adapter<RestaurantsHomeAdapter.ViewHolder>() {

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<ParseObject>() {
        override fun areItemsTheSame(oldItem: ParseObject, newItem: ParseObject): Boolean {
            return oldItem.objectId == newItem.objectId
        }

        override fun areContentsTheSame(oldItem: ParseObject, newItem: ParseObject): Boolean {
            return oldItem.getString("faName") == newItem.getString("faName") &&
                    oldItem.getString("address") == newItem.getString("address")
        }
    })

    inner class ViewHolder(val binding:ItemPlaceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item : ParseObject) {
            binding.textPlaceName.text = when (Locale.getDefault().language) {
                "en" -> item.getString("enName")
                "fa" -> item.getString("faName")
                else -> item.getString("enName")
            }
            CoroutineScope(Dispatchers.IO).launch {
                val photoUrls = homeRepository.getPlaceImages(item.objectId)
                withContext(Dispatchers.Main) {
                    if (photoUrls != null && photoUrls.isNotEmpty()) {
                        Glide.with(binding.root.context).load(photoUrls[0]).into(binding.imagePlace)
                    } else {
                        Log.d("HotelAdapter", "No hotel images found for hotel ID: ${item.objectId}")
                    }
                }
            }
        }
    }

    fun submitList(list: List<ParseObject>) {
        differ.submitList(list)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RestaurantsHomeAdapter.ViewHolder {
        return ViewHolder(
            ItemPlaceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
     return differ.currentList.size
    }


}