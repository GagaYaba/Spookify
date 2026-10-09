package com.ynov.spookify.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOn
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ynov.spookify.R
import com.ynov.spookify.ui.components.ArtworkImage
import com.ynov.spookify.ui.components.EmptyView
import com.ynov.spookify.ui.util.toDurationText

@Composable
fun PlayerScreen(
    state: PlayerUiState,
    onBack: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleMute: () -> Unit,
    onToggleRepeat: () -> Unit,
    onArtistClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val player = state.player
    val track = player.currentTrack
    if (track == null) {
        Column(modifier.fillMaxSize().statusBarsPadding()) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back)) }
            EmptyView(stringResource(R.string.player_nothing))
        }
        return
    }

    // Position pendant un glissement : on n'envoie le seek qu'au relâchement.
    var dragFraction by remember { mutableStateOf<Float?>(null) }
    val playedFraction = if (player.durationMs > 0) player.positionMs.toFloat() / player.durationMs else 0f
    val shownFraction = (dragFraction ?: playedFraction).coerceIn(0f, 1f)
    val shownPositionMs = (shownFraction * player.durationMs).toLong()

    Box(modifier.fillMaxSize().background(Color.Black)) {
        ArtworkImage(url = track.artworkLargeUrl.ifBlank { track.artworkUrl }, modifier = Modifier.fillMaxSize())
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(0.35f to Color.Transparent, 0.75f to Color.Black.copy(alpha = 0.7f), 1f to Color.Black)
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back), tint = Color.White)
            }
            Text(
                text = stringResource(R.string.player_title),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (state.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = stringResource(if (state.isFavorite) R.string.fav_remove else R.string.fav_add),
                    tint = Color.White,
                )
            }
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().navigationBarsPadding().padding(horizontal = 24.dp, vertical = 24.dp),
        ) {
            Text(
                text = track.title,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = track.artistName,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.75f),
                modifier = Modifier.padding(top = 2.dp).then(
                    if (track.artistId.isNotBlank()) Modifier.clickable { onArtistClick(track.artistId) } else Modifier
                ),
            )

            Slider(
                value = shownFraction,
                onValueChange = { dragFraction = it },
                onValueChangeFinished = {
                    dragFraction?.let { onSeek((it * player.durationMs).toLong()) }
                    dragFraction = null
                },
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.White.copy(alpha = 0.3f),
                ),
                modifier = Modifier.padding(top = 12.dp),
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(shownPositionMs.toDurationText(), style = MaterialTheme.typography.labelSmall, color = Color.White)
                Text(
                    "-" + (player.durationMs - shownPositionMs).coerceAtLeast(0L).toDurationText(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onToggleRepeat) {
                    Icon(
                        imageVector = if (player.repeatAll) Icons.Filled.RepeatOn else Icons.Filled.Repeat,
                        contentDescription = stringResource(R.string.player_repeat),
                        tint = Color.White,
                    )
                }
                IconButton(onClick = onPrevious, enabled = player.hasPrevious) {
                    Icon(
                        Icons.Filled.SkipPrevious,
                        contentDescription = stringResource(R.string.player_previous),
                        tint = Color.White.copy(alpha = if (player.hasPrevious) 1f else 0.4f),
                        modifier = Modifier.size(32.dp),
                    )
                }
                Surface(onClick = onPlayPause, shape = CircleShape, color = Color.White, modifier = Modifier.size(72.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        if (player.isBuffering && !player.isPlaying) {
                            CircularProgressIndicator(Modifier.size(32.dp), color = Color.Black, strokeWidth = 3.dp)
                        } else {
                            Icon(
                                imageVector = if (player.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = stringResource(if (player.isPlaying) R.string.pause else R.string.play),
                                tint = Color.Black,
                                modifier = Modifier.size(36.dp),
                            )
                        }
                    }
                }
                IconButton(onClick = onNext, enabled = player.hasNext) {
                    Icon(
                        Icons.Filled.SkipNext,
                        contentDescription = stringResource(R.string.player_next),
                        tint = Color.White.copy(alpha = if (player.hasNext) 1f else 0.4f),
                        modifier = Modifier.size(32.dp),
                    )
                }
                IconButton(onClick = onToggleMute) {
                    Icon(
                        imageVector = if (player.isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = stringResource(if (player.isMuted) R.string.unmute else R.string.mute),
                        tint = Color.White,
                    )
                }
            }
        }
    }
}
