package com.kianmahmoudi.android.shirazgard.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parse.ParseUser
import com.kianmahmoudi.android.shirazgard.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.kianmahmoudi.android.shirazgard.data.Result
import kotlinx.coroutines.delay

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _registerState = MutableLiveData<Result<ParseUser>?>()
    val registerState: MutableLiveData<Result<ParseUser>?> = _registerState

    private val _loginState = MutableLiveData<Result<ParseUser>?>()
    val loginState: MutableLiveData<Result<ParseUser>?> = _loginState

    private val _logoutState = MutableLiveData<Result<Unit>>()
    val logoutState: LiveData<Result<Unit>> = _logoutState

    private val _deleteAccountState = MutableLiveData<Result<Boolean>>()
    val deleteAccountState: LiveData<Result<Boolean>> = _deleteAccountState

    private val _passwordChangeState = MutableLiveData<Result<Boolean>>()
    val passwordChangeState: LiveData<Result<Boolean>> = _passwordChangeState

    private val _passwordVerificationState = MutableLiveData<Result<Boolean>>()
    val passwordVerificationState: LiveData<Result<Boolean>> = _passwordVerificationState

    private val _profileImageState = MutableLiveData<Result<String>?>()
    val profileImageState: MutableLiveData<Result<String>?> = _profileImageState

    private val _usernameState = MutableLiveData<Result<String>?>()
    val usernameState: MutableLiveData<Result<String>?> = _usernameState

    private val _profileImageDeletionState = MutableLiveData<Result<Boolean>?>()
    val profileImageDeletionState: MutableLiveData<Result<Boolean>?> = _profileImageDeletionState

    private fun resetLoginAndRegisterState(){
        _registerState.value = null
        _loginState.value = null
    }

    fun registerUser(username: String, password: String) {
        viewModelScope.launch {
            _registerState.value = Result.Loading
            try {
                val user = userRepository.registerUser(username, password)
                _registerState.postValue(Result.Success(user))
            } catch (e: Exception) {
                _registerState.postValue(Result.Error(e.parseErrorMessage()))
            }
        }
    }

    fun loginUser(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = Result.Loading
            try {
                val user = userRepository.loginUser(username, password)
                _loginState.postValue(Result.Success(user))
            } catch (e: Exception) {
                _loginState.postValue(Result.Error(e.parseErrorMessage()))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _logoutState.value = Result.Loading
            try {
                userRepository.logout()
                _logoutState.postValue(Result.Success(Unit))
                resetLoginAndRegisterState()
            } catch (e: Exception) {
                _logoutState.postValue(Result.Error(e.parseErrorMessage()))
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _deleteAccountState.value = Result.Loading
            try {
                val success = userRepository.deleteAccount()
                _deleteAccountState.postValue(Result.Success(success))
                resetLoginAndRegisterState()
            } catch (e: Exception) {
                _deleteAccountState.postValue(Result.Error(e.parseErrorMessage()))
            }
        }
    }

    fun changePassword(newPassword: String) {
        viewModelScope.launch {
            _passwordChangeState.value = Result.Loading
            try {
                val success = userRepository.changePassword(newPassword)
                _passwordChangeState.postValue(Result.Success(success))
            } catch (e: Exception) {
                _passwordChangeState.postValue(Result.Error(e.parseErrorMessage()))
            }
        }
    }

    fun verifyCurrentPassword(password: String) {
        viewModelScope.launch {
            _passwordVerificationState.value = Result.Loading
            try {
                val isValid = userRepository.isCurrentPasswordCorrect(password)
                _passwordVerificationState.postValue(Result.Success(isValid))
            } catch (e: Exception) {
                _passwordVerificationState.postValue(Result.Error(e.parseErrorMessage()))
            }
        }
    }

    fun uploadProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            _profileImageState.value = Result.Loading
            try {
                userRepository.uploadProfileImage(imageUri)
                val url = userRepository.getProfileImageUrl()
                _profileImageState.postValue(Result.Success(url))
            } catch (e: Exception) {
                _profileImageState.postValue(Result.Error(e.parseErrorMessage()))
            }
        }
    }

    fun updateUsername(newUsername: String) {
        viewModelScope.launch {
            _usernameState.value = Result.Loading
            try {
                userRepository.updateUsername(newUsername)

                _usernameState.postValue(Result.Success(newUsername))
                _usernameState.postValue(null)

            } catch (e: Exception) {
                _usernameState.postValue(Result.Error(e.parseErrorMessage()))
            }
        }
    }

    fun deleteProfileImage() {
        viewModelScope.launch {
            _profileImageDeletionState.value = Result.Loading
            try {
                val success = userRepository.deleteProfileImage()
                _profileImageDeletionState.postValue(Result.Success(success))
                _profileImageState.value = null
            } catch (e: Exception) {
                _profileImageDeletionState.postValue(Result.Error(e.parseErrorMessage()))
            }
        }
    }

    fun fetchProfileImageUrl() {
        viewModelScope.launch {
            _profileImageState.value = Result.Loading
            try {
                val url = userRepository.getProfileImageUrl()
                _profileImageState.postValue(Result.Success(url))
            } catch (e: Exception) {
                _profileImageState.postValue(Result.Error(e.parseErrorMessage()))
            }
        }
    }

    private fun Exception.parseErrorMessage(): String {
        return message?.substringAfterLast(":")?.trim()
            ?: "خطای ناشناخته رخ داده است"
    }

    fun resetProfileImgDeletionState(){
        _profileImageDeletionState.value = null
    }

}