package com.cycling.data.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.cycling.domain.model.PlayerState
import com.cycling.domain.model.RepeatMode
import com.cycling.domain.model.Song
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OptIn(UnstableApi::class)
class PlayerManager @Inject constructor(
    private val exoPlayer: ExoPlayer,
    @ApplicationContext private val context: Context
) {
    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val supervisorJob = SupervisorJob()
    private val scope = CoroutineScope(supervisorJob + Dispatchers.Main)

    private var playbackQueue: MutableList<Song> = mutableListOf()
    private var currentIndex: Int = -1

    init {
        setupPlayerListener()
    }

    private fun setupPlayerListener() {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playerState.update { it.copy(isPlaying = isPlaying) }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    onSongEnded()
                }
                updatePlaybackPosition()
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                updatePlaybackPosition()
            }
        })

        scope.launch {
            while (true) {
                updatePlaybackPosition()
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    private fun updatePlaybackPosition() {
        _playerState.update {
            it.copy(
                playbackPosition = exoPlayer.currentPosition,
                duration = exoPlayer.duration.takeIf { d -> d > 0 } ?: 0
            )
        }
    }

    private fun onSongEnded() {
        when (_playerState.value.repeatMode) {
            RepeatMode.ONE -> {
                exoPlayer.seekTo(0)
                exoPlayer.play()
            }
            RepeatMode.ALL -> {
                skipToNextInternal(wrapAround = true)
            }
            RepeatMode.OFF -> {
                if (currentIndex < playbackQueue.size - 1) {
                    skipToNextInternal(wrapAround = false)
                }
            }
        }
    }

    private fun createMediaItem(song: Song): MediaItem {
        val metadataBuilder = MediaMetadata.Builder()
            .setTitle(song.title)
            .setArtist(song.artist)
            .setAlbumTitle(song.album)

        song.albumArt?.let { artworkUri ->
            metadataBuilder.setArtworkUri(android.net.Uri.parse(artworkUri))
        }

        return MediaItem.Builder()
            .setUri(song.path)
            .setMediaMetadata(metadataBuilder.build())
            .build()
    }

    fun playSong(song: Song, queue: List<Song> = emptyList()) {
        if (queue.isNotEmpty()) {
            playbackQueue = queue.toMutableList()
            currentIndex = queue.indexOf(song)
        } else {
            if (!playbackQueue.contains(song)) {
                playbackQueue.add(song)
            }
            currentIndex = playbackQueue.indexOf(song)
        }

        PlayerService.start(context)

        val mediaItem = createMediaItem(song)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true

        _playerState.update {
            it.copy(
                currentSong = song,
                playbackQueue = playbackQueue.toList(),
                queueIndex = currentIndex,
                isPlaying = true
            )
        }
    }

    fun playPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
        _playerState.update { it.copy(playbackPosition = position) }
    }

    fun skipToNext() {
        skipToNextInternal(wrapAround = _playerState.value.repeatMode == RepeatMode.ALL)
    }

    private fun skipToNextInternal(wrapAround: Boolean) {
        if (playbackQueue.isEmpty()) return

        val nextIndex = if (_playerState.value.shuffleMode) {
            playbackQueue.indices.random()
        } else {
            currentIndex + 1
        }

        if (nextIndex < playbackQueue.size) {
            playSongAtIndex(nextIndex)
        } else if (wrapAround && nextIndex >= playbackQueue.size) {
            playSongAtIndex(0)
        }
    }

    fun skipToPrevious() {
        if (playbackQueue.isEmpty()) return

        if (exoPlayer.currentPosition > 3000) {
            exoPlayer.seekTo(0)
            return
        }

        val prevIndex = if (_playerState.value.shuffleMode) {
            playbackQueue.indices.random()
        } else {
            currentIndex - 1
        }

        if (prevIndex >= 0) {
            playSongAtIndex(prevIndex)
        } else if (_playerState.value.repeatMode == RepeatMode.ALL) {
            playSongAtIndex(playbackQueue.size - 1)
        }
    }

    private fun playSongAtIndex(index: Int) {
        if (index in playbackQueue.indices) {
            currentIndex = index
            val song = playbackQueue[index]

            val mediaItem = createMediaItem(song)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true

            _playerState.update {
                it.copy(
                    currentSong = song,
                    queueIndex = index,
                    isPlaying = true
                )
            }
        }
    }

    fun setRepeatMode(repeatMode: RepeatMode) {
        _playerState.update { it.copy(repeatMode = repeatMode) }
    }

    fun setShuffleMode(shuffle: Boolean) {
        _playerState.update { it.copy(shuffleMode = shuffle) }
    }

    fun addToQueue(song: Song) {
        playbackQueue.add(song)
        _playerState.update { it.copy(playbackQueue = playbackQueue.toList()) }
    }

    fun removeFromQueue(index: Int) {
        if (index in playbackQueue.indices) {
            playbackQueue.removeAt(index)
            if (currentIndex > index) {
                currentIndex--
            } else if (currentIndex == index) {
                if (playbackQueue.isNotEmpty()) {
                    playSongAtIndex(minOf(currentIndex, playbackQueue.size - 1))
                } else {
                    exoPlayer.stop()
                    _playerState.update { PlayerState() }
                }
            }
            _playerState.update { it.copy(playbackQueue = playbackQueue.toList()) }
        }
    }

    fun clearQueue() {
        playbackQueue.clear()
        currentIndex = -1
        exoPlayer.stop()
        _playerState.update { PlayerState() }
    }

    fun getCurrentState(): PlayerState = _playerState.value

    fun release() {
        supervisorJob.cancel()
    }
}
