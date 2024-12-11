package com.khrlanamm.dicodingtales.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khrlanamm.dicodingtales.data.Result
import com.khrlanamm.dicodingtales.data.Repository
import com.khrlanamm.dicodingtales.data.remote.response.StoryEntity
import kotlinx.coroutines.launch

class MapsViewModel(private val repository: Repository): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _stories = MutableLiveData<Result<List<StoryEntity>>>()
    val stories: LiveData<Result<List<StoryEntity>>> = _stories

    fun getAllStoriesWithMap(token: String, location: Int = 1) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.getStoryWithMap(token, location)
            _stories.value = result
            _isLoading.value = false
        }
    }
}