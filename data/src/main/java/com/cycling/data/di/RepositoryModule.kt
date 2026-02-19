package com.cycling.data.di

import com.cycling.data.repository.AlbumRepositoryImpl
import com.cycling.data.repository.ArtistRepositoryImpl
import com.cycling.data.repository.MusicScannerRepositoryImpl
import com.cycling.data.repository.PlaylistRepositoryImpl
import com.cycling.data.repository.ScanProgressRepositoryImpl
import com.cycling.data.repository.SongRepositoryImpl
import com.cycling.domain.repository.AlbumRepository
import com.cycling.domain.repository.ArtistRepository
import com.cycling.domain.repository.MusicScannerRepository
import com.cycling.domain.repository.PlaylistRepository
import com.cycling.domain.repository.ScanProgressRepository
import com.cycling.domain.repository.SongRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSongRepository(
        songRepositoryImpl: SongRepositoryImpl
    ): SongRepository

    @Binds
    @Singleton
    abstract fun bindAlbumRepository(
        albumRepositoryImpl: AlbumRepositoryImpl
    ): AlbumRepository

    @Binds
    @Singleton
    abstract fun bindArtistRepository(
        artistRepositoryImpl: ArtistRepositoryImpl
    ): ArtistRepository

    @Binds
    @Singleton
    abstract fun bindPlaylistRepository(
        playlistRepositoryImpl: PlaylistRepositoryImpl
    ): PlaylistRepository

    @Binds
    @Singleton
    abstract fun bindMusicScannerRepository(
        musicScannerRepositoryImpl: MusicScannerRepositoryImpl
    ): MusicScannerRepository

    @Binds
    @Singleton
    abstract fun bindScanProgressRepository(
        scanProgressRepositoryImpl: ScanProgressRepositoryImpl
    ): ScanProgressRepository
}
