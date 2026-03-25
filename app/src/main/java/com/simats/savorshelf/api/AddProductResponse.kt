package com.simats.savorshelf.api

data class AddProductResponse(
    val status: String,
    val message: String,
    val item_id: Int?,
    val item_name: String?,
    val image_url: String?,
    val saved_expiry: String?
)
