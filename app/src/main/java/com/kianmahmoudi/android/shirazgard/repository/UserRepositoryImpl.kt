package com.kianmahmoudi.android.shirazgard.repository

import android.content.Context
import android.net.Uri
import com.parse.ParseCloud
import com.parse.ParseFile
import com.parse.ParseQuery
import com.parse.ParseUser
import com.parse.SaveCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserRepository {

    override fun registerUser(
        userName: String,
        password: String,
        callback: (ParseUser?, String?) -> Unit
    ) {
        val user = ParseUser()
        user.username = userName
        user.setPassword(password)
        user.signUpInBackground { e ->
            if (e == null) {
                callback(user, null)
            } else {
                callback(null, e.message)
            }
        }
    }

    override fun loginUser(
        userName: String,
        password: String,
        callback: (ParseUser?, String?) -> Unit
    ) {
        ParseUser.logInInBackground(userName, password) { user, e ->
            if (user != null) {
                callback(user, null)
            } else {
                callback(null, e?.message)
            }
        }
    }

    override fun changePassword(
        password: String,
        callback: (Boolean, String?) -> Unit
    ) {
        val user = ParseUser.getCurrentUser()
        user.setPassword(password)
        user.saveInBackground { e ->
            if (e == null) {
                callback(true, null)
            } else {
                callback(false, e.message)
            }
        }
    }

    override fun uploadProfileImage(imageUri: Uri, callback: (Boolean, String?) -> Unit) {
        val user = ParseUser.getCurrentUser() ?: run {
            callback(false, "User not logged in")
            return
        }

        try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bytes = inputStream?.readBytes() ?: run {
                callback(false, "Failed to read image")
                return
            }

            val file = ParseFile("profile_${user.objectId}.jpg", bytes)

            file.saveInBackground(SaveCallback { e ->
                if (e != null) {
                    callback(false, e.message)
                    return@SaveCallback
                }

                user.put("profileImage", file)
                user.saveInBackground { saveError ->
                    callback(saveError == null, saveError?.message)
                }
            })
        } catch (e: Exception) {
            callback(false, e.message)
        }
    }

    override fun getProfileImageUrl(callback: (String?) -> Unit) {
        val user = ParseUser.getCurrentUser()
        callback(user?.getParseFile("profileImage")?.url)
    }


    override fun updateUsername(newUsername: String, callback: (Boolean, String?) -> Unit) {
        val user = ParseUser.getCurrentUser()
        user?.username = newUsername
        user?.saveInBackground { e ->
            callback(e == null, e?.message)
        }
    }

    override fun deleteProfileImage(callback: (Boolean, String?) -> Unit) {
        val user = ParseUser.getCurrentUser()
        user?.remove("profileImage")
        user?.saveInBackground { e ->
            callback(e == null, e?.message)
        }
    }


    override fun deleteAccount(callback: (Boolean, String?) -> Unit) {
        val user = ParseUser.getCurrentUser()
        user?.deleteInBackground { e ->
            if (e == null) {
                callback(true, null)
                ParseUser.logOut()
            } else {
                callback(false, e.message)
            }
        }
    }


    override fun logout(callback: (Boolean, String?) -> Unit) {
        ParseUser.logOutInBackground { e ->
            if (e == null) {
                callback(true, null)
            } else {
                callback(false, e.message)
            }
        }
    }


    override fun isCurrentPasswordCorrect(
        password: String,
        callback: (Boolean, String?) -> Unit
    ) {
        val params =
            hashMapOf("username" to ParseUser.getCurrentUser().username, "password" to password)
        ParseCloud.callFunctionInBackground<HashMap<String, Any>>(
            "verifyPassword",
            params
        ) { result, e ->
            if (e == null) {
                val response = result as HashMap<String, Any>
                if (response["success"] as Boolean) {
                    callback(true, null)
                } else {
                    callback(false, response["error"] as String?)
                }
            } else {
                callback(false, e.message)
            }
        }
    }

    override fun getProfileImage(userId: String, callback: (String?) -> Unit) {
        val params = hashMapOf<String, Any>("userId" to userId)
        ParseCloud.callFunctionInBackground<Map<String, Any>>("getProfileImage", params) { result, e ->
            if (e == null && result != null) {
                val imageUrl = result["imageUrl"] as? String
                callback(imageUrl)
            } else {
                Timber.e(e, "Error calling getProfileImage for user $userId")
                callback(null)
            }
        }
    }

}