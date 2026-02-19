package com.cycling.data.local.scanner

import android.content.Context
import com.cycling.data.local.dao.SongDao
import com.cycling.data.local.mediastore.MediaStoreHelper
import com.cycling.data.mapper.toEntity
import com.cycling.domain.model.ScanProgress
import com.cycling.domain.model.ScanResult
import com.cycling.domain.model.ScanStep
import com.cycling.domain.repository.ExcludedFolderRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicScanner @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mediaStoreHelper: MediaStoreHelper,
    private val songDao: SongDao,
    private val excludedFolderRepository: ExcludedFolderRepository
) {
    private val _scanProgress = MutableStateFlow(ScanProgress())
    val scanProgress: StateFlow<ScanProgress> = _scanProgress.asStateFlow()

    suspend fun scanMusic(): Result<ScanResult> = withContext(Dispatchers.IO) {
        try {
            val startTime = System.currentTimeMillis()
            
            _scanProgress.value = ScanProgress(
                isScanning = true,
                currentStep = ScanStep.QUERYING_MEDIASTORE
            )
            
            val excludedPaths = excludedFolderRepository.getExcludedPaths()
            val songs = mediaStoreHelper.queryAllSongs(excludedPaths)
            
            _scanProgress.value = ScanProgress(
                isScanning = true,
                currentStep = ScanStep.SAVING_TO_DATABASE,
                totalSongs = songs.size
            )
            
            songDao.deleteAllSongs()
            
            var processed = 0
            songs.chunked(BATCH_SIZE).forEach { chunk ->
                songDao.insertSongs(chunk.map { it.toEntity() })
                processed += chunk.size
                _scanProgress.value = ScanProgress(
                    isScanning = true,
                    currentStep = ScanStep.SAVING_TO_DATABASE,
                    songsProcessed = processed,
                    totalSongs = songs.size
                )
            }
            
            val albums = mediaStoreHelper.queryAllAlbums()
            val artists = mediaStoreHelper.queryAllArtists()
            
            val duration = System.currentTimeMillis() - startTime
            
            _scanProgress.value = ScanProgress(
                isScanning = false,
                currentStep = ScanStep.COMPLETED,
                songsProcessed = songs.size,
                totalSongs = songs.size
            )
            
            Result.success(
                ScanResult(
                    songsFound = songs.size,
                    albumsFound = albums.size,
                    artistsFound = artists.size,
                    duration = duration,
                    timestamp = System.currentTimeMillis()
                )
            )
        } catch (e: Exception) {
            _scanProgress.value = ScanProgress(
                isScanning = false,
                currentStep = ScanStep.ERROR
            )
            Result.failure(e)
        }
    }

    fun resetProgress() {
        _scanProgress.value = ScanProgress()
    }

    companion object {
        private const val BATCH_SIZE = 50
    }
}
