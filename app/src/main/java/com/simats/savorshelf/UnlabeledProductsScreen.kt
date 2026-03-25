package com.simats.savorshelf

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnlabeledProductsScreen(
    onBackClick: () -> Unit,
    onCategoryClick: (String) -> Unit
) {
    val bgColor = Color(0xFFF0F3EF) 
    val textPrimary = Color(0xFF141D1C)
    val textSecondary = Color(0xFF5A6D66) // Darker grey for better visibility

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
                    Text(
                        text = "Select Product Type",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "What's in your bag?",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textPrimary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Choose a category to organize your shelf.",
                fontSize = 14.sp,
                color = textSecondary,
                fontWeight = FontWeight.Medium
            )

            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Grid of categories
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Row 1
                Row(
                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        CategoryCard(
                            title = "Fruits",
                            icon = Icons.Outlined.LocalFlorist,
                            iconColor = primaryGreen,
                            imageUrl = "https://images.unsplash.com/photo-1610832958506-aa56368176cf?w=400&h=400&fit=crop",
                            onClick = { onCategoryClick("Fruits") }
                        )
                    }
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        CategoryCard(
                            title = "Vegetables",
                            icon = Icons.Outlined.Eco,
                            iconColor = primaryGreen,
                            imageUrl = "https://images.unsplash.com/photo-1557844352-761f2565b576?w=400&h=400&fit=crop", // Assorted vegetables
                            onClick = { onCategoryClick("Vegetables") }
                        )
                    }
                }
                
                // Row 2
                Row(
                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        CategoryCard(
                            title = "Leafy Greens",
                            icon = Icons.Outlined.Spa,
                            iconColor = primaryGreen,
                            imageUrl = "https://images.pexels.com/photos/2733918/pexels-photo-2733918.jpeg?auto=compress&cs=tinysrgb&w=400&h=400&fit=crop", // Spinach / Leafy Greens
                            onClick = { onCategoryClick("Leafy Greens") }
                        )
                    }
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        CategoryCard(
                            title = "Meat & Seafood",
                            icon = Icons.Outlined.SetMeal,
                            iconColor = primaryGreen,
                            imageUrl = "https://images.unsplash.com/photo-1607623814075-e51df1bdc82f?w=400&h=400&fit=crop",
                            onClick = { onCategoryClick("Meat & Seafood") }
                        )
                    }
                }
                
                // Row 3
                Row(
                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        CategoryCard(
                            title = "Dairy",
                            icon = Icons.Outlined.LocalDrink,
                            iconColor = primaryGreen,
                            imageUrl = "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=400&h=400&fit=crop", // Clean dairy milk
                            onClick = { onCategoryClick("Dairy") }
                        )
                    }
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        CategoryCard(
                            title = "Herbs & Seasonings",
                            icon = Icons.Outlined.LocalDining,
                            iconColor = primaryGreen,
                            imageUrl = "https://images.unsplash.com/photo-1596040033229-a9821ebd058d?w=400&h=400&fit=crop",
                            onClick = { onCategoryClick("Herbs & Seasonings") }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun CategoryCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    imageUrl: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NetworkImage(
                model = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1B2625),
                textAlign = TextAlign.Center
            )
        }
    }
}

