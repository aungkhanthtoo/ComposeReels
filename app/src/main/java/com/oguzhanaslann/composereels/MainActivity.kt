package com.oguzhanaslann.composereels

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.oguzhanaslann.composereels.ui.theme.ComposeReelsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ComposeReelsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val context = LocalContext.current
                    val playerPool = remember { PlayerPool(context) }

                    DisposableEffect(Unit) {
                        onDispose {
                            playerPool.releaseAll()
                        }
                    }

                    VideoPager(
                        playerPool = playerPool,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun VideoPager(
    playerPool: PlayerPool,
    modifier: Modifier = Modifier
) {
    val videoUrls = remember {
        listOf(
            "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4",
            "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4",
            "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4",
            "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4",
            "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4",
            "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4",
        )
    }
    val pagerState = rememberPagerState { videoUrls.size }

    VerticalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize()
    ) { page ->

        var player by remember { mutableStateOf<Player?>(null) }

        LaunchedEffect(page) {
            snapshotFlow { pagerState.settledPage }.collect { settledPage ->
                if (page == settledPage) {
                    val url = videoUrls[settledPage]
                    val pagePlayer = playerPool.getPlayerForPage(page)
                    pagePlayer.playWhenReady = false
                    pagePlayer.clearMediaItems()
                    pagePlayer.setMediaItem(MediaItem.fromUri(url))
                    pagePlayer.prepare()
                    pagePlayer.setPlaybackSpeed(5.0f)
                    pagePlayer.playWhenReady = true
                    player = pagePlayer
                } else {
                    player?.playWhenReady = false
                    player?.stop()
                }
            }

        }

        player?.let {
            VideoPlayer(
                player = it,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
