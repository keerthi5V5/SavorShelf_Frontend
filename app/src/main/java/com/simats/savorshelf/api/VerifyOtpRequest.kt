package com.simats.savorshelf.api

data class VerifyOtpRequest(
    val email: String,
    val otp: String
)
