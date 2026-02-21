package com.cycling.presentation.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cycling.domain.model.ScanStep
import com.cycling.domain.usecase.GetScanProgressUseCase
import com.cycling.domain.usecase.ResetScanProgressUseCase
import com.cycling.domain.usecase.ScanMusicUseCase
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
class ScanViewModel @Inject constructor(
    private val scanMusicUseCase: ScanMusicUseCase,
    private val getScanProgressUseCase: GetScanProgressUseCase,
    private val resetScanProgressUseCase: ResetScanProgressUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<ScanEffect>()
    val uiEffect: SharedFlow<ScanEffect> = _uiEffect.asSharedFlow()

    init {
        Timber.d("ScanViewModel: initialized")
        observeScanProgress()
    }

    fun handleIntent(intent: ScanIntent) {
        Timber.d("handleIntent: $intent")
        when (intent) {
            is ScanIntent.StartScan -> startScan()
            is ScanIntent.ResetScan -> resetScan()
            is ScanIntent.NavigateBack -> navigateBack()
            is ScanIntent.RequestPermission -> requestPermission()
            is ScanIntent.PermissionResult -> handlePermissionResult(intent.granted)
        }
    }

    fun checkAndRequestPermission(hasPermission: Boolean) {
        Timber.d("checkAndRequestPermission: hasPermission = $hasPermission")
        if (hasPermission) {
            _uiState.update { it.copy(hasStoragePermission = true, shouldRequestPermission = false) }
        } else {
            _uiState.update { it.copy(hasStoragePermission = false, shouldRequestPermission = true) }
        }
    }

    private fun requestPermission() {
        Timber.d("requestPermission: requesting storage permission")
        viewModelScope.launch {
            _uiEffect.emit(ScanEffect.RequestStoragePermission)
        }
    }

    private fun handlePermissionResult(granted: Boolean) {
        Timber.d("handlePermissionResult: granted = $granted")
        if (granted) {
            _uiState.update { it.copy(hasStoragePermission = true, shouldRequestPermission = false) }
        } else {
            Timber.w("handlePermissionResult: permission denied")
            _uiState.update { 
                it.copy(
                    hasStoragePermission = false, 
                    shouldRequestPermission = false,
                    error = "需要存储权限才能扫描音乐文件"
                )
            }
        }
    }

    private fun observeScanProgress() {
        Timber.d("observeScanProgress: starting to observe scan progress")
        viewModelScope.launch {
            getScanProgressUseCase().collect { progress ->
                Timber.d("observeScanProgress: progress = ${progress.songsProcessed}/${progress.totalSongs}, step = ${progress.currentStep}")
                _uiState.update { state ->
                    state.copy(
                        isScanning = progress.isScanning,
                        scanStep = progress.currentStep.toUiScanStep(),
                        songsProcessed = progress.songsProcessed,
                        totalSongs = progress.totalSongs,
                        progress = if (progress.totalSongs > 0) {
                            progress.songsProcessed.toFloat() / progress.totalSongs
                        } else 0f
                    )
                }
            }
        }
    }

    private fun startScan() {
        Timber.d("startScan: starting music scan")
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true, error = null) }
            
            scanMusicUseCase()
                .onSuccess { result ->
                    Timber.d("startScan: scan completed successfully, found ${result.songsFound} songs")
                    _uiState.update { 
                        it.copy(
                            isScanning = false,
                            scanResult = result,
                            scanStep = com.cycling.presentation.scan.ScanStep.Completed
                        )
                    }
                    _uiEffect.emit(ScanEffect.ShowToast("扫描完成: 发现 ${result.songsFound} 首歌曲"))
                }
                .onFailure { error ->
                    Timber.e(error, "startScan: scan failed")
                    _uiState.update { 
                        it.copy(
                            isScanning = false,
                            scanStep = com.cycling.presentation.scan.ScanStep.Error,
                            error = error.message
                        )
                    }
                    _uiEffect.emit(ScanEffect.ShowToast("扫描失败: ${error.message}"))
                }
        }
    }

    private fun resetScan() {
        Timber.d("resetScan: resetting scan state")
        resetScanProgressUseCase()
        _uiState.update { ScanUiState() }
    }

    private fun navigateBack() {
        Timber.d("navigateBack: navigating back")
        viewModelScope.launch {
            _uiEffect.emit(ScanEffect.NavigateBack)
        }
    }

    private fun ScanStep.toUiScanStep(): com.cycling.presentation.scan.ScanStep {
        return when (this) {
            ScanStep.IDLE -> com.cycling.presentation.scan.ScanStep.Idle
            ScanStep.QUERYING_MEDIASTORE -> com.cycling.presentation.scan.ScanStep.QueryingMediaStore
            ScanStep.SAVING_TO_DATABASE -> com.cycling.presentation.scan.ScanStep.SavingToDatabase
            ScanStep.COMPLETED -> com.cycling.presentation.scan.ScanStep.Completed
            ScanStep.ERROR -> com.cycling.presentation.scan.ScanStep.Error
        }
    }
}
