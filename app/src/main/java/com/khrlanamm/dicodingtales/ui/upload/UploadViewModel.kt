package com.khrlanamm.dicodingtales.ui.upload

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khrlanamm.dicodingtales.data.Result
import com.khrlanamm.dicodingtales.data.Repository
import com.khrlanamm.dicodingtales.data.remote.response.UploadResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploadViewModel(private val repository: Repository): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    var isLoading: MutableLiveData<Boolean> = _isLoading

    private val _uploadResult = MutableLiveData<Result<UploadResponse>>()
    var uploadResult: MutableLiveData<Result<UploadResponse>> = _uploadResult

    fun uploadStory(
        token: String,
        description: RequestBody,
        photo: MultipartBody.Part,
        lat: RequestBody?,
        lon: RequestBody?
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = repository.upload(token, description, photo, lat, lon)
            _uploadResult.value = result
            _isLoading.value = false
        }
    }
}