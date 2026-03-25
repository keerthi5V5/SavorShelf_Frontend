package com.simats.savorshelf

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onProductsClick: () -> Unit,
    onCameraClick: () -> Unit,
    onFreshnessClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onAlertTimingClick: () -> Unit,
    onAboutClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onTermsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit
) {
    val bgColor = Color(0xFFE9F0EC)
    val textPrimary = Color(0xFF141D1C)
    val textSecondary = Color(0xFF5A6D66) // Darker grey for better contrast

    val primaryGreen = Color(0xFF0D614E)
    
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    var currentAvatarRes by remember { mutableStateOf(sharedPrefs.getInt("avatar_res", -1)) }
    
    val userName = sharedPrefs.getString("user_name", "User") ?: "User"
    val userEmail = sharedPrefs.getString("user_email", "user@example.com") ?: "user@example.com"
    val userId = sharedPrefs.getInt("user_id", -1)

    var notificationsEnabled by remember { mutableStateOf(sharedPrefs.getBoolean("notifications_enabled", false)) }
    
    // Dynamic Alert Timing Value
    val expiryDaysBefore = sharedPrefs.getInt("expiry_days_before", 3)
    val alertTimingText = when (expiryDaysBefore) {
        1 -> "1 day before"
        else -> "$expiryDaysBefore days before"
    }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(
                selectedTab = 4, // Settings tab selected
                onHomeClick = onHomeClick,
                onProductsClick = onProductsClick,
                onCameraClick = onCameraClick,
                onFreshnessClick = onFreshnessClick,
                onSettingsClick = onSettingsClick
            )
        },
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Title Top Bar
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
                        Text(
                            text = "Profile",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    
                    // Profile Image
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(3.dp) // Ring effect
                            .clip(CircleShape)
                            .background(primaryGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentAvatarRes != -1) {
                            NetworkImage(
                                model = currentAvatarRes,
                                contentDescription = "Profile Photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                Icons.Outlined.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = userName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = userEmail,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // ACCOUNT
            SectionHeader("ACCOUNT")
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Column {
                    SettingsRow(
                        icon = Icons.Outlined.Person,
                        title = "Edit Profile",
                        onClick = onEditProfileClick
                    )
                    HorizontalDivider(color = Color(0xFFF0F5FA), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsRow(
                        icon = Icons.Outlined.Lock,
                        title = "Change Password",
                        onClick = onChangePasswordClick
                    )
                }
            }

            // PREFERENCES
            SectionHeader("PREFERENCES")
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Column {
                    // Notifications Row (Switch)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Notifications, contentDescription = null, tint = textSecondary, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Notifications", fontSize = 15.sp, color = textPrimary, modifier = Modifier.weight(1f))
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { isChecked ->
                                notificationsEnabled = isChecked
                                sharedPrefs.edit().putBoolean("notifications_enabled", isChecked).apply()
                                
                                // Sync with backend
                                if (userId != -1) {
                                    coroutineScope.launch {
                                        try {
                                            // 1. Get existing settings to preserve them
                                            val getResponse = com.simats.savorshelf.api.RetrofitClient.apiService.getAlertSettings(userId.toString())
                                            var requestBody = com.simats.savorshelf.api.SaveAlertSettingsRequest(
                                                userId = userId,
                                                isEnabled = isChecked,
                                                expiryDaysBefore = 3,
                                                expiryAlertTime = "9:00 AM",
                                                weeklySummaryEnabled = true,
                                                weeklySummaryDay = "Sunday",
                                                weeklySummaryTime = "9:00 AM",
                                                criticalAlertEnabled = true,
                                                criticalAlertTime = "9:00 AM"
                                            )
                                            
                                            if (getResponse.isSuccessful) {
                                                val s = getResponse.body()?.settings
                                                if (s != null) {
                                                    requestBody = com.simats.savorshelf.api.SaveAlertSettingsRequest(
                                                        userId = userId,
                                                        isEnabled = isChecked,
                                                        expiryDaysBefore = s.expiryDaysBefore,
                                                        expiryAlertTime = s.expiryAlertTime,
                                                        weeklySummaryEnabled = s.weeklySummaryEnabled,
                                                        weeklySummaryDay = s.weeklySummaryDay,
                                                        weeklySummaryTime = s.weeklySummaryTime,
                                                        criticalAlertEnabled = s.criticalAlertEnabled,
                                                        criticalAlertTime = s.criticalAlertTime
                                                    )
                                                }
                                            }
                                            
                                            // 2. Save updated settings
                                            com.simats.savorshelf.api.RetrofitClient.apiService.saveAlertSettings(requestBody)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF00C853)
                            )
                        )
                    }
                    if (notificationsEnabled) {
                        HorizontalDivider(color = Color(0xFFF0F5FA), modifier = Modifier.padding(horizontal = 16.dp))
                        // Alert Timing Row (Value)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onAlertTimingClick() }
                                .padding(horizontal = 16.dp, vertical = 18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Schedule, contentDescription = null, tint = textSecondary, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Alert Timing", fontSize = 15.sp, color = textPrimary, modifier = Modifier.weight(1f))
                            Text(alertTimingText, fontSize = 13.sp, color = textSecondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFFCBD5E1), modifier = Modifier.size(20.dp))
                        }

                        // NEW: Notification Reliability Troubleshooting Card
                        HorizontalDivider(color = Color(0xFFF0F5FA), modifier = Modifier.padding(horizontal = 16.dp))
                        Box(modifier = Modifier.padding(16.dp)) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFFF7ED), // Subtle warning orange
                                border = BorderStroke(1.dp, Color(0xFFFFEDD5))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.WarningAmber,
                                            contentDescription = null,
                                            tint = Color(0xFFC2410C),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Ensure Alerts Work",
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFC2410C),
                                            fontSize = 14.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Android systems can 'sleep' apps to save battery, which may delay or block your expiry alerts. To ensure 100% reliability, please set SavorShelf to 'Unrestricted' in battery settings.",
                                        fontSize = 12.sp,
                                        color = Color(0xFF7C2D12),
                                        lineHeight = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = {
                                            try {
                                                val intent = android.content.Intent(android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                // Fallback to app info settings if direct intent fails
                                                val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                                intent.data = android.net.Uri.fromParts("package", context.packageName, null)
                                                context.startActivity(intent)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC2410C)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text("Fix Alert Reliability", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SUPPORT
            SectionHeader("SUPPORT")
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Column {
                    SettingsRow(
                        icon = Icons.Outlined.Info,
                        title = "About SAVORSHELF",
                        onClick = onAboutClick
                    )
                    HorizontalDivider(color = Color(0xFFF0F5FA), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsRow(
                        icon = Icons.Outlined.Shield,
                        title = "Privacy Policy",
                        onClick = onPrivacyClick
                    )
                    HorizontalDivider(color = Color(0xFFF0F5FA), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsRow(
                        icon = Icons.Outlined.Description,
                        title = "Terms & Conditions",
                        onClick = onTermsClick
                    )
                }
            }

            // ACCOUNT ACTIONS
            Text(
                text = "ACCOUNT ACTIONS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE53935),
                modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp)
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFFF5F5),
                border = BorderStroke(1.dp, Color(0xFFFFEBEB)),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Column {
                    ActionRow(
                        icon = Icons.AutoMirrored.Outlined.ExitToApp,
                        title = "Logout",
                        subtitle = "Sign out of your account",
                        color = Color(0xFFE53935),
                        onClick = onLogoutClick
                    )
                    HorizontalDivider(color = Color(0xFFFFEBEB), modifier = Modifier.padding(horizontal = 16.dp))
                    ActionRow(
                        icon = Icons.Outlined.DeleteOutline,
                        title = "Delete Account",
                        subtitle = "Permanently delete your account",
                        color = Color(0xFFE53935),
                        onClick = onDeleteAccountClick
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Version 1.0.0 • © 2026 SAVORSHELF",
                fontSize = 11.sp,
                color = textSecondary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF6F7E7A),
        modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 4.dp)
    )
}

@Composable
fun SettingsRow(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF6F7E7A), modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontSize = 15.sp, color = Color(0xFF1B2625), modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFFCBD5E1), modifier = Modifier.size(20.dp))
    }
}

@Composable
fun ActionRow(icon: ImageVector, title: String, subtitle: String, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFFFEBEB), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = color)
            Text(subtitle, fontSize = 11.sp, color = Color(0xFF6F7E7A))
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFFFCA5A5), modifier = Modifier.size(20.dp))
    }
}

@Composable
fun ChangesSuccessfulScreen(
    onComplete: () -> Unit
) {
    val backgroundUrl = "https://image2url.com/r2/default/images/1772165942529-0db3e05f-ebef-48b7-9133-fc8662d9a661.png"
    val greenColor = Color(0xFF00C853)
    val textDark = Color(0xFF1C2C39)
    val textGray = Color(0xFF6F7E7A)

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)
        onComplete()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NetworkImage(
            model = backgroundUrl,
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = Color.Black.copy(alpha = 0.1f)
                    ),
                shape = RoundedCornerShape(24.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(color = greenColor, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Check",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "SUCCESS!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textDark,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Your changes have been\nupdated successfully.\nRedirecting to settings screen...",
                        fontSize = 14.sp,
                        color = textGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Loading Dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(6.dp).background(Color(0xFF64B5F6), CircleShape))
                        Box(modifier = Modifier.size(6.dp).background(Color(0xFF4FC3F7), CircleShape))
                        Box(modifier = Modifier.size(6.dp).background(Color(0xFF29B6F6), CircleShape))
                    }
                }
            }
        }
    }
}
