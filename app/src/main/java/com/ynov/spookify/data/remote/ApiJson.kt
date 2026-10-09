package com.ynov.spookify.data.remote

import kotlinx.serialization.json.Json

// Tolérant : l'API Audius ajoute des champs et renvoie parfois des null là où on attend une valeur.
val apiJson = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    isLenient = true
}
