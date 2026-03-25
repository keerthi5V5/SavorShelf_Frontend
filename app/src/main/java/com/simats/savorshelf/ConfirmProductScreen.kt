package com.simats.savorshelf

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmProductScreen(
    detectedDate: String = "",
    detectedMfgDate: String = "",
    detectedLotNumber: String = "",
    onBackClick: () -> Unit,
    onSaveClick: (String, String, String, String, String, String, String) -> Unit,
    onManualEntryClick: () -> Unit
) {
    val bgColor = Color(0xFFF8FAFB)
    val primaryGreen = Color(0xFF0D614E)
    val accentGreen = Color(0xFF1CB089)
    val textPrimary = Color(0xFF141D1C)
    val textSecondary = Color(0xFF5A6D66) // Darker grey for better visibility

    
    var productName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var manufactureDate by remember { mutableStateOf(detectedMfgDate) }
    var batchNumber by remember { mutableStateOf(detectedLotNumber) }
    var quantity by remember { mutableStateOf("") }
    
    var selectedStorage by remember { mutableStateOf("Room Temperature") }
    
    val context = LocalContext.current
    
    // Date Pickers
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    
    val manufactureDatePicker = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            val selected = Calendar.getInstance()
            selected.set(selectedYear, selectedMonth, selectedDay)
            if (selected.timeInMillis > System.currentTimeMillis()) {
                android.widget.Toast.makeText(context, "Manufacture date cannot be in the future", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                manufactureDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
            }
        }, year, month, day
    ).apply {
        datePicker.maxDate = System.currentTimeMillis()
    }

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
                        text = "Review & Confirm",
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
            // Success Hero Section
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = accentGreen,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Product Identified!",
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "We've analyzed the labels. Please verify the details below.",
                        fontSize = 13.sp,
                        color = textSecondary,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Expiry Highlight
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF1F9F6),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "EXPIRY DATE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold, // Bolder
                                color = textSecondary,
                                letterSpacing = 1.sp
                            )


                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = detectedDate.ifEmpty { "Not Found" },
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = primaryGreen
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    TextButton(onClick = onManualEntryClick) {
                        Text(
                            "Wrong date? Edit manually",
                            color = accentGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Form Section
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Product Details",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    LabeledInput("Product Name", productName, { productName = it }, "e.g. Greek Yogurt")
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LabeledInput("Category (Optional)", category, { category = it }, "e.g. Dairy, Snacks")
                    Spacer(modifier = Modifier.height(16.dp))

                    LabeledInput("Quantity / Weight", quantity, { quantity = it }, "e.g. 500g, 2 items")
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // MFG Date
                    Text("Manufacture Date", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textSecondary, modifier = Modifier.padding(bottom = 8.dp))

                    OutlinedTextField(
                        value = manufactureDate,
                        onValueChange = { },
                        readOnly = true,
                        placeholder = { Text("Click to select", color = Color.LightGray) },
                        modifier = Modifier.fillMaxWidth().clickable { manufactureDatePicker.show() },
                        shape = RoundedCornerShape(12.dp),
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = textPrimary,
                            disabledBorderColor = Color(0xFFE5EBE8),
                            disabledContainerColor = Color(0xFFF9FBFA)
                        ),
                        trailingIcon = {
                            Icon(Icons.Outlined.CalendarToday, null, tint = primaryGreen)
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LabeledInput("Batch / Lot No. (Optional)", batchNumber, { batchNumber = it }, "Optional")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Storage Selector
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Stored In", fontWeight = FontWeight.Bold, color = textPrimary)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StorageSmallCard("Fridge", Icons.Outlined.Kitchen, selectedStorage == "Fridge", { selectedStorage = "Fridge" }, Modifier.weight(1f))
                        StorageSmallCard("Pantry", Icons.Outlined.HorizontalSplit, selectedStorage == "Room Temperature", { selectedStorage = "Room Temperature" }, Modifier.weight(1f))
                        StorageSmallCard("Freezer", Icons.Outlined.AcUnit, selectedStorage == "Freezer", { selectedStorage = "Freezer" }, Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Save Button
            Button(
                onClick = {
                    if (productName.isBlank() || manufactureDate.isBlank() || detectedDate.isBlank() || quantity.isBlank()) {
                        android.widget.Toast.makeText(context, "Name, MFG, Expiry and Quantity are required", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        onSaveClick(productName, category, detectedDate, manufactureDate, batchNumber, selectedStorage, quantity)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Save, null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Add to Shelf", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(onClick = onBackClick, modifier = Modifier.fillMaxWidth()) {
                Text("Discard and Retake Photo", color = Color.Red.copy(alpha=0.7f))
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
}


@Composable
fun LabeledInput(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String) {
    Text(label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5A6D66), modifier = Modifier.padding(bottom = 8.dp))

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.LightGray) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFF9FBFA),
            focusedContainerColor = Color.White,
            unfocusedBorderColor = Color(0xFFE5EBE8),
            focusedBorderColor = Color(0xFF0D614E)
        ),
        singleLine = true
    )
}

@Composable
fun StorageSmallCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val color = if (isSelected) Color(0xFF0D614E) else Color(0xFFF9FBFA)
    val contentColor = if (isSelected) Color.White else Color(0xFF6F7E7A)
    
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = color,
        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE5EBE8)),
        modifier = modifier.height(50.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = contentColor, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, color = contentColor, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)

        }
    }
}
