package com.cycling.data.repository

import com.cycling.data.player.PlayerManager
import com.cycling.domain.model.PlayerState
import com.cycling.domain.model.RepeatMode
import com.cycling.domain.model.Song
import com.cycling.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepositoryImpl @Inject constructor(
    private val playerManager: PlayerManager
) : PlayerRepository {

    override val playerState: Flow<PlayerState> = playerManager.playerState

    override fun playSong(song: Song, queue: List<Song>) {
        playerManager.playSong(song, queue)
    }

    override fun playPause() {
        playerManager.playPause()
    }

    override fun seekTo(position: Long) {
        playerManager.seekTo(position)
    }

    override fun skipToNext() {
        playerManager.skipToNext()
    }

    override fun skipToPrevious() {
        playerManager.skipToPrevious()
    }

    override fun setRepeatMode(repeatMode: RepeatMode) {
        playerManager.setRepeatMode(repeatMode)
    }

    override fun setShuffleMode(shuffle: Boolean) {
        playerManager.setShuffleMode(shuffle)
    }

    override fun addToQueue(song: Song) {
        playerManager.addToQueue(song)
    }

    override fun removeFromQueue(index: Int) {
        playerManager.removeFromQueue(index)
    }

    override fun clearQueue() {
        playerManager.clearQueue()
    }

    override fun getCurrentState(): PlayerState = playerManager.getCurrentState()
}
