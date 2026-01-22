package com.oguzhanaslann.composereels

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    player: Player,
    modifier: Modifier = Modifier
) {
    PlayerSurface(
        modifier = modifier.fillMaxSize(),
        player = player,
        surfaceType = SURFACE_TYPE_TEXTURE_VIEW
    )
}