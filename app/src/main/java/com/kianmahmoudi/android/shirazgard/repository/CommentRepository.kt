package com.kianmahmoudi.android.shirazgard.repository

import androidx.lifecycle.LiveData
import com.parse.ParseObject

interface CommentRepository {
    fun addComment(
        placeId: String,
        text: String,
        rating: Int,
        callback: (Boolean, String?) -> Unit
    )

    fun getAllComments(
        placeId: String, callback: (List<ParseObject>, String?) -> Unit
    )
}