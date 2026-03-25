package com.simats.savorshelf

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualEntryScreen(
    onContinueClick: (String) -> Unit,
    onBackToScanClick: () -> Unit
) {
    val bgColor = Color(0xFFE9F0EC)
    val textPrimary = Color(0xFF141D1C)
    val textSecondary = Color(0xFF5A6D66) // Darker grey for better visibility

    val primaryGreen = Color(0xFF0D614E)
    val orangeIconColor = Color(0xFFF48554)

    val context = LocalContext.current

    var expiryDate by remember { mutableStateOf("") }

    // Date Picker Dialog setup
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            expiryDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
        }, year, month, day
    )

    Scaffold(
        containerColor = bgColor,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        if (expiryDate.isBlank()) {
                            android.widget.Toast.makeText(context, "Please select a date", android.widget.Toast.LENGTH_SHORT).show()
                        } else {
                            onContinueClick(expiryDate)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Continue to Product Details", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Button(
                    onClick = onBackToScanClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE5EBE8))
                ) {
                    Text("Back to Scan", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                }
            }
        }
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
                        onClick = onBackToScanClick,
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
                        text = "Enter Expiry Date",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            // Manual Entry Warning/Info Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(orangeIconColor, RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Manual Entry",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Enter the expiry date manually from the product label. You'll add other details on the next screen.",
                            fontSize = 13.sp,
                            color = textSecondary,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Medium
                        )

                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selection Form
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFFFFF4EC), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            tint = orangeIconColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "When does this product expire?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = textPrimary,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Check the expiry date on the product label",
                        fontSize = 13.sp,
                        color = textSecondary,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )


                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Expiry Date:", fontSize = 12.sp, color = textPrimary, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = expiryDate,
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { datePickerDialog.show() },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE5EBE8),
                                focusedBorderColor = primaryGreen,
                                unfocusedContainerColor = Color(0xFFF9FAF9),
                                focusedContainerColor = Color.White,
                                disabledContainerColor = Color(0xFFF9FAF9),
                                disabledBorderColor = Color(0xFFE5EBE8),
                                disabledTextColor = textPrimary
                            ),
                            trailingIcon = {
                                IconButton(onClick = { datePickerDialog.show() }) {
                                    Icon(Icons.Outlined.CalendarToday, contentDescription = "Select Date", tint = textSecondary, modifier = Modifier.size(20.dp))
                                }
                            },
                            enabled = false // Using clickable overlay instead
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Green display box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFE6F5EC), // Very Light Green
                        border = BorderStroke(1.dp, Color(0xFFC3E6D1)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Product expires on:",
                                fontSize = 12.sp,
                                color = textSecondary,
                                fontWeight = FontWeight.SemiBold
                            )

                            if (expiryDate.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = expiryDate,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Common Date Formats
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF2F6FE), 
                border = BorderStroke(1.dp, Color(0xFFD3E1FA)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "Format Info",
                        tint = Color(0xFF4B79E4), // Blue
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Common Date Formats",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = Color(0xFF4B79E4)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("• EXP: 03/15/2026 or 15/03/2026", fontSize = 12.sp, color = Color(0xFF3B61B7), fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("• Best Before: March 15, 2026", fontSize = 12.sp, color = Color(0xFF3B61B7), fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("• Use By: 2026-03-15", fontSize = 12.sp, color = Color(0xFF3B61B7), fontWeight = FontWeight.Medium)
                    }

                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
}
