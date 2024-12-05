package com.example.schoolimpact.ui.main.discover.major.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolimpact.data.model.MajorListItem
import com.example.schoolimpact.data.repository.MajorRepository
import com.example.schoolimpact.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MajorViewModel(private val repository: MajorRepository) : ViewModel() {
    private val _majors = MutableStateFlow<Result<List<MajorListItem>>>(Result.Loading)
    val majors = _majors.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    fun fetchMajors(category: String) {
        viewModelScope.launch {
            repository.getMajors(category).collectLatest { result ->
                when (result) {
                    is Result.Loading -> {
                        _majors.value = Result.Loading
                    }

                    is Result.Success -> {
                        _majors.value = Result.Success(result.data)
                    }

                    is Result.Error -> {
                        _majors.value = Result.Error(result.error)
                    }
                }
            }

        }
    }
}