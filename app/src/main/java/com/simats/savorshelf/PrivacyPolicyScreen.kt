package com.simats.savorshelf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit
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
                            text = "Privacy Policy",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Last updated: Feb 18, 2026",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
            
            // Introduction
            PolicyCard(title = "Introduction") {
                Text(
                    "Welcome to SAVORSHELF. We are committed to protecting your personal information and your right to privacy. This Privacy Policy explains what information we collect, how we use it, and what rights you have in relation to it.",
                    fontSize = 12.sp,
                    color = textSecondary,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Information We Collect
            PolicyCard(title = "Information We Collect") {
                Text("Personal Information", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text("We collect information you provide directly to us, including:", fontSize = 12.sp, color = textSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                BulletList(listOf("Name and contact information", "Account credentials", "Profile information"))
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text("Product Information", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text("When you use SAVORSHELF, we collect:", fontSize = 12.sp, color = textSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                BulletList(listOf("Product images and expiry dates", "Purchase and storage information", "Product categorization data"))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // How We Use Your Information
            PolicyCard(title = "How We Use Your Information") {
                Text("We use the information we collect to:", fontSize = 12.sp, color = textSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                BulletList(listOf(
                    "Provide and maintain our services",
                    "Send expiry notifications and alerts",
                    "Improve our AI detection algorithms",
                    "Analyze usage patterns and optimize features",
                    "Communicate important updates",
                    "Ensure security and prevent fraud"
                ))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Data Storage & Security
            PolicyCard(title = "Data Storage & Security") {
                Text(
                    "We implement appropriate security measures to protect your personal information. Your data is stored locally on your device and is not transmitted to external servers unless you explicitly enable cloud sync.",
                    fontSize = 12.sp,
                    color = textSecondary,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE9F5EF),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                        Text("🔒", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Your product images and data remain private and secure on your device.",
                            fontSize = 11.sp,
                            color = primaryGreen,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Your Rights
            PolicyCard(title = "Your Rights") {
                Text("You have the right to:", fontSize = 12.sp, color = textSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                BulletList(listOf(
                    "Access your personal data",
                    "Correct inaccurate information",
                    "Delete your account and data",
                    "Export your data",
                    "Opt-out of notifications",
                    "Withdraw consent at any time"
                ))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Third-Party Services
            PolicyCard(title = "Third-Party Services") {
                Text(
                    "SAVORSHELF uses AI-powered OCR technology to detect expiry dates. Images processed for OCR are temporarily analyzed and immediately deleted. We do not share your data with third parties for marketing purposes.",
                    fontSize = 12.sp,
                    color = textSecondary,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Changes to This Policy
            PolicyCard(title = "Changes to This Policy") {
                Text(
                    "We may update this Privacy Policy from time to time. We will notify you of any changes by posting the new Privacy Policy on this page and updating the \"Last updated\" date.",
                    fontSize = 12.sp,
                    color = textSecondary,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contact Us (Green Card)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFE9F5EF),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Contact Us", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "If you have any questions about this Privacy Policy, please contact us:",
                        fontSize = 12.sp,
                        color = textSecondary,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = textPrimary)) {
                                append("Email: ")
                            }
                            withStyle(style = SpanStyle(color = textSecondary)) {
                                append("savorshelf1.0@gmail.com")
                            }
                        },
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = textPrimary)) {
                                append("Website: ")
                            }
                            withStyle(style = SpanStyle(color = textSecondary)) {
                                append("www.savorshelf.com/privacy")
                            }
                        },
                        fontSize = 12.sp
                    )
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

@Composable
internal fun PolicyCard(title: String, content: @Composable () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B2625))
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun BulletList(items: List<String>) {
    Column {
        items.forEach { item ->
            Row(modifier = Modifier.padding(bottom = 6.dp), verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.padding(top = 6.dp, end = 8.dp)) {
                    Box(modifier = Modifier.size(4.dp).background(Color(0xFF6F7E7A), CircleShape))
                }
                Text(item, fontSize = 12.sp, color = Color(0xFF6F7E7A), lineHeight = 16.sp)
            }
        }
    }
}
