package com.simats.savorshelf.api

data class GetPantryItemsResponse(
    val status: String,
    val items: List<PantryItem>
)

data class PantryItem(
    val id: String?,
    val name: String?,
    val detailValue: String?,
    val freshnessLabel: String?,
    val storageType: String?,
    val imageUrl: String?,
    val progress: Int?,
    val isLabeled: Boolean?
)
