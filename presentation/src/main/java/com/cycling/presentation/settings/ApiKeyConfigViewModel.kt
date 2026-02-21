package com.cycling.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cycling.domain.repository.AiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ApiKeyConfigViewModel @Inject constructor(
    private val aiRepository: AiRepository
) : ViewModel() {

    private val _saved = MutableStateFlow(false)
    val saved = _saved.asStateFlow()

    val hasExistingKey = kotlinx.coroutines.flow.flow {
        emit(aiRepository.hasApiKey())
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    fun saveApiKey(apiKey: String) {
        Timber.d("saveApiKey: saving API key")
        viewModelScope.launch {
            aiRepository.setApiKey(apiKey)
            _saved.value = true
            Timber.d("saveApiKey: API key saved successfully")
        }
    }

    fun clearApiKey() {
        Timber.d("clearApiKey: clearing API key")
        viewModelScope.launch {
            aiRepository.setApiKey("")
            _saved.value = true
            Timber.d("clearApiKey: API key cleared successfully")
        }
    }
}
