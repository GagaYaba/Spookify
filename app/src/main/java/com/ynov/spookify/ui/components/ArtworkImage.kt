package com.ynov.spookify.ui.components

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

@Composable
fun ArtworkImage(
    url: String,
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    contentDescription: String? = null,
) {
    AsyncImage(
        model = url.ifBlank { null },
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        // Le fond reste visible pendant le chargement ou si l'image est absente.
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.primaryContainer),
    )
}
