package com.example.schoolimpact.ui.main.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolimpact.data.model.ProfileData
import com.example.schoolimpact.data.model.UpdateProfileResponse
import com.example.schoolimpact.data.repository.UserRepository
import com.example.schoolimpact.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow<Result<ProfileData>>(Result.Loading)
    val userProfile = _userProfile.asStateFlow()

    private val _updateProfile = MutableStateFlow<Result<UpdateProfileResponse>>(Result.Loading)
    val updateProfile = _updateProfile.asStateFlow()

    fun getUserProfile() {
        viewModelScope.launch {
            userRepository.getUserProfile().collect { result ->
                _userProfile.value = result
            }
        }
    }

    fun updateProfile(
        name: String, education: String, imageFile: File, phoneNumber: String, password: String
    ) {
        viewModelScope.launch {
            userRepository.updateUserProfile(name, education, imageFile, phoneNumber, password)
                .collect { result ->
                    _updateProfile.value = result
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}