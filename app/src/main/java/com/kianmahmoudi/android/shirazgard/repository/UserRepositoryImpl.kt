package com.kianmahmoudi.android.shirazgard.repository

import android.content.Context
import android.net.Uri
import com.parse.Parse
import com.parse.ParseFile
import com.parse.ParseUser
import com.parse.SaveCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserRepository {

    override suspend fun registerUser(userName: String, password: String): ParseUser {
        return ParseUser().apply {
            username = userName
            setPassword(password)
            signUp()
        }
    }

    override suspend fun loginUser(userName: String, password: String): ParseUser {
        return ParseUser.logIn(userName, password)
    }

    override suspend fun changePassword(newPassword: String): Boolean {
        ParseUser.getCurrentUser().apply {
            setPassword(newPassword)
            save()
        }
        return true
    }

    override suspend fun uploadProfileImage(imageUri: Uri): Boolean {
        val user = ParseUser.getCurrentUser()
        val bytes = context.contentResolver.openInputStream(imageUri)?.readBytes()
            ?: throw Exception("Failed to read image")

        ParseFile("profile_${user.objectId}.jpg", bytes).apply {
            save()
            user.put("profileImage", this)
            user.save()
        }
        return true
    }

    override suspend fun getProfileImageUrl(): String {
        return ParseUser.getCurrentUser().getParseFile("profileImage")?.url
            ?: throw Exception("No profile image found")
    }

    override suspend fun updateUsername(newUsername: String): Boolean {
        return try {
            val user = ParseUser.getCurrentUser()
            user.username = newUsername

            // ذخیره تغییرات با callback
            user.saveInBackground { e ->
                if (e == null) {
                    // دریافت داده‌های تازه بعد از ذخیره
                    user.fetchInBackground<ParseUser>()
                }
            }// منتظر پایان عملیات می‌ماند

            true
        } catch (e: Exception) {
            throw Exception("خطا در به‌روزرسانی نام کاربری: ${e.message}")
        }
    }

    override suspend fun deleteProfileImage(): Boolean {
        ParseUser.getCurrentUser().apply {
            remove("profileImage")
            save()
        }
        return true
    }

    override fun logout() {
        ParseUser.logOutInBackground()
    }

    override suspend fun deleteAccount(): Boolean {
        return try {
            ParseUser.getCurrentUser().deleteInBackground()
            logout()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun isCurrentPasswordCorrect(password: String): Boolean {
        return try {
            val user = ParseUser.getCurrentUser()
            ParseUser.logIn(user.username, password) != null
        } catch (e: Exception) {
            throw Exception("رمز عبور فعلی نامعتبر است")
        }
    }

}