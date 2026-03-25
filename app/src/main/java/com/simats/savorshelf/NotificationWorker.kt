package com.simats.savorshelf

import android.content.Context
import com.simats.savorshelf.api.RetrofitClient
import kotlinx.coroutines.runBlocking
import androidx.work.ForegroundInfo
import androidx.core.app.NotificationCompat
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class NotificationWorker(val context: Context, params: androidx.work.WorkerParameters) : androidx.work.Worker(context, params) {

    override fun doWork(): androidx.work.ListenableWorker.Result {
        val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userId = sharedPrefs.getInt("user_id", -1)
        val lastNotifiedId = sharedPrefs.getInt("last_notified_notif_id", -1)

        if (userId == -1) return androidx.work.ListenableWorker.Result.success()
        return try {
            runBlocking {
                val response = RetrofitClient.apiService.getNotifications(userId.toString())
                if (response.isSuccessful) {
                    val notifs = response.body()?.notifications ?: emptyList()
                    val newUnread = notifs.filter { (it.isUnread == true) && (it.id ?: -1) > lastNotifiedId }
                    
                    if (newUnread.isNotEmpty()) {
                        // Notify up to 20 oldest-to-newest to avoid missing high-volume alerts
                        val toNotify = newUnread.sortedBy { it.id ?: 0 }.takeLast(20)
                        toNotify.forEach { notification ->
                            NotificationHelper.showNotification(
                                context,
                                notification.title ?: "Notification",
                                notification.message ?: "",
                                notification.id ?: 0
                            )
                        }
                        
                        // Set lastNotifiedId to the max found
                        val highestNotified = toNotify.maxOfOrNull { it.id ?: -1 } ?: lastNotifiedId
                        if (highestNotified > lastNotifiedId) {
                            sharedPrefs.edit().putInt("last_notified_notif_id", highestNotified).apply()
                        }
                    }
                }
            }
            androidx.work.ListenableWorker.Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            androidx.work.ListenableWorker.Result.retry()
        }
    }

    override fun getForegroundInfo(): ForegroundInfo {
        val channelId = "savorshelf_background_sync"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Syncing Pantry", NotificationManager.IMPORTANCE_LOW)
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
        
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("SavorShelf is checking for updates")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
            
        return ForegroundInfo(999, notification)
    }
}
