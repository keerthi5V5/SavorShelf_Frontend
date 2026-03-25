package com.simats.savorshelf

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Tune
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

enum class NotificationType {
    URGENT, EXPIRING_SOON, ACTION_REQUIRED
}

data class NotificationItem(
    val id: String,
    val pantryItemId: String?,
    val title: String,
    val message: String,
    val time: String,
    var isRead: Boolean,
    val type: NotificationType
)

@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit = {},
    onProductsClick: () -> Unit = {},
    onCameraClick: () -> Unit = {},
    onFreshnessClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onCustomizeAlertsClick: () -> Unit = {}
) {
    val bgColor = Color(0xFFF6F8F7)
    val textPrimary = Color(0xFF141D1C)
    val textSecondary = Color(0xFF5A6D66) // Darker grey for better visibility

    val primaryGreen = Color(0xFF0D614E)

    var notificationList by remember { mutableStateOf<List<NotificationItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    var notificationToDelete by remember { mutableStateOf<NotificationItem?>(null) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE) }
    val userId = sharedPrefs.getInt("user_id", -1)

    LaunchedEffect(Unit) {
        if (userId != -1) {
            try {
                val response = com.simats.savorshelf.api.RetrofitClient.apiService.getNotifications(userId.toString())
                if (response.isSuccessful) {
                    val data = response.body()?.notifications ?: emptyList()
                    notificationList = data.map { apiItem ->
                        NotificationItem(
                            id = (apiItem.id ?: 0).toString(),
                            pantryItemId = (apiItem.pantryItemId ?: 0).toString(),
                            title = apiItem.title ?: "Notification",
                            message = apiItem.message ?: "",
                            time = apiItem.createdAt ?: "",
                            isRead = apiItem.isUnread == false,
                            type = when (apiItem.type) {
                                "URGENT" -> NotificationType.URGENT
                                "ACTION_REQUIRED" -> NotificationType.ACTION_REQUIRED
                                else -> NotificationType.EXPIRING_SOON
                            }
                        )
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

    val unreadCount = notificationList.count { !it.isRead }

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(
                selectedTab = 2, // No specific tab for notifications, using a placeholder
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
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
                                    text = "Notifications",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (unreadCount > 0) "$unreadCount new alerts" else "You're all caught up",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (unreadCount > 0) {
                                val scope = rememberCoroutineScope()
                                IconButton(
                                    onClick = {
                                        if (userId != -1) {
                                            scope.launch {
                                                try {
                                                    com.simats.savorshelf.api.RetrofitClient.apiService.markAllNotificationsRead(
                                                        com.simats.savorshelf.api.MarkAllNotificationsReadRequest(userId)
                                                    )
                                                    notificationList = notificationList.map { it.copy(isRead = true) }
                                                } catch (e: Exception) { e.printStackTrace() }
                                            }
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.DoneAll, "Mark all read", tint = Color.White, modifier = Modifier.size(26.dp))
                                }
                            }
                            
                            if (notificationList.isNotEmpty()) {
                                IconButton(onClick = { showDeleteAllDialog = true }) {
                                    Icon(
                                        imageVector = Icons.Outlined.DeleteSweep,
                                        contentDescription = "Clear All",
                                        tint = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Notifications List
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = primaryGreen)
                    }
                } else if (notificationList.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(bottom = 100.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = primaryGreen.copy(alpha = 0.05f),
                            modifier = Modifier.size(140.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                // Background rings
                                Box(modifier = Modifier.size(100.dp).background(primaryGreen.copy(alpha = 0.05f), CircleShape))
                                Icon(
                                    imageVector = Icons.Outlined.NotificationsNone,
                                    contentDescription = null,
                                    tint = primaryGreen.copy(alpha = 0.3f),
                                    modifier = Modifier.size(64.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(28.dp))
                        Text(
                            text = "Inbox Zero",
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            color = textPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Clean as a whistle! We'll ping you here\nwhen things need your attention.",
                            fontSize = 15.sp,
                            color = textSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 48.dp)
                        )

                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(notificationList, key = { it.id }) { notification ->
                            val scope = rememberCoroutineScope()
                            val dismissState = rememberSwipeToDismissBoxState(
                                confirmValueChange = { value ->
                                    if (value == SwipeToDismissBoxValue.EndToStart) {
                                        notificationToDelete = notification
                                        false // return false to prevent automatic dismissal, we'll handle it via state/dialog
                                    } else {
                                        false
                                    }
                                }
                            )

                            SwipeToDismissBox(
                                state = dismissState,
                                enableDismissFromStartToEnd = false,
                                backgroundContent = {
                                    val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                                        Color(0xFFE53935).copy(alpha = 0.8f)
                                    } else Color.Transparent

                                    Box(
                                        Modifier
                                            .fillMaxSize()
                                            .padding(vertical = 4.dp)
                                            .background(color, RoundedCornerShape(24.dp))
                                            .padding(horizontal = 24.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Delete,
                                            contentDescription = "Delete",
                                            tint = Color.White,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                },
                                content = {
                                    NotificationCard(
                                        notification = notification,
                                        onClick = {
                                            if (!notification.isRead) {
                                                scope.launch {
                                                    try {
                                                        val id = notification.id.toIntOrNull() ?: return@launch
                                                        val resp = com.simats.savorshelf.api.RetrofitClient.apiService.markNotificationRead(
                                                            com.simats.savorshelf.api.MarkNotificationReadRequest(id)
                                                        )
                                                        if (resp.isSuccessful) {
                                                            notificationList = notificationList.map { 
                                                                if (it.id == notification.id) it.copy(isRead = true) else it
                                                            }
                                                        }
                                                    } catch (e: Exception) {
                                                        e.printStackTrace()
                                                    }
                                                }
                                            }
                                        },
                                        onDeleteClick = { notificationToDelete = notification },
                                        onMarkReadClick = {
                                            if (!notification.isRead) {
                                                scope.launch {
                                                    try {
                                                        val id = notification.id.toIntOrNull() ?: return@launch
                                                        val resp = com.simats.savorshelf.api.RetrofitClient.apiService.markNotificationRead(
                                                            com.simats.savorshelf.api.MarkNotificationReadRequest(id)
                                                        )
                                                        if (resp.isSuccessful) {
                                                            notificationList = notificationList.map { 
                                                                if (it.id == notification.id) it.copy(isRead = true) else it
                                                            }
                                                        }
                                                    } catch (e: Exception) {
                                                        e.printStackTrace()
                                                    }
                                                }
                                            }
                                        }
                                    )
                                }
                            )
                        }

                        item {
                            NotificationSettingsCard(onCustomizeAlertsClick = onCustomizeAlertsClick)
                        }
                    }
                }
        }
    }

    // Delete Individual Context Confirmation Dialog
    if (notificationToDelete != null) {
        AlertDialog(
            onDismissRequest = { notificationToDelete = null },
            title = {
                Text(
                    text = "Delete Notification?",
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
            },
            text = {
                Text(
                    text = "Do you want to delete this notification?",
                    color = textSecondary
                )
            },
            confirmButton = {
                val scope = rememberCoroutineScope()
                Button(
                    onClick = {
                        val item = notificationToDelete
                        if (item != null) {
                            scope.launch {
                                try {
                                    val id = item.id.toIntOrNull() ?: return@launch
                                    val resp = com.simats.savorshelf.api.RetrofitClient.apiService.deleteNotification(id)
                                    if (resp.isSuccessful) {
                                        notificationList = notificationList.filter { it.id != item.id }
                                        notificationToDelete = null
                                    }
                                } catch (e: Exception) { e.printStackTrace() }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Yes", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { notificationToDelete = null }) {
                    Text("No", color = textSecondary, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Delete All Confirmation Dialog
    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = {
                Text(text = "Clear All Notifications", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(text = "This will permanently remove all your notifications. Proceed?")
            },
            confirmButton = {
                val scope = rememberCoroutineScope()
                Button(
                    onClick = {
                        if (userId != -1) {
                            scope.launch {
                                try {
                                    val resp = com.simats.savorshelf.api.RetrofitClient.apiService.deleteAllNotifications(
                                        com.simats.savorshelf.api.DeleteAllNotificationsRequest(userId)
                                    )
                                    if (resp.isSuccessful) {
                                        notificationList = emptyList()
                                        showDeleteAllDialog = false
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        } else {
                            notificationList = emptyList()
                            showDeleteAllDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Yes, Clear All", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteAllDialog = false }
                ) {
                    Text("Cancel", color = textSecondary, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun NotificationCard(
    notification: NotificationItem, 
    onClick: () -> Unit, 
    onDeleteClick: () -> Unit,
    onMarkReadClick: () -> Unit
) {
    val textPrimary = Color(0xFF141D1C)
    val textSecondary = Color(0xFF5A6D66) // Darker grey for better contrast

    val primaryGreen = Color(0xFF0D614E)

    val (iconBgColor, iconColor, icon) = when (notification.type) {
        NotificationType.URGENT, NotificationType.ACTION_REQUIRED -> Triple(
            Color(0xFFFFEDED),
            Color(0xFFE53935),
            Icons.Outlined.ReportProblem
        )
        NotificationType.EXPIRING_SOON -> Triple(
            Color(0xFFFFF4EC),
            Color(0xFFF48554),
            Icons.Outlined.Timer
        )
    }

    val cardBgColor = if (notification.isRead) Color(0xFFF9FBFA) else Color.White
    val borderStroke = if (notification.isRead) BorderStroke(1.dp, Color(0xFFE5EBE8)) else null

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = cardBgColor,
        border = borderStroke,
        shadowElevation = if (notification.isRead) 0.dp else 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = iconBgColor,
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontWeight = if (notification.isRead) FontWeight.Bold else FontWeight.Black,
                        fontSize = 16.sp,
                        color = textPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(iconColor, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = notification.message,
                    fontSize = 14.sp,
                    color = textSecondary,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium
                )


                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = textSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = notification.time,
                            fontSize = 12.sp,
                            color = textSecondary.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (!notification.isRead) {
                        Surface(
                            onClick = onMarkReadClick,
                            color = primaryGreen.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = "Mark Read",
                                    tint = primaryGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Mark Read",
                                    color = primaryGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationSettingsCard(onCustomizeAlertsClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFE9F5EF),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCustomizeAlertsClick() }
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = null,
                    tint = Color(0xFF0D614E),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Notification Settings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1B2625)
                )
                Text(
                    text = "Customize how and when you get alerted",
                    fontSize = 12.sp,
                    color = Color(0xFF5A6D66), // Use Darker grey
                    fontWeight = FontWeight.Medium
                )

            }
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF0D614E),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
