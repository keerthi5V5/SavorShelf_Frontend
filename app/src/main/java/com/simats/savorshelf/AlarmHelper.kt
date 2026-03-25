package com.simats.savorshelf

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

object AlarmHelper {
    fun scheduleNextAlarm(context: Context) {
        val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val notificationsEnabled = sharedPrefs.getBoolean("notifications_enabled", true)
        val preferredTimeStr = sharedPrefs.getString("preferred_alert_time", "9:00 AM") ?: "9:00 AM"
        
        if (!notificationsEnabled) {
            cancelAlarms(context)
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        // 1. PREFERRED TIME ALARM (RequestCode 100)
        val preferredIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "ACTION_PREFERRED_TIME"
        }
        val preferredPending = PendingIntent.getBroadcast(
            context, 100, preferredIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 2. ONE-MINUTE PULSE ALARM (RequestCode 200)
        val pulseIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "ACTION_PULSE_CHECK"
        }
        val pulsePending = PendingIntent.getBroadcast(
            context, 200, pulseIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calendar = Calendar.getInstance()
        val now = Calendar.getInstance()
        
        try {
            val parts = preferredTimeStr.split(" ", ":")
            var hour = parts[0].toInt()
            val minute = parts[1].toInt()
            val amPm = parts[2].uppercase()
            
            if (amPm == "PM" && hour < 12) hour += 12
            if (amPm == "AM" && hour == 12) hour = 0
            
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            
            if (calendar.before(now)) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
        } catch (e: Exception) {
            calendar.timeInMillis = System.currentTimeMillis() + (24 * 60 * 60 * 1000)
        }
        
        // Schedule Preferred Time Alarm (Exact)
        setExactAlarm(alarmManager, calendar.timeInMillis, preferredPending)
        
        // Schedule Next Pulse Alarm (1-minute interval)
        val pulseInterval = 1 * 60 * 1000L // 1 Minute
        setExactAlarm(alarmManager, System.currentTimeMillis() + pulseInterval, pulsePending)
    }

    private fun setExactAlarm(alarmManager: AlarmManager, time: Long, pendingIntent: PendingIntent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
                    } else {
                        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
                    }
                } else {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
                }
            } catch (e: SecurityException) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
            }
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent)
        }
    }

    fun cancelAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val pIntent = PendingIntent.getBroadcast(context, 100, Intent(context, AlarmReceiver::class.java), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE)
        pIntent?.let { alarmManager.cancel(it) }
        
        val qIntent = PendingIntent.getBroadcast(context, 200, Intent(context, AlarmReceiver::class.java), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE)
        qIntent?.let { alarmManager.cancel(it) }
    }
}
