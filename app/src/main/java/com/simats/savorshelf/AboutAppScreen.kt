package com.simats.savorshelf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutAppScreen(
    onBackClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onTermsClick: () -> Unit
) {
    val bgColor = Color(0xFFE9F0EC)
    val textPrimary = Color(0xFF1B2625)
    val textSecondary = Color(0xFF6F7E7A)
    val primaryGreen = Color(0xFF0D614E)

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
                            text = "About SAVORSHELF",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Version 1.0.0",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
            // Header Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF0D614E), Color(0xFF2A7C52))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(painter = painterResource(id = R.drawable.app_logo), contentDescription = null, modifier = Modifier.size(56.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("SAVORSHELF", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Smart Expiry Detection & Freshness Tracking",
                        fontSize = 12.sp,
                        color = Color(0xFFE9F5EF),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0x33FFFFFF))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Reduce waste. Save money. Live sustainably.",
                        fontSize = 11.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Our Mission
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Our Mission", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "SAVORSHELF is dedicated to helping households reduce food waste through intelligent expiry tracking and freshness monitoring. Our AI-powered technology makes it easy to manage consumable items, ensuring nothing goes to waste while keeping your family safe and healthy.",
                        fontSize = 12.sp,
                        color = textSecondary,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Key Features
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Key Features", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    FeatureRow(
                        icon = Icons.Outlined.CameraAlt,
                        iconBgColor = Color(0xFFE8EFFF),
                        iconColor = Color(0xFF4C4DDC),
                        title = "AI-Powered OCR Detection",
                        description = "Automatically detect expiry dates from product labels using advanced image recognition technology."
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    FeatureRow(
                        icon = Icons.Outlined.AutoAwesome,
                        iconBgColor = Color(0xFFE9F5EF),
                        iconColor = primaryGreen,
                        title = "Freshness Estimation",
                        description = "Smart algorithms calculate freshness levels based on purchase date, expiry, and storage conditions."
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    FeatureRow(
                        icon = Icons.Outlined.NotificationsActive,
                        iconBgColor = Color(0xFFFFF7ED),
                        iconColor = Color(0xFFE26027),
                        title = "Smart Notifications",
                        description = "Receive timely alerts before products expire, helping you use items at peak freshness."
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    FeatureRow(
                        icon = Icons.Outlined.Shield,
                        iconBgColor = Color(0xFFF3E8FF),
                        iconColor = Color(0xFF9333EA),
                        title = "Privacy First",
                        description = "Your data stays on your device. We prioritize your privacy and security above all else."
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // How It Works
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("How It Works", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    Spacer(modifier = Modifier.height(16.dp))

                    HowItWorksRow(step = "1", title = "Scan Product", description = "Take a photo of your product label and expiry date.")
                    HowItWorksRow(step = "2", title = "AI Detection", description = "Our AI automatically reads and extracts the expiry information.")
                    HowItWorksRow(step = "3", title = "Track & Monitor", description = "View freshness levels and receive timely notifications.")
                    HowItWorksRow(step = "4", title = "Reduce Waste", description = "Use products before they expire and save money.", showLine = false)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Technology
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Technology", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "SAVORSHELF is built with cutting-edge technology:",
                        fontSize = 12.sp,
                        color = textSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        TechCard(title = "AI & Machine\nLearning", subtitle = "OCR & Detection", modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(12.dp))
                        TechCard(title = "Computer Vision", subtitle = "Image Recognition", modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        TechCard(title = "Smart Algorithms", subtitle = "Freshness Estimation", modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(12.dp))
                        TechCard(title = "Cloud-Free", subtitle = "Privacy First", modifier = Modifier.weight(1f))
                    }
                }
            }

            // Get in Touch
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Get in Touch", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Have feedback or suggestions? We'd love to hear from you!",
                        fontSize = 12.sp,
                        color = textSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    ContactRow(label = "Support:", value = "savorshelf1.0@gmail.com")
                    Spacer(modifier = Modifier.height(8.dp))
                    ContactRow(label = "Website:", value = "www.savorshelf.com")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Privacy & Terms Row
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onPrivacyPolicyClick) {
                        Text("Privacy Policy", fontSize = 11.sp, color = primaryGreen)
                    }
                    Text("|", fontSize = 11.sp, color = Color(0xFFE5EBE8), modifier = Modifier.padding(horizontal = 8.dp))
                    TextButton(onClick = onTermsClick) {
                        Text("Terms & Conditions", fontSize = 11.sp, color = primaryGreen)
                    }
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
            Text(
                text = "Made with ❤️ for a sustainable future",
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

@Composable
fun FeatureRow(icon: androidx.compose.ui.graphics.vector.ImageVector, iconBgColor: Color, iconColor: Color, title: String, description: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(iconBgColor, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B2625))
            Spacer(modifier = Modifier.height(4.dp))
            Text(description, fontSize = 11.sp, color = Color(0xFF6F7E7A), lineHeight = 16.sp)
        }
    }
}

@Composable
fun HowItWorksRow(step: String, title: String, description: String, showLine: Boolean = true) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Color(0xFF0D614E), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(step, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            if (showLine) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .padding(vertical = 4.dp)
                        .background(Color(0xFFE9F5EF))
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.padding(bottom = if (showLine) 16.dp else 0.dp)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B2625))
            Spacer(modifier = Modifier.height(4.dp))
            Text(description, fontSize = 11.sp, color = Color(0xFF6F7E7A))
        }
    }
}

@Composable
fun TechCard(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFAFAFA),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B2625))
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, fontSize = 10.sp, color = Color(0xFF6F7E7A))
        }
    }
}

@Composable
fun ImpactItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D614E))
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 11.sp, color = Color(0xFF6F7E7A), textAlign = TextAlign.Center, lineHeight = 14.sp)
    }
}

@Composable
fun ContactRow(label: String, value: String) {
    Text(
        buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF1B2625))) {
                append("$label ")
            }
            withStyle(style = SpanStyle(color = Color(0xFF6F7E7A))) {
                append(value)
            }
        },
        fontSize = 12.sp
    )
}
