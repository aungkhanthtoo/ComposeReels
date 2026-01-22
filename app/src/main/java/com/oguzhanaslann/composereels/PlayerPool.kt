package com.oguzhanaslann.composereels

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import okhttp3.OkHttpClient
import java.io.File

@UnstableApi
class PlayerPool(
    private val context: Context,
    poolSize: Int = 3,
    private val cacheSizeMb: Long = 100L
) {
    private val databaseProvider by lazy { StandaloneDatabaseProvider(context) }

    private val cache: SimpleCache by lazy {
        val cacheDir = File(context.cacheDir, "media_cache")
        if (!cacheDir.exists()) cacheDir.mkdirs()
        SimpleCache(
            cacheDir,
            LeastRecentlyUsedCacheEvictor(cacheSizeMb * 1024 * 1024),
            databaseProvider
        )
    }

    private val okHttpClient = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .build()

    private val upstreamFactory = OkHttpDataSource.Factory(okHttpClient)

    private val cacheDataSourceFactory: CacheDataSource.Factory by lazy {
        CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(upstreamFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    private val mediaSourceFactory: DefaultMediaSourceFactory by lazy {
        DefaultMediaSourceFactory(cacheDataSourceFactory)
    }

    private val players: List<Player> = List(poolSize) {
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .build().apply {
                repeatMode = Player.REPEAT_MODE_ONE
            }
    }

    fun getPlayerForPage(page: Int): Player {
        return players[page % players.size]
    }

    fun releaseAll() {
        players.forEach {
            it.stop()
            it.release()
        }
        cache.release()
    }
}
