package com.simats.savorshelf.api

data class ResetPasswordRequest(
    val email: String,
    val new_password: String,
    val confirm_password: String
)
