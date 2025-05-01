package com.kianmahmoudi.android.shirazgard.repository

import androidx.lifecycle.LiveData
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser

class CommentRepositoryImpl : CommentRepository {
    override fun addComment(
        placeId: String,
        text: String,
        rating: Int,
        callback: (Boolean, String?) -> Unit
    ) {
        val comment = ParseObject("comment")
        val user = ParseUser.getCurrentUser()
        if (user == null) {
            callback(false, "User not logged in")
            return
        }
        comment.put("commentText", text)
        comment.put("userId", ParseUser.getCurrentUser().objectId)
        comment.put("placeId", placeId)
        comment.put("username", ParseUser.getCurrentUser().username)
        comment.put("ratingValue", rating)
        comment.saveInBackground { e ->
            if (e == null) {
                callback(true, null)
            } else {
                callback(false, e.message)
            }
        }
    }

    override fun getAllComments(
        placeId: String,
        callback: (List<ParseObject>, String?) -> Unit
    ) {
        val comments = ParseQuery.getQuery<ParseObject>("comment")
        comments.whereEqualTo("placeId", placeId)
        comments.orderByDescending("createdAt")
        comments.findInBackground { comments, e ->
            if (e == null) {
                callback(comments, null)
            } else {
                callback(emptyList(), e.message)
            }
        }
    }

}