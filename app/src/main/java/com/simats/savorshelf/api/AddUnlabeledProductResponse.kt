package com.simats.savorshelf.api

data class AddUnlabeledProductResponse(
    val status: String,
    val message: String,
    val item_id: Int?,
    val item_name: String?,
    val category: String?,
    val purchase_date: String?,
    val expiry: String?,
    val quantity: String?,
    val storage_type: String?,
    val image_url: String?,
    val estimated_shelf_life_days: Int?
)
