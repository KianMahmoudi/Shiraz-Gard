package com.kianmahmoudi.android.shirazgard.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kianmahmoudi.android.shirazgard.databinding.ItemPlaceBinding
import com.kianmahmoudi.android.shirazgard.fragments.Home.HomeFragmentDirections
import com.parse.ParseObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.ArrayList
import java.util.Locale

class HotelsHomeAdapter() :
    RecyclerView.Adapter<HotelsHomeAdapter.ViewHolder>() {

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<ParseObject>() {
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
                binding.imagePlace.setImageResource(com.denzcoskun.imageslider.R.drawable.default_placeholder)
            }
            itemView.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    if (!images.isNullOrEmpty()) {
                        val action =
                            HomeFragmentDirections.actionHomeFragmentToPlaceDetailsFragment(
                                faName = item.getString("faName") ?: "",
                                enName = item.getString("enName") ?: "",
                                address = item.getString("address") ?: "",
                                description = item.getString("description") ?: "",
                                type = item.getString("type") ?: "",
                                latitude = item.getParseGeoPoint("location")?.latitude?.toFloat()
                                    ?: 0f,
                                longitude = item.getParseGeoPoint("location")?.longitude?.toFloat()
                                    ?: 0f,
                                images = images.toTypedArray()
                            )
                        try {
                            withContext(Dispatchers.Main) {
                                it.findNavController().navigate(action)
                            }
                        } catch (e: Exception) {
                            Timber.e(e.message)
                        }
                    } else {
                        Log.d("HotelAdapter", "No hotel images found for hotel ID")
                    }
                }
            }
        }
    }

    fun submitList(list: List<ParseObject>) {
        differ.submitList(list)
    }

    fun addImages(list: List<ParseObject>) {
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

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = differ.currentList[position]
        val filteredImage =
            images.find { it.getString("placeId") == item.objectId }?.getList<String>("photosUrl")
        holder.bind(item, filteredImage)
    }

}