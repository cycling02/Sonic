package com.cycling.data.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.MediaStyleNotificationHelper
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionError
import androidx.media3.session.SessionResult
import com.cycling.data.R
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.Futures
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
@OptIn(UnstableApi::class)
class PlayerService : MediaSessionService() {

    @Inject
    lateinit var exoPlayer: ExoPlayer

    @Inject
    lateinit var albumArtLoader: AlbumArtLoader

    private var mediaSession: MediaSession? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var albumArtBitmap: Bitmap? = null
    private var currentLoadJob: Job? = null

    companion object {
        const val CHANNEL_ID = "player_channel"
        const val NOTIFICATION_ID = 1

        fun start(context: Context) {
            val intent = Intent(context, PlayerService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("PlayerService onCreate")
        createNotificationChannel()
        mediaSession = MediaSession.Builder(this, exoPlayer)
            .setCallback(PlayerSessionCallback())
            .build()

        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                Timber.d("PlayerService: isPlaying changed to $isPlaying")
                updateNotification()
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                Timber.d("PlayerService: mediaItem transition - ${mediaItem?.mediaMetadata?.title}")
                loadAlbumArtFromMediaItem(mediaItem)
                updateNotification()
            }
        })
    }

    private fun loadAlbumArtFromMediaItem(mediaItem: MediaItem?) {
        val artworkUri = mediaItem?.mediaMetadata?.artworkUri
        Timber.d("loadAlbumArtFromMediaItem: artworkUri=$artworkUri")
        if (artworkUri == null) {
            Timber.d("loadAlbumArtFromMediaItem: no artworkUri, clearing albumArtBitmap")
            albumArtBitmap = null
            updateNotification()
            return
        }

        currentLoadJob?.cancel()

        currentLoadJob = serviceScope.launch {
            Timber.d("loadAlbumArtFromMediaItem: loading album art from $artworkUri")
            val bitmap = albumArtLoader.loadAlbumArt(artworkUri)
            ensureActive()
            albumArtBitmap = bitmap
            Timber.d("loadAlbumArtFromMediaItem: album art loaded, bitmap=${bitmap != null}")
            updateNotification()
        }
    }

    private fun updateNotification() {
        Timber.d("updateNotification: isPlaying=${exoPlayer.isPlaying}, mediaItemCount=${exoPlayer.mediaItemCount}")
        if (exoPlayer.isPlaying || exoPlayer.mediaItemCount > 0) {
            val notification = createNotification()
            if (exoPlayer.isPlaying) {
                Timber.d("updateNotification: starting foreground")
                startForeground(NOTIFICATION_ID, notification)
            } else {
                Timber.d("updateNotification: updating notification (not playing)")
                val notificationManager = getSystemService(NotificationManager::class.java)
                notificationManager.notify(NOTIFICATION_ID, notification)
                ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_DETACH)
            }
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        Timber.d("PlayerService onDestroy")
        mediaSession?.release()
        mediaSession = null
        currentLoadJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "音乐播放",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "音乐播放控制"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val metadata = exoPlayer.currentMediaItem?.mediaMetadata
        val title = metadata?.title?.toString() ?: "未知歌曲"
        val artist = metadata?.artist?.toString() ?: "未知艺术家"
        val album = metadata?.albumTitle?.toString()

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(artist)
            .setSubText(album)
            .setSmallIcon(R.drawable.ic_music_note)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(exoPlayer.isPlaying)
            .setShowWhen(false)
            .setStyle(
                MediaStyleNotificationHelper.MediaStyle(mediaSession!!)
            )

        albumArtBitmap?.let { bitmap ->
            builder.setLargeIcon(bitmap)
        }

        return builder.build()
    }

    private inner class PlayerSessionCallback : MediaSession.Callback {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                .build()
        }

        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            return Futures.immediateFuture(SessionResult(SessionError.ERROR_NOT_SUPPORTED))
        }
    }
}
