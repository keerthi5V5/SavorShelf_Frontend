package com.simats.savorshelf

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun LogoutConfirmationScreen(
    onCancelClick: () -> Unit,
    onLogoutConfirm: () -> Unit
) {
    val backgroundUrl = "https://image2url.com/r2/default/images/1772165942529-0db3e05f-ebef-48b7-9133-fc8662d9a661.png"
    val redColor = Color(0xFFEE1122)
    val lightRedBg = Color(0xFFFFF4F4)
    val borderRed = Color(0xFFFFCCCC)
    val textDark = Color(0xFF1C2C39)

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
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Red Logout Icon
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(color = redColor, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Logout,
                            contentDescription = "Logout",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "LOGOUT\nCONFIRMATION",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textDark,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Warning Box
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = lightRedBg,
                        border = BorderStroke(1.dp, borderRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ErrorOutline,
                                contentDescription = "Warning",
                                tint = redColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Are you sure you want to logout?\nYou will need to sign in again to\naccess your dashboard.",
                                color = Color(0xFF993333), // Slightly darker for better contrast
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight.Medium
                            )

                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = onCancelClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFFD0D0D0))
                        ) {
                            Text(
                                "CANCEL",
                                color = Color(0xFF506060),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Button(
                            onClick = onLogoutConfirm,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = redColor
                            )
                        ) {
                            Text(
                                "LOGOUT",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LogoutSuccessScreen(
    onComplete: () -> Unit
) {
    val backgroundUrl = "https://image2url.com/r2/default/images/1772165942529-0db3e05f-ebef-48b7-9133-fc8662d9a661.png"
    val greenColor = Color(0xFF00C853)
    val textDark = Color(0xFF1C2C39)
    val textGray = Color(0xFF5A6D66) // Darker grey for better visibility


    LaunchedEffect(Unit) {
        delay(2000)
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
                        text = "LOGGED OUT\nSUCCESSFULLY",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textDark,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "You have been logged out.\nRedirecting to login screen...",
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

