package com.kianmahmoudi.android.shirazgard.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kianmahmoudi.android.shirazgard.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.kianmahmoudi.android.shirazgard.data.UiState
import com.parse.ParseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {


    private val _registerState = MutableLiveData<UiState<ParseUser>>(UiState.Idle)
    val registerState: LiveData<UiState<ParseUser>> = _registerState

    private val _loginState = MutableLiveData<UiState<ParseUser>>(UiState.Idle)
    val loginState: LiveData<UiState<ParseUser>> = _loginState

    private val _logoutState = MutableLiveData<UiState<Unit>>(UiState.Idle)
    val logoutState: LiveData<UiState<Unit>> = _logoutState

    private val _deleteAccountState = MutableLiveData<UiState<Boolean>>(UiState.Idle)
    val deleteAccountState: LiveData<UiState<Boolean>> = _deleteAccountState

    private val _passwordChangeState = MutableLiveData<UiState<Boolean>>(UiState.Idle)
    val passwordChangeState: LiveData<UiState<Boolean>> = _passwordChangeState

    private val _passwordVerificationState = MutableLiveData<UiState<Boolean>>(UiState.Idle)
    val passwordVerificationState: LiveData<UiState<Boolean>> = _passwordVerificationState

    private val _profileImageState = MutableLiveData<UiState<String>>(UiState.Idle)
    val profileImageState: LiveData<UiState<String>> = _profileImageState

    private val _usernameState = MutableLiveData<UiState<String>>(UiState.Idle)
    val usernameState: LiveData<UiState<String>> = _usernameState

    private val _profileImageDeletionState = MutableLiveData<UiState<Boolean>>(UiState.Idle)
    val profileImageDeletionState: LiveData<UiState<Boolean>> = _profileImageDeletionState

    private val _usersProfileImages = MutableLiveData<UiState<Map<String, String?>>>(UiState.Idle)
    val usersProfileImages: LiveData<UiState<Map<String, String?>>> = _usersProfileImages

    fun registerUser(username: String, password: String) {
        if (_registerState.value is UiState.Loading) return
        _registerState.postValue(UiState.Loading)

        userRepository.registerUser(username, password) { user, error ->
            if (error == null) {
                _registerState.value = UiState.Success(user!!)
            } else {
                _registerState.value = UiState.Error(error)
            }
        }
    }

    fun loginUser(username: String, password: String) {
        if (_loginState.value is UiState.Loading) return
        _loginState.postValue(UiState.Loading)

        userRepository.loginUser(username, password) { user, error ->
            if (error == null) {
                _loginState.postValue(UiState.Success(user!!))
            } else {
                _loginState.postValue(UiState.Error(error))
            }
        }
    }

    fun logout() {
        if (_logoutState.value is UiState.Loading) return
        _logoutState.postValue(UiState.Loading)

        userRepository.logout { success, error ->
            if (success) {
                _logoutState.postValue(UiState.Success(Unit))
            } else {
                _logoutState.postValue(error?.let { UiState.Error(it) })
            }
        }

    }

    fun updateUsername(newUsername: String) {

        if (_usernameState.value is UiState.Loading) return
        _usernameState.postValue(UiState.Loading)

        userRepository.updateUsername(newUsername) { success, error ->
            if (success) {
                _usernameState.postValue(UiState.Success(newUsername))
            } else {
                _usernameState.postValue(error?.let { UiState.Error(it) })
            }
        }
    }

    fun uploadProfileImage(imageUri: Uri) {
        if (_profileImageState.value is UiState.Loading) return
        _profileImageState.postValue(UiState.Loading)

        userRepository.uploadProfileImage(imageUri) { success, error ->
            if (error == null && success) {
                userRepository.getProfileImageUrl() { url ->
                    if (url != null) {
                        _profileImageState.postValue(UiState.Success(url))
                    } else {
                        _profileImageState.postValue(UiState.Error("Failed to get profile image URL"))
                    }
                }
            } else {
                _profileImageState.postValue(UiState.Error(error ?: "Image upload error"))
            }
        }
    }

    fun deleteProfileImage() {
        if (_profileImageDeletionState.value is UiState.Loading) return
        _profileImageDeletionState.postValue(UiState.Loading)

        userRepository.deleteProfileImage() { success, error ->
            if (error == null && success) {
                _profileImageDeletionState.postValue(UiState.Success(true))
                _profileImageState.postValue(UiState.Success(""))
            } else {
                _profileImageDeletionState.postValue(UiState.Error(error ?: "Error deleting image"))
            }
        }
    }

    fun fetchProfileImageUrl() {
        if (_profileImageState.value is UiState.Loading) return
        _profileImageState.postValue(UiState.Loading)

        userRepository.getProfileImageUrl() { url ->
            if (url != null) {
                _profileImageState.postValue(UiState.Success(url))
            } else {
                _profileImageState.postValue(UiState.Error("Error fetching profile image URL"))
            }
        }
    }

    fun changePassword(newPassword: String) {
        if (_passwordChangeState.value is UiState.Loading) return
        _passwordChangeState.postValue(UiState.Loading)

        userRepository.changePassword(newPassword) { success, error ->
            if (error == null && success) {
                _passwordChangeState.postValue(UiState.Success(true))
            } else {
                _passwordChangeState.postValue(UiState.Error(error ?: "Error changing password"))
            }
        }
    }

    fun verifyCurrentPassword(password: String) {
        if (_passwordVerificationState.value is UiState.Loading) return
        _passwordVerificationState.postValue(UiState.Loading)

        userRepository.isCurrentPasswordCorrect(password) { success, error ->
            if (error == null && success) {
                _passwordVerificationState.postValue(UiState.Success(true))
            } else {
                _passwordVerificationState.postValue(
                    UiState.Error(error ?: "Password verification failed")
                )
            }
        }
    }

    fun deleteAccount() {
        if (_deleteAccountState.value is UiState.Loading) return
        _deleteAccountState.postValue(UiState.Loading)

        userRepository.deleteAccount() { success, error ->
            if (error == null && success) {
                _deleteAccountState.postValue(UiState.Success(true))
            } else {
                _deleteAccountState.postValue(UiState.Error(error ?: "Account deletion failed"))
            }
        }
    }

    fun getProfileImage(userId: String) {
        userRepository.getProfileImage(userId) { url ->
            val currentMap = (_usersProfileImages.value as? UiState.Success)?.data?.toMutableMap() ?: mutableMapOf()
            currentMap[userId] = url
            val previousState = _usersProfileImages.value
            if (previousState !is UiState.Success || previousState.data[userId] != url) {
                _usersProfileImages.postValue(UiState.Success(currentMap))
            }

        }
    }

    fun resetUsernameState() {
        _usernameState.postValue(UiState.Idle)
    }

    fun resetRegisterState() {
        _registerState.postValue(UiState.Idle)
    }

    fun resetLoginState() {
        _loginState.postValue(UiState.Idle)
    }

    fun resetLogoutState() {
        _logoutState.postValue(UiState.Idle)
    }

    fun resetDeleteAccountState() {
        _deleteAccountState.postValue(UiState.Idle)
    }

    fun resetPasswordState() {
        _passwordChangeState.postValue(UiState.Idle)
        _passwordVerificationState.postValue(UiState.Idle)
    }

    fun resetProfileImageState() {
        _profileImageState.postValue(UiState.Idle)
    }

    fun resetProfileImageDeletionState() {
        _profileImageDeletionState.postValue(UiState.Idle)
    }

}
