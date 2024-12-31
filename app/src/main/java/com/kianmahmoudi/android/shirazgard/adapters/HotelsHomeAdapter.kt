package com.kianmahmoudi.android.shirazgard.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kianmahmoudi.android.shirazgard.activities.ItemPlaceDetailsActivity
import com.kianmahmoudi.android.shirazgard.databinding.ItemPlaceBinding
import com.parse.ParseObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
                val intent = Intent(itemView.context, ItemPlaceDetailsActivity::class.java)
                CoroutineScope(Dispatchers.IO).launch {
                    if (!images.isNullOrEmpty()) {
                        intent.putStringArrayListExtra("images", images as ArrayList<String>)
                        intent.putExtra("faName", item.getString("faName"))
                        intent.putExtra("enName", item.getString("enName"))
                        intent.putExtra("address", item.getString("address"))
                        intent.putExtra("description", item.getString("description"))
                        intent.putExtra("type", item.getString("type"))
                        itemView.context.startActivity(intent)
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