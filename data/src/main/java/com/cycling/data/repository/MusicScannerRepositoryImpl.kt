package com.cycling.data.repository

import com.cycling.data.local.scanner.MusicScanner
import com.cycling.domain.model.ScanResult
import com.cycling.domain.repository.MusicScannerRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicScannerRepositoryImpl @Inject constructor(
    private val musicScanner: MusicScanner
) : MusicScannerRepository {
    override suspend fun scanMusic(): Result<ScanResult> {
        return musicScanner.scanMusic()
    }
}
