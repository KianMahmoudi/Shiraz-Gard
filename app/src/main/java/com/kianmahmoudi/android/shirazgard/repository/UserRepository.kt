package com.kianmahmoudi.android.shirazgard.repository

import android.net.Uri
import com.parse.ParseUser

interface UserRepository {
    fun registerUser(userName: String, password: String, callback: (ParseUser?, String?) -> Unit)
    fun loginUser(userName: String, password: String, callback: (ParseUser?, String?) -> Unit)
    fun changePassword(newPassword: String, callback: (Boolean, String?) -> Unit)
    fun uploadProfileImage(imageUri: Uri, callback: (Boolean, String?) -> Unit)
    fun getProfileImageUrl(callback: (String?) -> Unit)
    fun updateUsername(newUsername: String, callback: (Boolean, String?) -> Unit)
    fun deleteProfileImage(callback: (Boolean, String?) -> Unit)
    fun deleteAccount(callback: (Boolean, String?) -> Unit)
    fun logout(callback: (Boolean, String?) -> Unit)
    fun isCurrentPasswordCorrect(password: String, callback: (Boolean, String?) -> Unit)
}