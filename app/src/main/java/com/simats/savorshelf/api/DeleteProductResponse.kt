package com.simats.savorshelf.api

data class DeleteProductResponse(
    val status: String,
    val message: String,
    val deleted_id: String?
)
