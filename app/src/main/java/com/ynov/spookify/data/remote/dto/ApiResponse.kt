package com.ynov.spookify.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(val data: T? = null)
