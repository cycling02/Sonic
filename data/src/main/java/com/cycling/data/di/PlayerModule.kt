package com.cycling.data.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.cycling.data.player.PlayerManager
import com.cycling.data.repository.PlayerRepositoryImpl
import com.cycling.domain.repository.PlayerRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlayerModule {

    @Binds
    @Singleton
    abstract fun bindPlayerRepository(
        playerRepositoryImpl: PlayerRepositoryImpl
    ): PlayerRepository

    companion object {
        @Provides
        @Singleton
        @OptIn(UnstableApi::class)
        fun provideExoPlayer(
            @ApplicationContext context: Context
        ): ExoPlayer {
            return ExoPlayer.Builder(context)
                .build()
        }

        @Provides
        @Singleton
        fun providePlayerManager(
            exoPlayer: ExoPlayer,
            @ApplicationContext context: Context
        ): PlayerManager {
            return PlayerManager(exoPlayer, context)
        }
    }
}
