package com.ynov.spookify.data.mapper

import com.ynov.spookify.data.remote.dto.ImageDto

/** Taille moyenne en priorité : assez nette pour une carte, légère à télécharger. */
fun ImageDto?.artworkUrl(): String = this?.medium ?: this?.large ?: this?.small ?: ""

/** Grande taille pour le lecteur plein écran, où la pochette occupe tout l'écran. */
fun ImageDto?.largeArtworkUrl(): String = this?.large ?: this?.medium ?: this?.small ?: ""

fun ImageDto?.coverUrl(): String = this?.coverMedium ?: this?.coverLarge ?: this?.large ?: ""
