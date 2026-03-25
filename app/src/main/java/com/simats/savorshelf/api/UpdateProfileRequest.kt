package com.simats.savorshelf.api

data class UpdateProfileRequest(
    val user_id: Int,
    val full_name: String
)
