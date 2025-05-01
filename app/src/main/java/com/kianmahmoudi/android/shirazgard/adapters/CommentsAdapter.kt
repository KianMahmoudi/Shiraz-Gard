package com.kianmahmoudi.android.shirazgard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kianmahmoudi.android.shirazgard.R
import com.kianmahmoudi.android.shirazgard.databinding.ItemCommentBinding
import com.parse.ParseObject
import java.text.SimpleDateFormat
import java.util.*

class CommentsAdapter(private val tag: String) :
    RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<ParseObject>() {
        override fun areItemsTheSame(oldItem: ParseObject, newItem: ParseObject): Boolean {
            return oldItem.objectId == newItem.objectId
        }

        override fun areContentsTheSame(oldItem: ParseObject, newItem: ParseObject): Boolean {
            return oldItem.getString("commentText") == newItem.getString("commentText") &&
                    oldItem.getString("userId") == newItem.getString("userId") &&
                    oldItem.getInt("ratingValue") == newItem.getInt("ratingValue") &&
                    oldItem.createdAt == newItem.createdAt
        }
    }

    private val comments = AsyncListDiffer(this, differCallback)
    private var profileImages: MutableMap<String, String?> = mutableMapOf()

    inner class ViewHolder(val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(comment: ParseObject) {
            val text = comment.getString("commentText") ?: ""
            val date = formatDate(comment.createdAt)
            val rating = comment.getNumber("ratingValue")
            val username = comment.getString("username") ?: ""
            val userId = comment.getString("userId") ?: ""
            val profileUrl = profileImages[userId]

            binding.tvUserId.text = username
            binding.tvTimestamp.text = date
            binding.tvRating.text = rating?.toString() ?: "-"
            binding.tvComment.text = text

            Glide.with(binding.root)
                .load(profileUrl)
                .placeholder(R.drawable.person_24px)
                .error(R.drawable.person_24px)
                .circleCrop()
                .into(binding.profileImageComment)

        }
    }

    fun submitComments(allComments: List<ParseObject>) {
        val finalList = if (tag == "PlaceDetailsFragment") {
            allComments.take(3)
        } else {
            allComments
        }
        comments.submitList(finalList)
    }

    fun submitProfileImages(profiles: Map<String, String?>) {
        val newImages = profiles.filterValues { it != null }
        if (newImages != profileImages) {
            profileImages.clear()
            profileImages.putAll(newImages)
            notifyItemRangeChanged(0, itemCount)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = comments.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(comments.currentList[position])
    }

    private fun formatDate(date: Date?): String {
        if (date == null) return "-"
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return formatter.format(date)
    }
}
