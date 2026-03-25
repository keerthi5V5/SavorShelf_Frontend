package com.simats.savorshelf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Description
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
fun TermsAndConditionsScreen(
    onBackClick: () -> Unit
) {
    val bgColor = Color(0xFFE9F0EC)
    val textPrimary = Color(0xFF1B2625)
    val textSecondary = Color(0xFF6F7E7A)
    val warningBg = Color(0xFFFFF7ED)
    val warningText = Color(0xFFC05621) // Or E26027

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
                        Color(0xFF0D614E),
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
                            text = "Terms & Conditions",
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
            
            // Agreement to Terms
            TermsCard(title = "Agreement to Terms") {
                Text(
                    "By accessing and using SAVORSHELF (\"the App\"), you agree to be bound by these Terms and Conditions. If you do not agree to these terms, please do not use the App.",
                    fontSize = 12.sp,
                    color = textSecondary,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Use of Service
            TermsCard(title = "Use of Service") {
                Text("SAVORSHELF provides AI-powered expiry detection and freshness estimation services. By using the App, you agree to:", fontSize = 12.sp, color = textSecondary, lineHeight = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                BulletList(listOf(
                    "Provide accurate information when using the service",
                    "Use the App only for lawful purposes",
                    "Not attempt to interfere with the App's functionality",
                    "Not use the App to store or share inappropriate content",
                    "Maintain the security of your account credentials"
                ))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI-Powered Features
            TermsCard(title = "AI-Powered Features") {
                Text("SAVORSHELF uses artificial intelligence for OCR and expiry detection. You acknowledge that:", fontSize = 12.sp, color = textSecondary, lineHeight = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                BulletList(listOf(
                    "AI detection may not be 100% accurate",
                    "You should verify detected expiry dates manually",
                    "The App provides estimates, not guarantees",
                    "Food safety decisions remain your responsibility"
                ))
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = warningBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFEDD5)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                        Text("⚠️", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Always verify expiry dates and use your judgment regarding food safety.",
                            fontSize = 11.sp,
                            color = warningText,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Accounts
            TermsCard(title = "User Accounts") {
                Text("Account responsibilities:", fontSize = 12.sp, color = textSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                BulletList(listOf(
                    "You are responsible for maintaining account security",
                    "You must keep your password confidential",
                    "You are liable for all activities under your account",
                    "Notify us immediately of unauthorized access",
                    "One account per user is permitted"
                ))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Intellectual Property
            TermsCard(title = "Intellectual Property") {
                Text(
                    "All content, features, and functionality of SAVORSHELF, including but not limited to text, graphics, logos, icons, images, and software, are the exclusive property of SAVORSHELF and are protected by copyright and trademark laws.",
                    fontSize = 12.sp,
                    color = textSecondary,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Your product images and data remain your property. By using the App, you grant SAVORSHELF a limited license to process your images for OCR and expiry detection purposes only.",
                    fontSize = 12.sp,
                    color = textSecondary,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Limitation of Liability
            TermsCard(title = "Limitation of Liability") {
                Text("SAVORSHELF is provided \"as is\" without warranties of any kind. We shall not be liable for:", fontSize = 12.sp, color = textSecondary, lineHeight = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                BulletList(listOf(
                    "Food spoilage or illness resulting from App usage",
                    "Inaccurate expiry date detection",
                    "Loss of data or product information",
                    "Service interruptions or technical issues",
                    "Indirect or consequential damages"
                ))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Data and Privacy
            TermsCard(title = "Data and Privacy") {
                Text(
                    "Your use of SAVORSHELF is also governed by our Privacy Policy. Please review our Privacy Policy to understand our data practices. By using the App, you consent to the collection and use of information as outlined in the Privacy Policy.",
                    fontSize = 12.sp,
                    color = textSecondary,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Termination
            TermsCard(title = "Termination") {
                Text(
                    "We reserve the right to suspend or terminate your access to SAVORSHELF at any time, with or without cause or notice. You may also delete your account at any time through the App settings. Upon termination, all provisions of these Terms that should survive termination shall remain in effect.",
                    fontSize = 12.sp,
                    color = textSecondary,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Changes to Terms
            TermsCard(title = "Changes to Terms") {
                Text(
                    "We reserve the right to modify these Terms and Conditions at any time. Changes will be effective immediately upon posting. Your continued use of the App after changes are posted constitutes acceptance of the modified terms.",
                    fontSize = 12.sp,
                    color = textSecondary,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Governing Law
            TermsCard(title = "Governing Law") {
                Text(
                    "These Terms and Conditions shall be governed by and construed in accordance with applicable laws. Any disputes arising from these terms shall be resolved through binding arbitration.",
                    fontSize = 12.sp,
                    color = textSecondary,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contact Us (Orange Card)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = warningBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Contact Us", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "If you have questions about these Terms and Conditions, contact us:",
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
                                append("www.savorshelf.com/terms")
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
internal fun TermsCard(title: String, content: @Composable () -> Unit) {
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
                Text("•", fontSize = 12.sp, color = Color(0xFF6F7E7A), modifier = Modifier.padding(end = 6.dp))
                Text(item, fontSize = 12.sp, color = Color(0xFF6F7E7A), lineHeight = 16.sp)
            }
        }
    }
}
