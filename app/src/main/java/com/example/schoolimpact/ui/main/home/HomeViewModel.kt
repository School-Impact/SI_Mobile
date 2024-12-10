package com.example.schoolimpact.ui.main.home

import androidx.lifecycle.ViewModel
import com.example.schoolimpact.data.preferences.AuthDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    authDataSource: AuthDataSource
) : ViewModel() {

    val userFirstName = authDataSource.userName
}