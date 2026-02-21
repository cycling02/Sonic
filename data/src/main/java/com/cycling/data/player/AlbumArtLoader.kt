package com.cycling.data.player

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import coil3.ImageLoader
import coil3.memory.MemoryCache
import coil3.request.ImageRequest
import coil3.request.bitmapConfig
import coil3.size.Size
import coil3.toBitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

interface AlbumArtLoader {
    suspend fun loadAlbumArt(uri: Uri): Bitmap?
}

@Singleton
class CoilAlbumArtLoader @Inject constructor(
    @ApplicationContext private val context: Context
) : AlbumArtLoader {

    private val imageLoader = ImageLoader.Builder(context)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .memoryCache {
            MemoryCache.Builder()
                .maxSizePercent(context)
                .build()
        }
        .build()

    override suspend fun loadAlbumArt(uri: Uri): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val request = ImageRequest.Builder(context)
                    .data(uri)
                    .size(Size(512, 512))
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build()

                val result = imageLoader.execute(request)
                result.image?.toBitmap()
            } catch (e: Exception) {
                null
            }
        }
    }
}
