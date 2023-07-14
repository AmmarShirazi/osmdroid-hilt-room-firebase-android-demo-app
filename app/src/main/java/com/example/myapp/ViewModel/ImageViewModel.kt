package com.example.myapp.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapp.Model.DisplayImageModel
import com.example.myapp.Repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val repository: ImageRepository
) : ViewModel() {

    val allImages: LiveData<List<DisplayImageModel>> = repository.allImages.asLiveData()

    fun insert(image: DisplayImageModel) = viewModelScope.launch {
        repository.insert(image)
    }

    fun delete(image: DisplayImageModel) = viewModelScope.launch {
        repository.delete(image)
    }

}
