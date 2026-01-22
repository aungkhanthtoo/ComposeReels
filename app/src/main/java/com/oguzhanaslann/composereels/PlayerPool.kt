package com.oguzhanaslann.composereels

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class PlayerPool(
    private val context: Context,
    poolSize: Int = 3
) {
    private val players: List<Player> = List(poolSize) {
        ExoPlayer.Builder(context).build().apply {
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
    }
}
