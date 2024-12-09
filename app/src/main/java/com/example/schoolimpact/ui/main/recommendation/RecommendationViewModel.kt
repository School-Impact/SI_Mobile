package com.example.schoolimpact.ui.main.recommendation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schoolimpact.data.local.HistoryDao
import com.example.schoolimpact.data.local.HistoryEntity
import com.example.schoolimpact.data.model.MlResponse
import com.example.schoolimpact.data.repository.MlRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val repository: MlRepository,
    private val historyDao: HistoryDao
) : ViewModel() {
    private val _interestResult = MutableStateFlow<MlResult<MlResponse>>(MlResult.Initial)
    val interestResult = _interestResult.asStateFlow()

    val recommendationHistory = historyDao.getAllRecommendations()


    fun postInterest(interest: String) {
        viewModelScope.launch {
            repository.postInterest(interest)
                .catch { e ->
                    _interestResult.value = MlResult.Error(e.message.toString())
                }
                .collectLatest { result ->
                    if (result is MlResult.Success) {
                        val entity = HistoryEntity(
                            majorId = result.data.mlResult.userId,
                            majors = result.data.mlResult.majors,
                            interest = result.data.mlResult.interest
                        )
                        historyDao.insertHistory(entity)
                    }
                }
        }
    }

}