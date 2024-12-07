package com.kianmahmoudi.android.shirazgard.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.databinding.ItemPlaceHomeBinding
import com.kianmahmoudi.android.shirazgard.repository.HotelRepository
import com.parse.ParseObject
import com.parse.ParseQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.log

class HotelsHomeAdapter(private val hotelRepository: HotelRepository) :
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

    inner class ViewHolder(val binding: ItemPlaceHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ParseObject) {

            binding.textHotelName.text = when (Locale.getDefault().language) {
                "en" -> item.getString("enName")
                "fa" -> item.getString("faName")
                else -> item.getString("enName")
            }

            CoroutineScope(Dispatchers.IO).launch {
                val photoUrls = hotelRepository.getHotelImages(item.objectId)
                withContext(Dispatchers.Main) {
                    if (photoUrls != null && photoUrls.isNotEmpty()) {
                        Glide.with(binding.root.context).load(photoUrls[0]).into(binding.imageHotel)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPlaceHomeBinding.inflate(
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
        holder.bind(item)
    }

}