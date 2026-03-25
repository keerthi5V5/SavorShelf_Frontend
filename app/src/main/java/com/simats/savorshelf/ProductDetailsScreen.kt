package com.simats.savorshelf

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
import com.simats.savorshelf.api.RetrofitClient
import com.simats.savorshelf.api.ProductDetailData
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import kotlinx.coroutines.launch
import com.simats.savorshelf.api.DeleteProductRequest
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    productId: String?, 
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var product by remember { mutableStateOf<ProductDetailData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isDeleting by remember { mutableStateOf(false) }
    
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(productId) {
        if (productId != null) {
            try {
                val response = RetrofitClient.apiService.getProductDetails(productId)
                if (response.isSuccessful) {
                    product = response.body()?.data
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        isLoading = false
    }

    if (isLoading) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) { 
            CircularProgressIndicator()
        }
        return
    }

    if (product == null) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) { 
            Text("Product not found") 
            Button(onClick = onBackClick) { Text("Back to Products") }
        }
        return
    }
    
    val bgColor = Color(0xFFF6F8F7)
    val textPrimary = Color(0xFF141D1C)
    val textSecondary = Color(0xFF5A6D66) // Darker grey for better visibility


    val daysRemaining = product?.days_remaining ?: 7
    val freshnessLabel = product?.freshness_label ?: when {
        daysRemaining < 0 -> "Expired"
        daysRemaining <= 3 -> "Use Soon"
        else -> "Fresh"
    }

    val freshnessColor = when (freshnessLabel) {
        "Expired" -> Color(0xFFE53935)
        "Use Soon" -> Color(0xFFE26027)
        else -> Color(0xFF0D614E)
    }

    val freshnessBgColor = when (freshnessLabel) {
        "Expired" -> Color(0xFFFCE8E8)
        "Use Soon" -> Color(0xFFFBECE5)
        else -> Color(0xFFE9F5EF)
    }

    Scaffold(
        containerColor = bgColor,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .navigationBarsPadding()
            ) {
                Button(
                    onClick = {
                        if (productId == null || isDeleting) return@Button
                        isDeleting = true
                        coroutineScope.launch {
                            try {
                                val response = RetrofitClient.apiService.deleteProduct(DeleteProductRequest(productId))
                                if (response.isSuccessful) {
                                    Toast.makeText(context, "Product consumed!", Toast.LENGTH_SHORT).show()
                                    onDeleteClick()
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
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D614E)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isDeleting
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Consumed", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
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
                        text = "Product Details",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
            // Header Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        NetworkImage(
                            model = product!!.image_path ?: "",
                            contentDescription = product!!.item_name ?: "Product",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(text = product!!.item_name ?: "Unknown", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = textPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .background(freshnessBgColor, RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = freshnessLabel,
                                color = freshnessColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Information Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Product Information",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = textPrimary
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Purchase Date
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = textSecondary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(product!!.primary_date_label ?: "Date", fontSize = 12.sp, color = textSecondary, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(product!!.primary_date_value ?: "-", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textPrimary)

                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Freshness Details
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Outlined.Info, contentDescription = null, tint = textSecondary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(product!!.expiry_label ?: "Expiry", fontSize = 12.sp, color = textSecondary, fontWeight = FontWeight.Medium)

                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                                Text(product!!.expiry_value ?: "-", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                                Text(freshnessLabel, fontSize = 11.sp, color = freshnessColor, fontWeight = FontWeight.Bold)
                            }
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            // Progress Bar
                            Box(modifier = Modifier.fillMaxWidth().height(6.dp).background(Color(0xFFE9F0EC), CircleShape)) {
                                val fraction = (product!!.freshness_progress ?: 0) / 100f
                                Box(modifier = Modifier.fillMaxWidth(fraction).fillMaxHeight().background(freshnessColor, CircleShape))
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Storage Location
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Outlined.Thermostat, contentDescription = null, tint = textSecondary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Storage Location", fontSize = 12.sp, color = textSecondary, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(product!!.storage_location ?: "Pantry", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textPrimary)

                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Condition Banner
            val backgroundColor: Color
            val borderColor: Color
            val icon: androidx.compose.ui.graphics.vector.ImageVector
            val title: String
            val msg: String
            
            if (freshnessLabel == "Fresh") {
                backgroundColor = Color(0xFFF0FDF4)
                borderColor = Color(0xFFDCFCE7)
                icon = Icons.Outlined.CheckCircle
                title = "Great Condition!"
                msg = "This item is in excellent condition. Continue storing properly to maintain freshness."
            } else if (freshnessLabel == "Use Soon") {
                backgroundColor = Color(0xFFFFF7ED)
                borderColor = Color(0xFFFFEDD5)
                icon = Icons.Outlined.Warning
                title = "Use Soon!"
                msg = "This item is approaching its expiry or losing freshness. Plan to use it shortly."
            } else {
                backgroundColor = Color(0xFFFEF2F2)
                borderColor = Color(0xFFFEE2E2)
                icon = Icons.Outlined.ErrorOutline
                title = "Expired!"
                msg = "This item has expired or is no longer fresh. Please discard or compost it."
            }
            
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = backgroundColor,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(icon, contentDescription = null, tint = freshnessColor, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(title, fontWeight = FontWeight.Bold, color = textPrimary, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(msg, color = textSecondary, fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium)

                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
}
