package com.simats.savorshelf.api

data class LoginResponse(
    val status: String,
    val message: String,
    val user: UserDetails? = null
)

data class UserDetails(
    val id: Int,
    val full_name: String,
    val email: String
)
