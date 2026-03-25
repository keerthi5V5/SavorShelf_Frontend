package com.simats.savorshelf.api

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("full_name")
    val fullName: String,
    val email: String,
    val password: String,
    @SerializedName("confirm_password")
    val confirmPassword: String
)
