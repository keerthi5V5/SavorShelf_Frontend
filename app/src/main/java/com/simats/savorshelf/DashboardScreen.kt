package com.simats.savorshelf

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import android.content.Context

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    unreadNotificationCount: Int = 0,
    onProductClick: (String?) -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onViewAllClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onProductsClick: () -> Unit = {},
    onCameraClick: () -> Unit = {},
    onFreshnessClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onPremiumUpgradeClick: () -> Unit = {},
    onPackagedLabeledClick: () -> Unit = {},
    onFreshUnlabeledClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val userName = sharedPrefs.getString("user_name", "User") ?: "User"
    val userId = sharedPrefs.getInt("user_id", -1)

    var dashboardFreshCount by remember { mutableIntStateOf(0) }
    var dashboardUseSoonCount by remember { mutableIntStateOf(0) }
    var dashboardExpiredCount by remember { mutableIntStateOf(0) }
    var dashboardTip by remember { mutableStateOf("Store herbs upright in water like flowers to keep them fresh longer! \uD83C\uDF3F") }
    var dashboardRecentItems by remember { mutableStateOf<List<com.simats.savorshelf.api.DashboardRecentItem>>(emptyList()) }
    var localUnreadCount by remember { mutableIntStateOf(unreadNotificationCount) }

    suspend fun fetchNotifications() {
        if (userId == -1) return
        try {
            val notifResponse = com.simats.savorshelf.api.RetrofitClient.apiService.getNotifications(userId.toString())
            if (notifResponse.isSuccessful) {
                val notifs = notifResponse.body()?.notifications ?: emptyList()
                val unread = notifs.filter { it.isUnread == true }
                localUnreadCount = unread.size
                
                // Show system notifications for ones not yet seen on this device
                val lastNotifiedId = sharedPrefs.getInt("last_notified_notif_id", -1)
                val newNotifs = unread.filter { (it.id ?: -1) > lastNotifiedId }
                if (newNotifs.isNotEmpty()) {
                    newNotifs.sortedBy { it.id ?: -1 }.takeLast(3).forEach { notif ->
                        NotificationHelper.showNotification(context, notif.title ?: "Alert", notif.message ?: "", notif.id ?: 1001)
                    }
                    val maxId = newNotifs.maxOfOrNull { it.id ?: -1 } ?: lastNotifiedId
                    sharedPrefs.edit().putInt("last_notified_notif_id", maxId).apply()
                }
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    LaunchedEffect(Unit) {
        if (userId != -1) {
            try {
                // Fetch Dashboard Content once
                val response = com.simats.savorshelf.api.RetrofitClient.apiService.getDashboard(userId.toString())
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        body.summary?.let {
                            dashboardFreshCount = it.fresh
                            dashboardUseSoonCount = it.useSoon
                            dashboardExpiredCount = it.expired
                        }
                        dashboardTip = body.dailyTip ?: dashboardTip
                        body.recentItems?.let { items ->
                            dashboardRecentItems = items
                        }
                    }
                }
            } catch (e: Exception) { e.printStackTrace() }
            
            // Loop for frequent notification updates (every 30 seconds)
            while(true) {
                fetchNotifications()
                kotlinx.coroutines.delay(10000)
            }
        }
    }

    val bgColor = com.simats.savorshelf.ui.theme.MintBackground
    val primaryGreen = Color(0xFF0D614E)
    val textPrimary = Color(0xFF141D1C)
    val textSecondary = Color(0xFF5A6D66) // Darker grey for better visibility

    
    val packagedUrl = "https://image2url.com/r2/default/images/1772091005337-5389eaca-6ec9-425b-b4b3-b15ce30751bb.png"
    val freshUrl = "https://image2url.com/r2/default/images/1772091245372-fc42fb44-c713-469e-8368-7c12b981b14a.png"

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(
                selectedTab = 0,
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
            // Premium Branding Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        primaryGreen,
                        RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
                    .statusBarsPadding()
            ) {
                Column(modifier = Modifier.padding(bottom = 28.dp)) {
                    // Top Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White)
                                    .clickable { /* Logo action */ }, 
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.app_logo),
                                    contentDescription = "Logo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "SavorShelf",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "Smart Pantry, Zero Waste",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        
                        // Notification Icon
                        Box(contentAlignment = Alignment.TopEnd) {
                            IconButton(
                                onClick = onNotificationClick,
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
                                    .size(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            // Notification badge - Refined for better visibility
                            if (localUnreadCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp) // Slightly larger
                                        .align(Alignment.TopEnd)
                                        .offset(x = 6.dp, y = (-6).dp)
                                        .background(Color(0xFFF44336), CircleShape)
                                        .border(1.5.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val countText = if (localUnreadCount > 9) "9+" else localUnreadCount.toString()
                                    Text(
                                        text = countText,
                                        color = Color.White,
                                        fontSize = 10.sp, // Larger font
                                        fontWeight = FontWeight.Bold, // Bold for visibility
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.offset(y = (-1).dp), // Sub-pixel adjustment for better centering
                                        style = TextStyle(
                                            platformStyle = PlatformTextStyle(
                                                includeFontPadding = false
                                            )
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Hello, $userName!",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Ready to manage your pantry?",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.85f), // Slightly brighter for visibility
                            fontWeight = FontWeight.Medium
                        )

                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            


            // Your Pantry at a Glance Box
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "YOUR PANTRY AT A GLANCE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PantrySquareCard(
                            count = dashboardExpiredCount.toString(),
                            label = "Expired",
                            icon = null,
                            color = Color(0xFFE53935),
                            bgColor = Color(0xFFFFEBEE),
                            modifier = Modifier.weight(1f)
                        )
                        PantrySquareCard(
                            count = dashboardUseSoonCount.toString(),
                            label = "Use Soon",
                            icon = null,
                            color = Color(0xFFFF9800),
                            bgColor = Color(0xFFFFF3E0),
                            modifier = Modifier.weight(1f)
                        )
                        PantrySquareCard(
                            count = dashboardFreshCount.toString(),
                            label = "Fresh",
                            icon = null,
                            color = Color(0xFF4CAF50),
                            bgColor = Color(0xFFE8F5E9),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            
            // Quick Scan Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📸 Quick Scan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp)
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onPackagedLabeledClick() }) {
                    TrackingCard(
                        title = "Packaged & Labeled",
                        subtitle = "Scan expiry dates on products",
                        imageUrl = packagedUrl
                    )
                }
                Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onFreshUnlabeledClick() }) {
                    TrackingCard(
                        title = "Fresh & Unlabeled",
                        subtitle = "Track fruits, vegetables, and items without dates. We'll estimate freshness",
                        imageUrl = freshUrl
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // Daily Tip Box
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFFEDD5),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .border(1.dp, Color(0xFFFFDAB9), RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFF97316), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lightbulb,
                            contentDescription = "Tip",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.padding(top = 2.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "💡 Today's Freshness Tip ✨",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E3A8A),
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = dashboardTip,
                            fontSize = 13.sp,
                            color = Color(0xFF334155),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Items",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                Text(
                    text = "View All",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE26027),
                    modifier = Modifier.clickable { onViewAllClick() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (dashboardRecentItems.isEmpty()) {
                    Text(
                        text = "No recent items found on your shelf.",
                        color = textSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                } else {
                    dashboardRecentItems.take(3).forEach { product ->
                        RecentItemCard(
                            name = product.name,
                            addedTime = product.addedTime,
                            statusLabel = product.statusLabel,
                            statusValue = product.statusValue,
                            statusColor = when (product.freshnessLabel) {
                                "Expired" -> Color(0xFFE53935)
                                "Use Soon" -> Color(0xFFFF9800)
                                else -> Color(0xFF4CAF50)
                            },
                            imageUrl = product.imageUrl,
                            onClick = { onProductClick(product.id) }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun TrackingCard(title: String, subtitle: String, imageUrl: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NetworkImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFF475569), // Slate 600 - Good contrast on white
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

@Composable
fun RecentItemCard(
    name: String, 
    addedTime: String, 
    statusLabel: String, 
    statusValue: String, 
    statusColor: Color,
    imageUrl: String? = null,
    onClick: () -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (imageUrl != null) {
                NetworkImage(
                    model = imageUrl,
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE5EBE8))
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1B2625)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = addedTime,
                    fontSize = 12.sp,
                    color = Color(0xFF5A6D66), // Use Darker grey
                    fontWeight = FontWeight.Medium
                )

            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = statusLabel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = statusColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = statusValue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF1B2625)
                )
            }
        }
    }
}

@Composable
fun PantrySquareCard(
    count: String?,
    label: String,
    icon: ImageVector?,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(color = bgColor, shape = RoundedCornerShape(16.dp))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (count != null) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = color,
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = count,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp
                    )
                }
            }
        } else if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

@Composable
fun CustomBottomNavigation(
    selectedTab: Int = 0,
    onHomeClick: () -> Unit,
    onProductsClick: () -> Unit,
    onCameraClick: () -> Unit,
    onFreshnessClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val primaryGreen = Color(0xFF0D614E)
    val lightGreenBg = Color(0xFFE9F5EF) // Matches the app's light success bg
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Main Navigation Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 16.dp, spotColor = Color.Black.copy(alpha = 0.1f)),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home,
                    label = "Home",
                    isSelected = selectedTab == 0,
                    onClick = onHomeClick
                )
                BottomNavItem(
                    selectedIcon = Icons.Filled.Kitchen, // Fridge icon - very apt for pantry
                    unselectedIcon = Icons.Outlined.Kitchen,
                    label = "Products",
                    isSelected = selectedTab == 1,
                    onClick = onProductsClick
                )
                
                // Gap for the floating scan button
                Spacer(modifier = Modifier.width(60.dp))
                
                BottomNavItem(
                    selectedIcon = Icons.Filled.AvTimer, // Gauge icon - perfect for "Remaining Freshness"
                    unselectedIcon = Icons.Outlined.AvTimer,
                    label = "Freshness",
                    isSelected = selectedTab == 3,
                    onClick = onFreshnessClick
                )
                BottomNavItem(
                    selectedIcon = Icons.Filled.Tune, // Distinct slider icon for settings
                    unselectedIcon = Icons.Outlined.Tune,
                    label = "Settings",
                    isSelected = selectedTab == 4,
                    onClick = onSettingsClick
                )
            }
        }
        
        // Circular Scan Button
        Surface(
            modifier = Modifier
                .offset(y = (-24).dp)
                .size(68.dp)
                .clickable { onCameraClick() },
            shape = CircleShape,
            color = Color(0xFFB9E8CE), // Matching your app's secondary green
            shadowElevation = 10.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.CameraEnhance, // Professional "Smart Scan" icon
                    contentDescription = "Scan",
                    tint = Color(0xFF0D614E),
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Composable
fun BottomNavItem(
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val activeColor = Color(0xFF0D614E)
    val inactiveColor = Color(0xFF94A3B8)
    val bgColor = if (isSelected) Color(0xFFE9F5EF) else Color.Transparent
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = if (isSelected) selectedIcon else unselectedIcon,
            contentDescription = label,
            tint = if (isSelected) activeColor else inactiveColor,
            modifier = Modifier.size(24.dp)
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = activeColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
