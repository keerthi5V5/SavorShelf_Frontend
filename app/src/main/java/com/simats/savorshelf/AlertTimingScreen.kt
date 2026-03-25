package com.simats.savorshelf

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertTimingScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val bgColor = Color(0xFFE9F0EC)
    val textPrimary = Color(0xFF141D1C)
    val textSecondary = Color(0xFF5A6D66) // Darker grey for better contrast

    val primaryGreen = Color(0xFF0D614E)

    var isEnabled by remember { mutableStateOf(true) }
    var selectedTiming by remember { mutableStateOf("3 days before") }
    var weeklySummaryEnabled by remember { mutableStateOf(true) }
    var criticalAlertsEnabled by remember { mutableStateOf(true) }
    
    var preferredAlertTime by remember { mutableStateOf("9:00 AM") }
    var selectedSummaryDay by remember { mutableStateOf("Sunday") }

    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE) }
    val userId = sharedPrefs.getInt("user_id", -1)
    
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (userId != -1) {
            try {
                val response = com.simats.savorshelf.api.RetrofitClient.apiService.getAlertSettings(userId.toString())
                if (response.isSuccessful) {
                    val settings = response.body()?.settings
                    if (settings != null) {
                        isEnabled = settings.isEnabled
                        selectedTiming = when (settings.expiryDaysBefore) {
                            1 -> "1 day before"
                            2 -> "2 days before"
                            3 -> "3 days before"
                            4 -> "4 days before"
                            5 -> "5 days before"
                            else -> "${settings.expiryDaysBefore} days before"
                        }
                        weeklySummaryEnabled = settings.weeklySummaryEnabled
                        criticalAlertsEnabled = settings.criticalAlertEnabled
                        selectedSummaryDay = settings.weeklySummaryDay
                        preferredAlertTime = settings.expiryAlertTime // Use same time for all
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    val showDayPicker = { onDaySelected: (String) -> Unit ->
        val days = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        android.app.AlertDialog.Builder(context)
            .setTitle("Select Day")
            .setItems(days) { _, which ->
                onDaySelected(days[which])
            }
            .show()
    }
    val showTimePicker = { onTimeSelected: (String) -> Unit ->
        val calendar = java.util.Calendar.getInstance()
        android.app.TimePickerDialog(
            context,
            { _, h, m ->
                val amPm = if (h < 12) "AM" else "PM"
                val hour12 = if (h % 12 == 0) 12 else h % 12
                val minStr = m.toString().padStart(2, '0')
                onTimeSelected("$hour12:$minStr $amPm")
            },
            calendar.get(java.util.Calendar.HOUR_OF_DAY),
            calendar.get(java.util.Calendar.MINUTE),
            false
        ).show()
    }

    val timingOptions = listOf(
        Pair("1 day before", "Get notified 1 day before expiry"),
        Pair("2 days before", "Get notified 2 days before expiry"),
        Pair("3 days before", "Get notified 3 days before expiry"),
        Pair("4 days before", "Get notified 4 days before expiry"),
        Pair("5 days before", "Get notified 5 days before expiry")
    )

    Scaffold(
        containerColor = bgColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            // Premium Header with full-bleed green background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        primaryGreen,
                        RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Alert Timing",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Choose when to get notified",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            
            Column(modifier = Modifier.padding(20.dp)) {
            
            // Expiry Alert Settings Info Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFE8EFFF),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Outlined.Info, contentDescription = null, tint = Color(0xFF4C4DDC), modifier = Modifier.size(16.dp).padding(top = 2.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Expiry Alert Settings", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Select how many days before expiry you want to receive notifications. This helps you use products at their peak freshness and reduce waste.", 
                            fontSize = 12.sp, color = textSecondary, lineHeight = 18.sp, fontWeight = FontWeight.Medium)
                    }

                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Notification Preferences (Preference Timing) - MOVED TO TOP
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                        Text("Preference Timing", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                        Spacer(modifier = Modifier.weight(1f))
                        Row(
                            modifier = Modifier
                                .background(primaryGreen.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                                .clickable { showTimePicker { preferredAlertTime = it } }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.AccessTime, contentDescription = "Select Time", tint = primaryGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(preferredAlertTime, fontSize = 13.sp, color = primaryGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Surface(
                        color = Color(0xFFF9FBFA),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "This time will be used for all expiry notifications, weekly summaries, and critical alerts.",
                            fontSize = 11.sp,
                            color = textSecondary,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Weekly Summary", fontSize = 13.sp, color = textPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(modifier = Modifier.clickable {
                                    if (weeklySummaryEnabled) {
                                        showDayPicker { selectedSummaryDay = it }
                                    }
                                }) {
                                    Icon(
                                        Icons.Outlined.DateRange,
                                        contentDescription = "Select Date",
                                        tint = primaryGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Get a weekly summary on $selectedSummaryDay at $preferredAlertTime", 
                                fontSize = 11.sp, 
                                color = textSecondary,
                                fontWeight = FontWeight.Medium
                            )

                        }
                        Switch(
                            checked = weeklySummaryEnabled,
                            onCheckedChange = { weeklySummaryEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = primaryGreen
                            )
                        )
                    }
                    
                    HorizontalDivider(color = Color(0xFFF0F5FA), modifier = Modifier.padding(vertical = 12.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Critical Alerts", fontSize = 13.sp, color = textPrimary, fontWeight = FontWeight.SemiBold)
                            Text("Alert at $preferredAlertTime for items expiring today", fontSize = 11.sp, color = textSecondary, fontWeight = FontWeight.Medium)

                        }
                        Switch(
                            checked = criticalAlertsEnabled,
                            onCheckedChange = { criticalAlertsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = primaryGreen
                            )
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))

            // Select Alert Timing
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text("Select Alert Timing", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    }
                    
                    timingOptions.forEach { (title, subtitle) ->
                        val isSelected = selectedTiming == title
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFFE9F5EF) else Color.White,
                            border = BorderStroke(1.dp, if (isSelected) primaryGreen else Color(0xFFF0F5FA)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable { selectedTiming = title }
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(title, fontSize = 14.sp, color = textPrimary, fontWeight = FontWeight.SemiBold)
                                    Text(subtitle, fontSize = 11.sp, color = textSecondary, fontWeight = FontWeight.Medium)
                                }

                                if (isSelected) {
                                    Icon(Icons.Filled.CheckCircle, contentDescription = "Selected", tint = primaryGreen)
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Preview
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Preview", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textPrimary, modifier = Modifier.padding(bottom = 16.dp))
                    
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFF7ED),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.AccessTime, contentDescription = null, tint = Color(0xFFE26027), modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Expiry Alert", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("\"Organic Milk\" will expire in 3 days", fontSize = 12.sp, color = textSecondary, fontWeight = FontWeight.Medium)

                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                                    Text("Freshness: ", fontSize = 11.sp, color = textSecondary)
                                    Text("45%", fontSize = 11.sp, color = Color(0xFFE26027), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    if (userId != -1) {
                        isLoading = true
                        coroutineScope.launch {
                            try {
                                val daysBefore = when (selectedTiming) {
                                    "1 day before" -> 1
                                    "2 days before" -> 2
                                    "3 days before" -> 3
                                    "4 days before" -> 4
                                    "5 days before" -> 5
                                    else -> 3
                                }
                                val request = com.simats.savorshelf.api.SaveAlertSettingsRequest(
                                    userId = userId,
                                    isEnabled = sharedPrefs.getBoolean("notifications_enabled", true),
                                    expiryDaysBefore = daysBefore,
                                    expiryAlertTime = preferredAlertTime,
                                    weeklySummaryEnabled = weeklySummaryEnabled,
                                    weeklySummaryDay = selectedSummaryDay,
                                    weeklySummaryTime = preferredAlertTime,
                                    criticalAlertEnabled = criticalAlertsEnabled,
                                    criticalAlertTime = preferredAlertTime
                                )
                                val response = com.simats.savorshelf.api.RetrofitClient.apiService.saveAlertSettings(request)
                                if (response.isSuccessful) {
                                    // Save locally for background workers to use immediately
                                    sharedPrefs.edit().apply {
                                        putString("preferred_alert_time", preferredAlertTime)
                                        putInt("expiry_days_before", daysBefore)
                                        putBoolean("weekly_summary_enabled", weeklySummaryEnabled)
                                        putBoolean("critical_alert_enabled", criticalAlertsEnabled)
                                        putString("weekly_summary_day", selectedSummaryDay)
                                        apply()
                                    }
                                    
                                    // Refresh alarms with new time
                                    AlarmHelper.scheduleNextAlarm(context)
                                    
                                    android.widget.Toast.makeText(context, "Settings saved successfully", android.widget.Toast.LENGTH_SHORT).show()
                                    onSaveClick()
                                } else {
                                    android.widget.Toast.makeText(context, "Failed to save settings", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                android.widget.Toast.makeText(context, "Network error", android.widget.Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    } else {
                        onSaveClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("Save Alert Settings", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Version 1.0.0 • © 2026 SAVORSHELF",
                fontSize = 11.sp,
                color = textSecondary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
}
