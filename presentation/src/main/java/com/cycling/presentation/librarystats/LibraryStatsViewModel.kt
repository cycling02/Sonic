package com.cycling.presentation.librarystats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cycling.domain.usecase.GetLibraryStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LibraryStatsViewModel @Inject constructor(
    private val getLibraryStatsUseCase: GetLibraryStatsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryStatsUiState())
    val uiState: StateFlow<LibraryStatsUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<LibraryStatsEffect>()
    val uiEffect: SharedFlow<LibraryStatsEffect> = _uiEffect.asSharedFlow()

    init {
        loadData()
    }

    fun handleIntent(intent: LibraryStatsIntent) {
        Timber.d("handleIntent: $intent")
        when (intent) {
            is LibraryStatsIntent.LoadData -> loadData()
        }
    }

    private fun loadData() {
        Timber.d("loadData: starting")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val stats = getLibraryStatsUseCase()
                Timber.d("loadData: loaded stats successfully")
                _uiState.update { it.copy(
                    stats = stats,
                    isLoading = false
                )}
            } catch (e: Exception) {
                Timber.e(e, "loadData: error loading stats")
                _uiState.update { it.copy(
                    isLoading = false,
                    error = e.message
                )}
            }
        }
    }
}
