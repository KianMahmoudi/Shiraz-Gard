package com.kianmahmoudi.android.shirazgard.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.parse.ParseUser
import com.kianmahmoudi.android.shirazgard.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    // Existing LiveData
    private val _registerUser = MutableLiveData<ParseUser?>()
    val registerUser: LiveData<ParseUser?> = _registerUser

    private val _registerError = MutableLiveData<String?>()
    val registerError: LiveData<String?> = _registerError

    private val _loginUser = MutableLiveData<ParseUser?>()
    val loginUser: LiveData<ParseUser?> = _loginUser

    private val _loginError = MutableLiveData<String?>()
    val loginError: LiveData<String?> = _loginError

    private val _changePasswordResult = MutableLiveData<Boolean>()
    val changePasswordResult: LiveData<Boolean> = _changePasswordResult

    private val _changePasswordError = MutableLiveData<String?>()
    val changePasswordError: LiveData<String?> = _changePasswordError

    private val _isCurrentPasswordCorrect = MutableLiveData<Boolean>()
    val isCurrentPasswordCorrect: LiveData<Boolean> = _isCurrentPasswordCorrect

    private val _isCurrentPasswordError = MutableLiveData<String?>()
    val isCurrentPasswordError: LiveData<String?> = _isCurrentPasswordError

    private val _deleteAccountResult = MutableLiveData<Boolean>()
    val deleteAccountResult: LiveData<Boolean> = _deleteAccountResult

    private val _deleteAccountError = MutableLiveData<String?>()
    val deleteAccountError: LiveData<String?> = _deleteAccountError

    // New LiveData for Profile Management
    private val _uploadProfileImageResult = MutableLiveData<Boolean>()
    val uploadProfileImageResult: LiveData<Boolean> = _uploadProfileImageResult

    private val _uploadProfileImageError = MutableLiveData<String?>()
    val uploadProfileImageError: LiveData<String?> = _uploadProfileImageError

    private val _profileImageUrl = MutableLiveData<String?>()
    val profileImageUrl: LiveData<String?> = _profileImageUrl

    private val _updateUsernameResult = MutableLiveData<Boolean>()
    val updateUsernameResult: LiveData<Boolean> = _updateUsernameResult

    private val _updateUsernameError = MutableLiveData<String?>()
    val updateUsernameError: LiveData<String?> = _updateUsernameError

    private val _deleteProfileImageResult = MutableLiveData<Boolean>()
    val deleteProfileImageResult: LiveData<Boolean> = _deleteProfileImageResult

    private val _deleteProfileImageError = MutableLiveData<String?>()
    val deleteProfileImageError: LiveData<String?> = _deleteProfileImageError

    // Existing Methods
    fun registerUser(userName: String, password: String) {
        userRepository.registerUser(userName, password) { user, error ->
            _registerUser.postValue(user)
            _registerError.postValue(error)
        }
    }

    fun loginUser(userName: String, password: String) {
        userRepository.loginUser(userName, password) { user, error ->
            _loginUser.postValue(user)
            _loginError.postValue(error)
        }
    }

    fun changePassword(newPassword: String) {
        userRepository.changePassword(newPassword) { success, error ->
            _changePasswordResult.postValue(success)
            _changePasswordError.postValue(error)
        }
    }

    fun isCurrentPasswordCorrect(password: String) {
        userRepository.isCurrentPasswordCorrect(password) { success, error ->
            _isCurrentPasswordCorrect.postValue(success)
            _isCurrentPasswordError.postValue(error)
        }
    }

    fun logout() {
        userRepository.logout()
        resetUserData()
    }

    fun deleteAccount() {
        userRepository.deleteAccount { success, error ->
            if (success) {
                _deleteAccountResult.postValue(true)
                resetUserData()
            } else {
                _deleteAccountError.postValue(error)
            }
        }
    }

    fun uploadProfileImage(imageUri: Uri) {
        userRepository.uploadProfileImage(imageUri) { success, error ->
            if (success) {
                _uploadProfileImageResult.postValue(true)
                fetchProfileImageUrl()
            } else {
                _uploadProfileImageError.postValue(error)
            }
        }
    }

    fun fetchProfileImageUrl() {
        userRepository.getProfileImageUrl { url ->
            _profileImageUrl.postValue(url)
        }
    }

    fun updateUsername(newUsername: String) {
        userRepository.updateUsername(newUsername) { success, error ->
            if (success) {
                _updateUsernameResult.postValue(true)
            } else {
                _updateUsernameError.postValue(error)
            }
        }
    }

    fun resetUserNameAndProfileResults() {
        _updateUsernameResult.value = false
        _uploadProfileImageResult.value = false
        _deleteProfileImageResult.value = false
    }

    fun deleteProfileImage() {
        userRepository.deleteProfileImage { success, error ->
            if (success) {
                _deleteProfileImageResult.postValue(true)
                fetchProfileImageUrl()
            } else {
                _deleteProfileImageError.postValue(error)
            }
        }
    }

    private fun resetUserData() {
        _loginUser.postValue(null)
        _registerUser.postValue(null)
        _loginError.postValue(null)
        _registerError.postValue(null)
        _profileImageUrl.postValue(null)
    }

}