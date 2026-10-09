package com.ynov.spookify.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Audius renvoie une map de tailles ; pochettes et photos de couverture n'ont pas les mêmes clés.
@Serializable
data class ImageDto(
    @SerialName("150x150") val small: String? = null,
    @SerialName("480x480") val medium: String? = null,
    @SerialName("1000x1000") val large: String? = null,
    @SerialName("640x") val coverMedium: String? = null,
    @SerialName("2000x") val coverLarge: String? = null,
)
