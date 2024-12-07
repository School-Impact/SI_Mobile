package com.example.schoolimpact.ui.main.discover.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolimpact.data.model.MajorDetailItem
import com.example.schoolimpact.data.repository.MajorRepository
import com.example.schoolimpact.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MajorDetailViewModel @Inject constructor(
    private val repository: MajorRepository
) : ViewModel() {

    private val _majorDetail = MutableStateFlow<Result<List<MajorDetailItem>>>(Result.Loading)
    val majorDetail = _majorDetail.asStateFlow()

    fun getMajorDetail(id: Int) {
        viewModelScope.launch {
            repository.getMajorDetail(id).collectLatest { result ->
                _majorDetail.value = result
            }
        }
    }
}