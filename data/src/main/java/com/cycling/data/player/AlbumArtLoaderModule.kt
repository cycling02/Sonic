package com.cycling.data.player

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AlbumArtLoaderModule {
    @Binds
    @Singleton
    abstract fun bindAlbumArtLoader(impl: CoilAlbumArtLoader): AlbumArtLoader
}
