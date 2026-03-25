package com.simats.savorshelf.api

data class GetProductDetailsResponse(
    val status: String,
    val data: ProductDetailData?
)

data class ProductDetailData(
    val item_name: String,
    val image_path: String?,
    val category: String?,
    val storage_location: String?,
    val primary_date_label: String?,
    val primary_date_value: String?,
    val expiry_label: String?,
    val expiry_value: String?,
    val freshness_progress: Int?,
    val days_remaining: Int?,
    val freshness_label: String?,
    val detail_value: String?
)
