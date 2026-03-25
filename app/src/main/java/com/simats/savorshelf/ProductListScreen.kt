package com.simats.savorshelf

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import com.simats.savorshelf.api.RetrofitClient
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ProductModel(
    val id: String,
    val name: String,
    val detailValue: String,
    val freshnessLabel: String,         // "Expired", "Fresh", "Use Soon"
    val storageType: String,            // "Fridge", "Pantry"
    val freshnessColor: Color,          // Green, Red, Orange
    val freshnessBgColor: Color,        // Light tint of the color
    val imageUrl: String,
    val progress: Int,
    val isLabeled: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    onProductClick: (ProductModel) -> Unit,
    onDeleteProduct: (ProductModel) -> Unit,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onProductsClick: () -> Unit,
    onCameraClick: () -> Unit,
    onFreshnessClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val bgColor = Color(0xFFF6F8F7)
    val textPrimary = Color(0xFF141D1C)
    val textSecondary = Color(0xFF5A6D66) // Darker grey for better visibility

    val primaryGreen = Color(0xFF0D614E)

    // Products arrive via top-level state

    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    
    // Deletion State
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<ProductModel?>(null) }
    
    var productList by remember { mutableStateOf<List<ProductModel>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            val userIdString = sharedPrefs.getInt("user_id", -1).toString()
            val response = RetrofitClient.apiService.getPantryItems(userIdString)
            if (response.isSuccessful) {
                val data = response.body()?.items ?: emptyList()
                productList = data.map { item ->
                    val fLabel = item.freshnessLabel ?: "Fresh"
                    val bgCol = when (fLabel) {
                        "Expired" -> Color(0xFFFCE8E8)
                        "Use Soon" -> Color(0xFFFBECE5)
                        "Moderate" -> Color(0xFFFFF7E6)
                        else -> Color(0xFFE9F5EF)
                    }
                    val color = when (fLabel) {
                        "Expired" -> Color(0xFFE53935)
                        "Use Soon" -> Color(0xFFE26027)
                        "Moderate" -> Color(0xFFF5A623)
                        else -> Color(0xFF0D614E)
                    }
                    ProductModel(
                        id = item.id ?: "",
                        name = item.name ?: "Unknown",
                        detailValue = item.detailValue ?: "",
                        freshnessLabel = fLabel,
                        storageType = item.storageType ?: "Pantry",
                        freshnessColor = color,
                        freshnessBgColor = bgCol,
                        imageUrl = item.imageUrl ?: "",
                        progress = item.progress ?: 0,
                        isLabeled = item.isLabeled ?: false
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Filter Logic
    val filteredProducts = productList.filter { product ->
        val matchesSearch = product.name.contains(searchQuery, ignoreCase = true)
        val matchesCategory = when (selectedCategory) {
            "All" -> true
            else -> product.freshnessLabel.equals(selectedCategory, ignoreCase = true)
        }
        matchesSearch && matchesCategory
    }

    // Dynamic Category Counts
    val allCount = productList.size
    val freshCount = productList.count { it.freshnessLabel == "Fresh" }
    val moderateCount = productList.count { it.freshnessLabel == "Moderate" }
    val useSoonCount = productList.count { it.freshnessLabel == "Use Soon" }
    val expiredCount = productList.count { it.freshnessLabel == "Expired" }

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(
                selectedTab = 1, // Products tab selected
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
                        text = "My Products",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            // Search Bar area - integrated with background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search your products...", color = Color(0xFF9E9EA8), fontSize = 14.sp) }, // Better placeholder contrast

                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = Color(0xFFA1AFAB),
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE5EBE8),
                        focusedBorderColor = primaryGreen,
                        unfocusedContainerColor = Color(0xFFF9FAF9),
                        focusedContainerColor = Color.White
                    ),
                    singleLine = true
                )
            }
            
            // Filter Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { CategoryChip("All", allCount, selectedCategory == "All") { selectedCategory = "All" } }
                item { CategoryChip("Fresh", freshCount, selectedCategory == "Fresh") { selectedCategory = "Fresh" } }
                item { CategoryChip("Moderate", moderateCount, selectedCategory == "Moderate") { selectedCategory = "Moderate" } }
                item { CategoryChip("Use Soon", useSoonCount, selectedCategory == "Use Soon") { selectedCategory = "Use Soon" } }
                item { CategoryChip("Expired", expiredCount, selectedCategory == "Expired") { selectedCategory = "Expired" } }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Product List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredProducts) { product ->
                    ProductCard(
                        product = product,
                        onClick = { onProductClick(product) },
                        onDeleteClick = {
                            productToDelete = product
                            showDeleteConfirmDialog = true
                        }
                    )
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
                        productList = productList.filter { it.id != p.id }
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
fun CategoryChip(text: String, count: Int, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) Color(0xFF0D614E) else Color.White
    val contentColor = if (isSelected) Color.White else Color(0xFF5A6D66) // Darker grey

    val borderColor = if (isSelected) Color.Transparent else Color(0xFFE5EBE8)

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .height(38.dp)
            .clickable { onClick() }
            .shadow(if (isSelected) 4.dp else 0.dp, RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                color = contentColor,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .background(
                        if (isSelected) Color.White.copy(alpha = 0.2f) else Color(0xFFF1F5F3),
                        CircleShape
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = count.toString(),
                    color = if (isSelected) Color.White else Color(0xFF0D614E),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ProductCard(
    product: ProductModel,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F5F3))
            ) {
                NetworkImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1B2625)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.detailValue,
                    fontSize = 12.sp,
                    color = Color(0xFF5A6D66), // Darker grey
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Category,
                        contentDescription = null,
                        tint = Color(0xFF0D614E).copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = product.storageType,
                        fontSize = 11.sp,
                        color = Color(0xFF5A6D66), // Darker grey
                        fontWeight = FontWeight.SemiBold
                    )

                }
                
                Spacer(modifier = Modifier.height(10.dp))
                
                // Freshness Progress Bar
                val progress = product.progress / 100f
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = product.freshnessColor,
                    trackColor = product.freshnessBgColor,
                )
            }

            // Status & Action Column
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight()
            ) {
                Box(
                    modifier = Modifier
                        .background(product.freshnessBgColor, RoundedCornerShape(12.dp))
                        .border(1.dp, product.freshnessColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = product.freshnessLabel,
                        color = product.freshnessColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = "Remove",
                        tint = Color(0xFFE53935).copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
