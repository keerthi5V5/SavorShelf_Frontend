package com.simats.savorshelf.api

import com.google.gson.annotations.SerializedName

data class GetDashboardResponse(
    val status: String, 
    val summary: DashboardSummary?, 
    @SerializedName("daily_tip") val dailyTip: String?, 
    @SerializedName("recent_items") val recentItems: List<DashboardRecentItem>?
)

data class DashboardSummary(
    val fresh: Int, 
    @SerializedName("use_soon") val useSoon: Int, 
    val expired: Int
)

data class DashboardRecentItem(
    val id: String, 
    val name: String, 
    val addedTime: String, 
    val statusLabel: String, 
    val statusValue: String, 
    val freshnessLabel: String, 
    val imageUrl: String,
    val isLabeled: Boolean
)

data class UpdateItemStatusRequest(
    @SerializedName("item_id") val itemId: Int, 
    val status: String
)

data class UpdateItemStatusResponse(
    val status: String, 
    val message: String
)

data class SaveAlertSettingsRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("is_enabled") val isEnabled: Boolean,
    @SerializedName("expiry_days_before") val expiryDaysBefore: Int,
    @SerializedName("expiry_alert_time") val expiryAlertTime: String,
    @SerializedName("weekly_summary_enabled") val weeklySummaryEnabled: Boolean,
    @SerializedName("weekly_summary_day") val weeklySummaryDay: String,
    @SerializedName("weekly_summary_time") val weeklySummaryTime: String,
    @SerializedName("critical_alert_enabled") val criticalAlertEnabled: Boolean,
    @SerializedName("critical_alert_time") val criticalAlertTime: String
)

data class AlertSettingsResponseItem(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("is_enabled") val isEnabled: Boolean,
    @SerializedName("expiry_days_before") val expiryDaysBefore: Int,
    @SerializedName("expiry_alert_time") val expiryAlertTime: String,
    @SerializedName("weekly_summary_enabled") val weeklySummaryEnabled: Boolean,
    @SerializedName("weekly_summary_day") val weeklySummaryDay: String,
    @SerializedName("weekly_summary_time") val weeklySummaryTime: String,
    @SerializedName("critical_alert_enabled") val criticalAlertEnabled: Boolean,
    @SerializedName("critical_alert_time") val criticalAlertTime: String
)

data class SaveAlertSettingsResponse(
    val status: String, 
    val message: String
)

data class GetAlertSettingsResponse(
    val status: String, 
    val settings: AlertSettingsResponseItem?
)

data class NotificationApiItem(
    val id: Int?,
    @SerializedName("pantry_item_id") val pantryItemId: Int?,
    val title: String?,
    val message: String?,
    val type: String?,
    @SerializedName("is_unread") val isUnread: Boolean?,
    @SerializedName("created_at") val createdAt: String?
)

data class GetNotificationsResponse(
    val status: String, 
    val notifications: List<NotificationApiItem>
)

data class MarkNotificationReadRequest(
    @SerializedName("notification_id") val notificationId: Int
)

data class MarkNotificationReadResponse(
    val status: String, 
    val message: String
)

data class DeleteNotificationResponse(
    val status: String, 
    val message: String
)

data class MarkAllNotificationsReadRequest(
    @SerializedName("user_id") val userId: Int
)

data class MarkAllNotificationsReadResponse(
    val status: String,
    val message: String
)

data class DeleteAllNotificationsRequest(
    @SerializedName("user_id") val userId: Int
)

data class DeleteAllNotificationsResponse(
    val status: String,
    val message: String
)

data class TriggerSchedulerResponse(
    val status: String, 
    val message: String
)
