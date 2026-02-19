package com.cycling.presentation.scan

import com.cycling.domain.model.ScanResult

data class ScanUiState(
    val isScanning: Boolean = false,
    val scanStep: ScanStep = ScanStep.Idle,
    val progress: Float = 0f,
    val songsProcessed: Int = 0,
    val totalSongs: Int = 0,
    val scanResult: ScanResult? = null,
    val error: String? = null
)

sealed interface ScanStep {
    data object Idle : ScanStep
    data object QueryingMediaStore : ScanStep
    data object SavingToDatabase : ScanStep
    data object Completed : ScanStep
    data object Error : ScanStep
}

sealed interface ScanIntent {
    data object StartScan : ScanIntent
    data object ResetScan : ScanIntent
    data object NavigateBack : ScanIntent
}

sealed interface ScanEffect {
    data class ShowToast(val message: String) : ScanEffect
    data object NavigateBack : ScanEffect
}
