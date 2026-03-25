package com.simats.savorshelf

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.simats.savorshelf.api.RetrofitClient
import com.simats.savorshelf.api.DeleteAccountRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAccountScreen(
    onCancelClick: () -> Unit,
    onDeleteConfirm: () -> Unit
) {
    val backgroundUrl = "https://image2url.com/r2/default/images/1772165942529-0db3e05f-ebef-48b7-9133-fc8662d9a661.png"
    val redColor = Color(0xFFCC0000)
    val lightRedBg = Color(0xFFFFF0F0)
    val borderRed = Color(0xFFFFB3B3)
    val textDark = Color(0xFF141D1C)
    val textGray = Color(0xFF5A6D66) // Darker grey for better visibility


    var inputText by remember { mutableStateOf("") }
    val isConfirmEnabled = inputText == "DELETE"
    var isDeleting by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE) }

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
                    // Red Garbage Icon
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(color = redColor, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "DELETE ACCOUNT",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textDark
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Warning Box
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = lightRedBg,
                        border = BorderStroke(1.dp, borderRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.WarningAmber,
                                    contentDescription = "Warning",
                                    tint = redColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "This action cannot be undone!",
                                    color = redColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                "Deleting your account will permanently remove:",
                                color = Color(0xFF8B0000), // Darker red for contrast
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                fontWeight = FontWeight.Bold
                            )


                            Spacer(modifier = Modifier.height(8.dp))

                            val bullets = listOf(
                                "All patient records and data",
                                "Your profile information",
                                "Clinic details and settings",
                                "All appointments and history"
                            )

                            bullets.forEach { point ->
                                Row(
                                    modifier = Modifier.padding(bottom = 6.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        "•",
                                        color = Color(0xFF993333),
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(end = 6.dp)
                                    )
                                    Text(
                                        point,
                                        color = Color(0xFF8B0000),
                                        fontSize = 12.sp,
                                        lineHeight = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )

                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Confirm Input
                    val typeConfirmText = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = textDark, fontWeight = FontWeight.Bold)) {
                            append("Type ")
                        }
                        withStyle(style = SpanStyle(color = redColor, fontWeight = FontWeight.Bold)) {
                            append("DELETE")
                        }
                        withStyle(style = SpanStyle(color = textDark, fontWeight = FontWeight.Bold)) {
                            append(" to confirm:")
                        }
                    }

                    Text(
                        text = typeConfirmText,
                        fontSize = 13.sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("DELETE", color = Color(0xFFB0B0B0)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

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
                            shape = RoundedCornerShape(12.dp),
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
                            onClick = {
                                if (isDeleting) return@Button
                                
                                isDeleting = true
                                coroutineScope.launch {
                                    try {
                                        val userId = sharedPrefs.getInt("user_id", -1)
                                        val request = DeleteAccountRequest(user_id = userId)
                                        val response = RetrofitClient.apiService.deleteAccount(request)
                                        
                                        if (response.isSuccessful) {
                                            sharedPrefs.edit().clear().apply() // clear shared prefs
                                            Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()
                                            onDeleteConfirm()
                                        } else {
                                            Toast.makeText(context, "Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isDeleting = false
                                    }
                                }
                            },
                            enabled = isConfirmEnabled && !isDeleting,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = redColor,
                                disabledContainerColor = redColor.copy(alpha = 0.5f)
                            )
                        ) {
                            if (isDeleting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            } else {
                                Text(
                                    "DELETE\nACCOUNT",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 14.sp
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
fun AccountDeletedScreen(
    onComplete: () -> Unit
) {
    val backgroundUrl = "https://image2url.com/r2/default/images/1772165942529-0db3e05f-ebef-48b7-9133-fc8662d9a661.png"
    val greenColor = Color(0xFF00C853)
    val textDark = Color(0xFF1C2C39)
    val textGray = Color(0xFF6F7E7A)

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
                        text = "ACCOUNT DELETED",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textDark
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Your account has been permanently deleted. Thank you for using SavorShelf.",
                        fontSize = 14.sp,
                        color = Color(0xFF5A6D66), 
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium,
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

