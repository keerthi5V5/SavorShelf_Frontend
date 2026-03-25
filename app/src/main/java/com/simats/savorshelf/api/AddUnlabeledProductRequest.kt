package com.simats.savorshelf.api

data class AddUnlabeledProductRequest(
    val user_id: Int,
    val category: String,
    val item_name: String,
    val custom_name: String,
    val purchase_date: String,
    val quantity: String,
    val storage_type: String
)
