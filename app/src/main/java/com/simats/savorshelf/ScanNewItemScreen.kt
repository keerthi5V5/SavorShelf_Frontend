package com.simats.savorshelf

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanNewItemScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onProductsClick: () -> Unit = {},
    onCameraClick: () -> Unit = {},
    onFreshnessClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLabeledProductClick: () -> Unit = {},
    onUnlabeledProductClick: () -> Unit = {}
) {
    val bgColor = Color(0xFFF1F5F3) 
    val textPrimary = Color(0xFF1B2625)
    val textSecondary = Color(0xFF6F7E7A)

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(
                selectedTab = 2,
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
                    Text(
                        text = "Scan New Item",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "Choose Scan Type",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Select how you want to add your item",
                    fontSize = 14.sp,
                    color = textSecondary
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                ScanTypeCard(
                    title = "Labeled Product",
                    description = "Scan barcodes, dates, or labels",
                    badgeText = "AI-powered OCR detection",
                    badgeColor = Color(0xFF3B68E9),
                    badgeBgColor = Color(0xFFE8F0FE),
                    perfectForText = "Perfect for: Milk, Snacks,\nMedicines, Canned Goods,\nPackaged Foods",
                    icon = Icons.Outlined.QrCodeScanner,
                    iconTint = Color(0xFF3B68E9),
                    iconBgColor = Color(0xFFE8F0FE),
                    onClick = onLabeledProductClick
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                ScanTypeCard(
                    title = "Unlabeled Products",
                    description = "Add fresh produce manually",
                    badgeText = "Smart freshness estimation",
                    badgeColor = Color(0xFF0D614E),
                    badgeBgColor = Color(0xFFE9F5EF),
                    perfectForText = "Perfect for: Fruits, Vegetables,\nHerbs & Seasonings, Fresh Produce",
                    icon = Icons.Outlined.Eco,
                    iconTint = Color(0xFF0D614E),
                    iconBgColor = Color(0xFFE9F5EF),
                    onClick = onUnlabeledProductClick
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF1F6FB),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Smartphone,
                                contentDescription = "Tips",
                                tint = textPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Scanning Tips:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = textPrimary
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        val tips = listOf(
                            "Ensure good lighting for better detection",
                            "Hold camera steady over expiry date",
                            "Clean the lens for clearer scans",
                            "Try different angles if scan fails"
                        )
                        
                        tips.forEach { tip ->
                            Row(modifier = Modifier.padding(bottom = 6.dp)) {
                                Text(
                                    text = "• ",
                                    color = textSecondary,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = tip,
                                    color = textSecondary,
                                    fontSize = 13.sp
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
fun ScanTypeCard(
    title: String,
    description: String,
    badgeText: String,
    badgeColor: Color,
    badgeBgColor: Color,
    perfectForText: String,
    icon: ImageVector,
    iconTint: Color,
    iconBgColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1B2625)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = Color(0xFF6F7E7A)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Box(
                    modifier = Modifier
                        .background(badgeBgColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = badgeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = perfectForText,
                    fontSize = 11.sp,
                    color = Color(0xFF6F7E7A),
                    lineHeight = 16.sp
                )
            }
        }
    }
}
