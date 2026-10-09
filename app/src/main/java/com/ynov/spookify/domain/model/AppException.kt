package com.ynov.spookify.domain.model

/** Erreur déjà traduite en message lisible : les ViewModels l'affichent telle quelle. */
class AppException(message: String, cause: Throwable? = null) : Exception(message, cause)
