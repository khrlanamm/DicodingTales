package com.khrlanamm.dicodingtales

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khrlanamm.dicodingtales.data.Result
import com.khrlanamm.dicodingtales.data.Repository
import com.khrlanamm.dicodingtales.data.remote.response.ListStoryItem
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: Repository): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _stories = MutableLiveData<Result<List<ListStoryItem>>>()
    val stories: LiveData<Result<List<ListStoryItem>>> = _stories

    fun getAllStories(token: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.getAllStories(token)
            _stories.value = result
            _isLoading.value = false
        }
    }
}