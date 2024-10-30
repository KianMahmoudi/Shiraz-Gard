package com.kianmahmoudi.android.shirazgard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.parse.ParseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor() : ViewModel() {

    private val _registerUser = MutableLiveData<ParseUser>()
    val registerUser: LiveData<ParseUser> = _registerUser

    private val _registerError = MutableLiveData<String>()
    val registerError: LiveData<String> = _registerError

    private val _loginUser = MutableLiveData<ParseUser>()
    val loginUser: LiveData<ParseUser> = _loginUser

    private val _loginError = MutableLiveData<String>()
    val loginError: LiveData<String> = _loginError

    fun registerUser(userName: String, password: String) {
        val user = ParseUser()
        user.username = userName
        user.setPassword(password)

        user.signUpInBackground { e ->
            if (e == null) {
                _registerUser.postValue(user)
            } else {
                _registerError.postValue(e.message)
            }
        }
    }

    fun loginUser(userName: String, password: String) {
        ParseUser.logInInBackground(userName, password) { user, e ->
            if (user != null) {
                _loginUser.postValue(user)
            } else {
                _loginError.postValue(e.message)
            }
        }
    }

}