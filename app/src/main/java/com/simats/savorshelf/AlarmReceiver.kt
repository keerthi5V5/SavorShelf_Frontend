package com.simats.savorshelf

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        
        // 1. Enqueue Expedited Work (Standard robust way)
        val workRequest = androidx.work.OneTimeWorkRequestBuilder<NotificationWorker>()
            .setExpedited(androidx.work.OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
        androidx.work.WorkManager.getInstance(context).enqueue(workRequest)

        // 2. Direct Sync Check (Redundant high-priority check)
        // goAsync allows the receiver to stay alive for up to 10-30 seconds to do a quick network check
        val pendingResult = goAsync()
        kotlinx.coroutines.GlobalScope.launch {
            try {
                val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val userId = sharedPrefs.getInt("user_id", -1)
                val lastNotifiedId = sharedPrefs.getInt("last_notified_notif_id", -1)

                if (userId != -1) {
                    val response = com.simats.savorshelf.api.RetrofitClient.apiService.getNotifications(userId.toString())
                    if (response.isSuccessful) {
                        val notifs = response.body()?.notifications ?: emptyList()
                        val newUnread = notifs.filter { (it.isUnread == true) && (it.id ?: -1) > lastNotifiedId }
                        
                        if (newUnread.isNotEmpty()) {
                            val toNotify = newUnread.sortedBy { it.id ?: 0 }.takeLast(5) 
                            toNotify.forEach { notification ->
                                NotificationHelper.showNotification(
                                    context,
                                    notification.title ?: "Notification",
                                    notification.message ?: "",
                                    notification.id ?: 0
                                )
                            }
                            val highest = toNotify.maxOfOrNull { it.id ?: -1 } ?: lastNotifiedId
                            if (highest > lastNotifiedId) {
                                sharedPrefs.edit().putInt("last_notified_notif_id", highest).apply()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
        
        // Re-schedule the next alarm/pulse
        AlarmHelper.scheduleNextAlarm(context)
    }
}
