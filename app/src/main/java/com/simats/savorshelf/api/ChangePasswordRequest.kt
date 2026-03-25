package com.simats.savorshelf.api

data class ChangePasswordRequest(
    val user_id: Int,
    val current_password: String,
    val new_password: String,
    val confirm_new_password: String
)
