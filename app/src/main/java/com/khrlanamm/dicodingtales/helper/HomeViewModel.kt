package com.khrlanamm.dicodingtales.helper

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.khrlanamm.dicodingtales.data.Repository
import com.khrlanamm.dicodingtales.data.remote.response.StoryEntity

class HomeViewModel(private val repository: Repository) : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun stories(token: String): LiveData<PagingData<StoryEntity>> {
        _isLoading.value = true
        return repository.getStoriesPagingWithMediator(token)
            .cachedIn(viewModelScope)
            .also {
                it.observeForever { _ ->
                    _isLoading.value = false
                }
            }
    }
}