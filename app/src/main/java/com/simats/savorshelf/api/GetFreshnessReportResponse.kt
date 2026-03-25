package com.simats.savorshelf.api

data class GetFreshnessReportResponse(
    val status: String,
    val summary: FreshnessSummary?,
    val items: List<ReportItem>?
)

data class FreshnessSummary(
    val fresh: Int,
    val use_soon: Int,
    val expired: Int,
    val weekly_consumed: Int?,
    val weekly_wasted: Int?,
    val most_wasted_item: String?
)

data class ReportItem(
    val id: String?,
    val name: String?,
    val detailValue: String?,
    val freshnessLabel: String?,
    val storageType: String?,
    val imageUrl: String?,
    val progress: Int?,
    @com.google.gson.annotations.SerializedName("isLabeled")
    val isLabeled: Boolean?
)
