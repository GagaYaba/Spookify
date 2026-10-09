package com.ynov.spookify.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ynov.spookify.R
import com.ynov.spookify.ui.screens.player.PlayerUiState
import com.ynov.spookify.ui.util.toDurationText

@Composable
fun MiniPlayer(
    state: PlayerUiState,
    onClick: () -> Unit,
    onPlayPause: () -> Unit,
    onToggleMute: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val player = state.player
    val track = player.currentTrack ?: return
    val progress = if (player.durationMs > 0) player.positionMs.toFloat() / player.durationMs else 0f

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ArtworkImage(url = track.artworkUrl, modifier = Modifier.size(40.dp), shape = MaterialTheme.shapes.small)
                Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = track.artistName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                IconButton(onClick = onPlayPause) {
                    Icon(
                        imageVector = if (player.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = stringResource(if (player.isPlaying) R.string.pause else R.string.play),
                    )
                }
                IconButton(onClick = onToggleMute) {
                    Icon(
                        imageVector = if (player.isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = stringResource(if (player.isMuted) R.string.unmute else R.string.mute),
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(progress = { progress.coerceIn(0f, 1f) }, modifier = Modifier.weight(1f))
                Text(
                    text = "-" + (player.durationMs - player.positionMs).coerceAtLeast(0L).toDurationText(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
    }
}
