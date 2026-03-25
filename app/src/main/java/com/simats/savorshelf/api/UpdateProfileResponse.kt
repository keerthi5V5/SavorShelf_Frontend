package com.simats.savorshelf.api

data class UpdateProfileResponse(
    val status: String,
    val message: String,
    val new_name: String?
)
