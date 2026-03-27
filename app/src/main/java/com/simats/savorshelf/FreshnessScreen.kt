package com.simats.savorshelf

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import com.simats.savorshelf.api.RetrofitClient
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreshnessScreen(
    onBackClick: () -> Unit,
    onStartAddingClick: () -> Unit,
    onHomeClick: () -> Unit,
    onProductsClick: () -> Unit,
    onCameraClick: () -> Unit,
    onFreshnessClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onProductClick: (ProductModel) -> Unit,
    onDeleteProduct: (ProductModel) -> Unit
) {
    val textPrimary = Color(0xFF141D1C)
    val textSecondary = Color(0xFF5A6D66) // Darker grey for better visibility

    
    var reportItems by remember { mutableStateOf<List<ProductModel>>(emptyList()) }
    var freshCount by remember { mutableIntStateOf(0) }
    var useSoonCount by remember { mutableIntStateOf(0) }
    var expiredCount by remember { mutableIntStateOf(0) }
    var consumedCount by remember { mutableIntStateOf(0) }
    var wastedCount by remember { mutableIntStateOf(0) }
    var mostWastedItem by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE) }
    
    LaunchedEffect(Unit) {
        try {
            val userIdString = sharedPrefs.getInt("user_id", -1).toString()
            val response = RetrofitClient.apiService.getFreshnessReport(userIdString)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    freshCount = body.summary?.fresh ?: 0
                    useSoonCount = body.summary?.use_soon ?: 0
                    expiredCount = body.summary?.expired ?: 0
                    consumedCount = body.summary?.weekly_consumed ?: 0
                    wastedCount = body.summary?.weekly_wasted ?: 0
                    mostWastedItem = body.summary?.most_wasted_item
                    
                    reportItems = body.items?.map { item ->
                        val color = when (item.freshnessLabel ?: "") {
                            "Expired" -> Color(0xFFE53935)
                            "Use Soon" -> Color(0xFFE26027)
                            else -> Color(0xFF0D614E)
                        }
                        val bgCol = when (item.freshnessLabel ?: "") {
                            "Expired" -> Color(0xFFFCE8E8)
                            "Use Soon" -> Color(0xFFFBECE5)
                            else -> Color(0xFFE9F5EF)
                        }
                        ProductModel(
                            id = item.id ?: "",
                            name = item.name ?: "",
                            detailValue = item.detailValue ?: "",
                            freshnessLabel = item.freshnessLabel ?: "Fresh",
                            storageType = item.storageType ?: "Fridge",
                            freshnessColor = color,
                            freshnessBgColor = bgCol,
                            imageUrl = item.imageUrl ?: "",
                            progress = item.progress ?: 100,
                            isLabeled = item.isLabeled ?: false
                        )
                    } ?: emptyList()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<ProductModel?>(null) }

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(
                selectedTab = 3, // Freshness tab selected
                onHomeClick = onHomeClick,
                onProductsClick = onProductsClick,
                onCameraClick = onCameraClick,
                onFreshnessClick = onFreshnessClick,
                onSettingsClick = onSettingsClick
            )
        },
        containerColor = Color(0xFFF9FAF9)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
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
                        text = "Freshness Report",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            // Summary Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ReportCard(title = "Fresh", count = freshCount, baseColor = Color(0xFF00C853), modifier = Modifier.weight(1f))
                ReportCard(title = "Use Soon", count = useSoonCount, baseColor = Color(0xFFFF6D00), modifier = Modifier.weight(1f))
                ReportCard(title = "Expired", count = expiredCount, baseColor = Color(0xFFEF4444), modifier = Modifier.weight(1f))
            }
            
            // Weekly Waste Impact Tracker
            WasteImpactBanner(consumedCount = consumedCount, wastedCount = wastedCount, mostWastedItem = mostWastedItem)

            val labeledProducts = reportItems.filter { it.isLabeled }
            val unlabeledProducts = reportItems.filter { !it.isLabeled }

            if (isLoading) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF0D614E))
                }
            } else if (reportItems.isEmpty()) {
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No items to display", color = textSecondary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Start adding items",
                        color = Color(0xFF00C853),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onStartAddingClick() }
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    if (labeledProducts.isNotEmpty()) {
                        item {
                            Text(
                                "Labeled Items: Expiry Level",
                                fontWeight = FontWeight.Bold,
                                color = textPrimary,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                            labeledProducts.forEachIndexed { index, product ->
                                FreshnessLabeledCard(
                                    product = product,
                                    onDeleteClick = onDeleteProduct
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }

                    if (unlabeledProducts.isNotEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0xFFF0FDF4), // Light green tint
                                border = BorderStroke(1.dp, Color(0xFFDCFCE7)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        "Unlabeled Items: Estimated\nFreshness",
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary,
                                        fontSize = 18.sp,
                                        lineHeight = 24.sp,
                                        modifier = Modifier.padding(bottom = 16.dp, top = 4.dp)
                                    )
                                    
                                    unlabeledProducts.forEachIndexed { index, product ->
                                        FreshnessUnlabeledCard(
                                            product = product,
                                            onDeleteClick = onDeleteProduct
                                        )
                                        if (index < unlabeledProducts.size - 1) {
                                            Spacer(modifier = Modifier.height(12.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    if (showDeleteConfirmDialog && productToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Product Consumed?", fontWeight = FontWeight.Bold, color = textPrimary) },
            text = { Text("Have you finished ${productToDelete?.name}? It will be removed from your pantry and added to your weekly report.", color = textSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    val p = productToDelete
                    if (p != null) { 
                        onDeleteProduct(p) 
                        reportItems = reportItems.filter { it.id != p.id }
                    }
                    showDeleteConfirmDialog = false
                    productToDelete = null
                }) {
                    Text("Consume", color = Color(0xFF0D614E), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showDeleteConfirmDialog = false
                    productToDelete = null
                }) {
                    Text("Cancel", color = textPrimary)
                }
            },
            containerColor = Color.White
        )
    }
}

@Composable
fun ReportCard(title: String, count: Int, baseColor: Color, modifier: Modifier = Modifier) {
    val gradient = Brush.linearGradient(
        colors = listOf(baseColor, baseColor.copy(alpha = 0.85f))
    )
    
    Surface(
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 4.dp,
        color = Color.Transparent,
        modifier = modifier.height(85.dp)
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.25f),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = count.toString(),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 16.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}


@Composable
fun FreshnessLabeledCard(
    product: ProductModel,
    onDeleteClick: (ProductModel) -> Unit
) {
    val textSecondary = Color(0xFF5A6D66)
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE5EBE8)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B2625),
                    fontSize = 18.sp
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = product.freshnessColor,
                ) {
                    Text(
                        text = product.freshnessLabel,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = product.detailValue,
                fontSize = 14.sp,
                color = textSecondary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress Bar simulation
            Box(modifier = Modifier.fillMaxWidth().height(8.dp).background(Color(0xFFE2E8F0), CircleShape)) {
                val fraction = product.progress / 100f
                Box(modifier = Modifier.fillMaxWidth(fraction).fillMaxHeight().background(product.freshnessColor, CircleShape))
            }
        }
    }
}

@Composable
fun FreshnessUnlabeledCard(
    product: ProductModel,
    onDeleteClick: (ProductModel) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B2625),
                    fontSize = 18.sp
                )
                val statusText = if (product.detailValue.contains(":")) product.detailValue.substringAfter(":").trim() else product.freshnessLabel
                Text(
                    text = statusText,
                    color = product.freshnessColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress Bar simulation
            Box(modifier = Modifier.fillMaxWidth().height(10.dp).background(Color(0xFFE2E8F0), CircleShape)) {
                val fraction = product.progress / 100f
                Box(modifier = Modifier.fillMaxWidth(fraction).fillMaxHeight().background(product.freshnessColor, CircleShape))
            }
        }
    }
}

@Composable
fun WasteImpactBanner(consumedCount: Int, wastedCount: Int, mostWastedItem: String?) {
    val totalProcessed = consumedCount + wastedCount
    
    val suggestionText = if (wastedCount > 0) {
        if (mostWastedItem != null) {
            " Try to use your ${mostWastedItem.lowercase()} sooner to reduce waste!"
        } else {
            " Try to use your items sooner to reduce waste!"
        }
    } else {
        " Excellent, zero waste this week!"
    }
 
    val wastedRatio = if (totalProcessed > 0) (wastedCount.toFloat() / totalProcessed) else 0f
    val consumedRatio = 1f - wastedRatio
    
    val wastedPercent = (wastedRatio * 100).toInt()
    val consumedPercent = if (totalProcessed > 0) 100 - wastedPercent else 0
 
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFFF1F0), // Light red/pink
        border = BorderStroke(1.dp, Color(0xFFFFEBEE)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🗑️", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Weekly Waste Impact",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFFD32F2F)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "$wastedCount ${if(wastedCount == 1) "item" else "items"} wasted this week.$suggestionText",
                color = Color(0xFFD32F2F),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            // Consumed vs Wasted Progress Bar Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Consumed: $consumedPercent%", fontSize = 12.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                Text("Wasted: $wastedPercent%", fontSize = 12.sp, color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape)
            ) {
                if (totalProcessed == 0) {
                    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFE2E8F0)))
                } else {
                    Box(modifier = Modifier.weight(if (consumedRatio > 0f) consumedRatio else 0.001f).fillMaxHeight().background(Color(0xFF4CAF50))) 
                    Box(modifier = Modifier.weight(if (wastedRatio > 0f) wastedRatio else 0.001f).fillMaxHeight().background(Color(0xFFEF4444)))
                }
            }
        }
    }
}
