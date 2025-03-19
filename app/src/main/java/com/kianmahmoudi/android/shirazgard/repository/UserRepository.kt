package com.kianmahmoudi.android.shirazgard.repository

import android.net.Uri
import com.parse.ParseUser

interface UserRepository {
    suspend fun registerUser(userName: String, password: String): ParseUser
    suspend fun loginUser(userName: String, password: String): ParseUser
    suspend fun changePassword(newPassword: String): Boolean
    suspend fun uploadProfileImage(imageUri: Uri): Boolean
    suspend fun getProfileImageUrl(): String
    suspend fun updateUsername(newUsername: String): Boolean
    suspend fun deleteProfileImage(): Boolean
    suspend fun deleteAccount(): Boolean
    fun logout()
    suspend fun isCurrentPasswordCorrect(password: String): Boolean
}