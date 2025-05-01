package com.kianmahmoudi.android.shirazgard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kianmahmoudi.android.shirazgard.data.UiState
import com.kianmahmoudi.android.shirazgard.repository.CommentRepository
import com.parse.ParseObject
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val repository: CommentRepository
) : ViewModel() {

    private val _addComment = MutableLiveData<UiState<Boolean>>(UiState.Idle)
    val addComment: LiveData<UiState<Boolean>> = _addComment

    private val _comments = MutableLiveData<UiState<List<ParseObject>>>(UiState.Idle)
    val comments: LiveData<UiState<List<ParseObject>>> = _comments

    fun addComment(
        placeId: String,
        text: String,
        rating: Int
    ) {
        _addComment.postValue(UiState.Loading)
        repository.addComment(placeId, text, rating) { success, error ->
            if (success) {
                _addComment.postValue(UiState.Success(true))
                getComments(placeId)
            } else {
                _addComment.postValue(error?.let { UiState.Error(it) })
            }
        }
    }

    fun getComments(placeId:String){
        _comments.postValue(UiState.Loading)
        repository.getAllComments(placeId){comments,error->
            if (error == null){
                _comments.postValue(UiState.Success(comments))
                Timber.d("comments count = ${comments}")

            }else{
                _comments.postValue(UiState.Error(error))
                Timber.d("comments count = ${error}")

            }
        }
    }

    fun resetAddComment(){
        _addComment.postValue(UiState.Idle)
    }

}