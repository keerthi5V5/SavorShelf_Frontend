package com.simats.savorshelf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CameraPermissionScreen(
    onAllowClick: () -> Unit,
    onDenyClick: () -> Unit
) {
    PermissionScreenTemplate(
        icon = Icons.Outlined.CameraAlt,
        title = "Permission Required",
        description = "SavorShelf needs camera access to scan products and expiry dates.",
        onAllowClick = onAllowClick,
        onDenyClick = onDenyClick
    )
}

@Composable
fun GalleryPermissionScreen(
    onAllowClick: () -> Unit,
    onDenyClick: () -> Unit
) {
    PermissionScreenTemplate(
        icon = Icons.Outlined.PhotoLibrary,
        title = "Confirm Upload",
        description = "Do you want to share images from your gallery to SavorShelf?",
        onAllowClick = onAllowClick,
        onDenyClick = onDenyClick
    )
}

@Composable
fun PermissionScreenTemplate(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onAllowClick: () -> Unit,
    onDenyClick: () -> Unit
) {
    val bgColor = Color(0xFFE9F0EC)
    val textPrimary = Color(0xFF141D1C)
    val textSecondary = Color(0xFF5A6D66) // Darker grey for better visibility

    val primaryGreen = Color(0xFF0D614E)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFFE9F0EC), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = primaryGreen,
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = description,
                    fontSize = 15.sp,
                    color = textSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = onAllowClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Allow", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                    onClick = onDenyClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Deny", fontSize = 16.sp, color = textSecondary, fontWeight = FontWeight.SemiBold)

                }
            }
        }
    }
}
