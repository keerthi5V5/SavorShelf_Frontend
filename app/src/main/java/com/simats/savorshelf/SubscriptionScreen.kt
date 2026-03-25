package com.simats.savorshelf

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SubscriptionScreen(
    onBackClick: () -> Unit = {},
    onStartPremiumClick: () -> Unit = {}
) {
    // Premium Color Palette
    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0F172A), // Deep Navy
            Color(0xFF020617)  // Deeper Navy/Black
        )
    )
    val cardBg = Color(0xFF1E293B).copy(alpha = 0.7f)
    val goldColor = Color(0xFFFFD700)
    val lightBlue = Color(0xFF38BDF8)
    val greenCheck = Color(0xFF22C55E)
    
    val logoUrl = "https://image2url.com/r2/default/images/1772164873232-c166ef95-c445-4f7d-9852-4f48d008db1c.png"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo Section
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = Color.White.copy(alpha = 0.1f)
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(12.dp)
            ) {
                NetworkImage(
                    model = logoUrl,
                    contentDescription = "SavorShelf Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = "SavorShelf",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Premium Badge
            Surface(
                color = Color.Transparent,
                shape = RoundedCornerShape(4.dp),
                border = null
            ) {
                Text(
                    text = "PREMIUM",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = goldColor,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Description
            Text(
                text = "Unlock smart features designed to track\nexpiry and reduce food waste effectively",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Features Column
            FeatureItem(
                icon = Icons.Default.FlashOn,
                iconTint = goldColor,
                title = "AI Expiry Scanning",
                subtitle = "Automated date extraction from labels"
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            FeatureItem(
                icon = Icons.Default.NotificationsActive,
                iconTint = Color(0xFFF43F5E), // Rose color
                title = "Smart Freshness Alerts",
                subtitle = "Real-time notifications before items expire"
            )

            Spacer(modifier = Modifier.height(16.dp))

            FeatureItem(
                icon = Icons.Default.Psychology,
                iconTint = lightBlue,
                title = "Insights & Analytics",
                subtitle = "Track your waste reduction impact"
            )

            Spacer(modifier = Modifier.height(16.dp))

            FeatureItem(
                icon = Icons.Default.Diamond,
                iconTint = Color(0xFFA855F7), // Purple
                title = "Unlimited Storage",
                subtitle = "Manage thousands of items hassle-free"
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(40.dp))

            // CTA Button
            Button(
                onClick = onStartPremiumClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(32.dp),
                        spotColor = Color.White.copy(alpha = 0.2f)
                    ),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(32.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "START PREMIUM",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Arrow",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer Links
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "By continuing, you agree to our ",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.4f)
                )
                Text(
                    text = "Terms & Privacy Policy",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { /* Add navigation */ }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Dismiss
            Text(
                text = "Maybe later",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.5f),
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clickable { onBackClick() }
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun FeatureItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        color = Color(0xFF1E293B).copy(alpha = 0.4f),
        shape = RoundedCornerShape(20.dp),
        border = null
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconTint.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Medium
                )
            }

            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Included",
                tint = Color(0xFF22C55E),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
