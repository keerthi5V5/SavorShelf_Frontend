package com.simats.savorshelf

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.simats.savorshelf.api.RetrofitClient
import com.simats.savorshelf.api.AddUnlabeledProductRequest
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUnlabeledItemsScreen(
    category: String,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val bgColor = Color(0xFFF8FAFB)
    val primaryGreen = Color(0xFF0D614E)
    val accentGreen = Color(0xFF1CB089)
    val textPrimary = Color(0xFF141D1C) // Darker for deep contrast
    val textSecondary = Color(0xFF5A6D66) // Darker than original grey


    var expanded by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<Pair<String, String>?>(null) }
    var customName by remember { mutableStateOf("") }
    var purchaseDate by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    
    val storageTypes = listOf("Room Temperature", "Freezer", "Fridge")
    var selectedStorage by remember { mutableStateOf(storageTypes[0]) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE) }
    var isLoading by remember { mutableStateOf(false) }

    val productDatabase = mapOf(
        "Fruits" to listOf(
            "Apple", "Apricot", "Avocado", "Banana", "Blackberry", "Blood Orange", "Blueberry", "Boysenberry",
            "Canary Melon", "Cantaloupe", "Casaba Melon", "Cherimoya", "Cherry", "Christmas Melon", "Clementine",
            "Cranberry", "Crenshaw Melon", "Currants", "Dates", "Dragon Fruit", "Durian", "Fig", "Gooseberry",
            "Grape Fruit", "Grapes", "Guava", "Honeydew Melon", "Horned Melon", "Jack Fruit", "Jujube", "Kiwi",
            "Kumquat", "Lemon", "Lime", "Logan Berry", "Longan", "Lychee", "Mamoncillo", "Mandarin", "Mango",
            "Mangosteen", "Minneola", "Muskmelon", "Nance", "Nectarine", "Orange", "Papaya", "Passion Fruit",
            "Peach", "Pear", "Persimmon", "Pineapple", "Plum", "Pomegranate", "Pommelo", "Prickly Pear",
            "Pulasan", "Rambutan", "Raspberry", "Satsuma", "Soursop", "Star Fruit", "Watermelon", "Ugli Fruit",
            "Tamarillo", "Strawberry", "Tangelo", "Tangerine"
        ).map { Pair(it, "") },
        "Vegetables" to listOf(
            "Anaheim Peppers", "Acorn Squash", "Artichoke", "Ash Gourd", "Asparagus", "Beetroot", "Bitter Gourd",
            "Bottle Gourd", "Brinjal", "Broad Bean", "Broccoli", "Broccoli Raab", "Broccolini", "Brown Onion",
            "Buttercup Squash", "Butternut Squash", "Carrots", "Cauliflower", "Celeriac", "Chilli", "Cluster Bean",
            "Corn", "Sweet Corn", "Courgettes", "Cow Pea", "Cucumber", "Daikon Radish", "Dolichos Bean",
            "Drum Stick", "Eggplant", "Elephantfoot Yam", "French Bean", "Garden Pea", "Garlic", "Ginger",
            "Gourds", "Green Beans", "Green Capsicum", "Green Onions", "Ivy Gourd", "Jicama", "Knol Knol",
            "Kohlrabi", "Kumara", "Kumi Kumi", "Leeks", "Lima Beans", "Mushrooms", "Okra", "Onion", "Parsnips",
            "Pattipan Squash", "Peas", "Pointed Gourd", "Potato", "Pumpkin", "Purple Beans", "Purple Carrots",
            "Purple Cauliflower", "Purple Kumara", "Red Radish", "Red Capsicum", "Red Kumara", "Red Onions",
            "Ridge Gourd", "Romanesco", "Rutabaga", "Shallots", "Snakegourd", "Sugar Snap Peas", "Snow Peas",
            "Spaghetti Squash", "Spine Gourd", "Sponge Gourd", "Squash", "Swede", "Sweet Peppers", "Sweet Potato",
            "Water Chestnuts", "Wing Bean", "Yellow Carrots", "Tapioca", "Taro", "Tomatillo", "Tomato",
            "Turnips", "Yam", "Zucchini"
        ).map { Pair(it, "") },
        "Leafy Greens" to listOf(
            "Amaranthus", "Baby Spinach", "Beet Greens", "Bok Choy", "Brussels Sprouts", "Butter Lettuce",
            "Celery", "Chard", "Chicory", "Chinese Cabbage", "Collard Greens", "Cress", "Curry Leaves",
            "Dandelion Greens", "Endive Lettuce", "Green & Red Lettuce", "Green Cabbage", "Kale", "Lettuce",
            "Mustard Greens", "Napa Cabbage", "Puha", "Radicchio", "Red Cabbage", "Rhubarb", "Romaine Lettuce",
            "Salad Greens", "Savoy Cabbage", "Silverbeet", "Spinach", "Turnips Greens", "Watercress", "Whitloof",
            "Wongbok"
        ).map { Pair(it, "") },
        "Meat & Seafood" to listOf(
            "Anchovy", "Bacon", "Beef Ribs", "Beef Roast", "Beef Steak", "Bison", "Catfish", "Chicken Breast",
            "Chicken Thighs", "Chicken Wings", "Clams", "Cod", "Crab", "Duck", "Goose", "Ground Beef",
            "Ground Lamb", "Ground Pork", "Ground Turkey", "Halibut", "Ham", "Lamb Chops", "Lamb Legs",
            "Lobster", "Mackerel", "Mussels", "Octopus", "Pork Chops", "Pork Ribs", "Pork Tenderloin",
            "Prawns", "Salmon Atlantic", "Salmon Wild", "Sardines", "Sausage", "Scallops", "Sea Bass",
            "Shrimp", "Snapper", "Squid", "Swordfish", "Tilapia", "Trout", "Tuna", "Turkey Breast", "Veal",
            "Venison", "Whole Chicken"
        ).map { Pair(it, "") },
        "Dairy" to listOf(
            "Blue Cheese", "Buttermilk", "Cheese Brie", "Cheese Cheddar", "Cheese Feta", "Cheese Gouda",
            "Cheese Monterey Jack", "Cheese Mozzarella", "Cheese Parmesan", "Cheese Provolone", "Cheese Swiss",
            "Cottage Cheese", "Cream Cheese", "Fruit Yogurt", "Halloumi", "Heavy Cream", "Kefir", "Light Cream",
            "Mascarpone", "Milk 1%", "Milk 2%", "Milk Skim", "Milk Whole", "Paneer", "Plain Yogurt", "Ricotta",
            "Salted Butter", "Sour Cream", "Unsalted Butter", "Vanilla Yogurt", "Whipped Cream", "Ghee",
            "Greek Yogurt"
        ).map { Pair(it, "") },
        "Herbs & Seasonings" to listOf(
            "Allspice", "Anise", "Basil", "Bay Leaves", "Black Pepper", "Caraway Seed", "Cassia Bark",
            "Chamomile", "Chervil", "Chilli Pepper", "Chives", "Cilantro", "Cinnamon", "Coriander Seeds",
            "Crushed Red Pepper", "Cumin", "Curry Powder", "Dill", "Fenugreek", "Garlic Powder", "Ginger Powder",
            "Lavender", "Lemongrass", "Marjoram", "Mint", "Mustard Seed Powder", "Nutmeg", "Onion Powder",
            "Oregano", "Paprika", "Parsley", "Rosemary", "Saffron", "Sage", "Salt", "Savory", "Stevia",
            "Tarragon", "Thyme", "Turmeric Powder", "Winter Savory"
        ).map { Pair(it, "") }
    )

    val products = productDatabase[category] ?: emptyList()

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
                        text = "Product Details",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
            // Category Highlight Card with Insight
            Surface(
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 6.dp,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(primaryGreen, accentGreen)
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White.copy(alpha = 0.25f),
                                modifier = Modifier.size(56.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Outlined.Category,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Adding to Category:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                                Text(
                                    text = category.ifEmpty { "General Items" },
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                    
                    // Insight Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F9F6))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Outlined.Lightbulb,
                                contentDescription = null,
                                tint = primaryGreen,
                                modifier = Modifier.size(18.dp).padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "STORAGE INSIGHT",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = primaryGreen,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = when(category) {
                                        "Fruits" -> "Store ethylene-producing fruits like apples away from others to prevent premature ripening."
                                        "Vegetables" -> "Keep most vegetables in the crisper drawer with high humidity for maximum crispness."
                                        "Meat & Seafood" -> "Always keep raw meat on the bottom shelf to prevent juices from dripping on other food."
                                        "Dairy" -> "Store milk and eggs in the main body of the fridge where temperature is most stable."
                                        else -> "Organize your shelf by expiry date to reduce food waste effectively."
                                    },
                                    fontSize = 13.sp,
                                    color = primaryGreen.copy(alpha = 0.8f),
                                    lineHeight = 18.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Form Card
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Item Specification",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    // Select Product Dropdown
                    CustomLabel("Select Product")
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                    ) {
                        OutlinedTextField(
                            value = selectedProduct?.first ?: "",
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Search or select item", color = textSecondary.copy(alpha = 0.6f)) },

                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color(0xFFF9FBFA),
                                focusedContainerColor = Color.White,
                                unfocusedBorderColor = Color(0xFFE5EBE8),
                                focusedBorderColor = primaryGreen
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            var searchProductText by remember { mutableStateOf("") }
                            
                            OutlinedTextField(
                                value = searchProductText,
                                onValueChange = { searchProductText = it },
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                placeholder = { Text("Type to search...", color = Color.Gray, fontSize = 14.sp) },
                                leadingIcon = { Icon(Icons.Outlined.Search, null, tint = primaryGreen) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color(0xFFE5EBE8),
                                    focusedBorderColor = primaryGreen,
                                    unfocusedTextColor = textPrimary,
                                    focusedTextColor = textPrimary
                                )
                            )
                            
                            val filteredProducts = products.filter { it.first.contains(searchProductText, ignoreCase = true) }
                            
                            filteredProducts.forEach { product ->
                                DropdownMenuItem(
                                    text = { Text(product.first, color = textPrimary, fontWeight = FontWeight.Medium) },

                                    onClick = {
                                        selectedProduct = product
                                        expanded = false
                                        searchProductText = ""
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Custom Name
                    CustomLabel("Or Enter Custom Name")
                    OutlinedTextField(
                        value = customName,
                        onValueChange = { customName = it },
                        placeholder = { Text("e.g. Grandma's Pickles", color = textSecondary.copy(alpha = 0.6f)) },

                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF9FBFA),
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color(0xFFE5EBE8),
                            focusedBorderColor = primaryGreen
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Purchase Date
                    CustomLabel("Purchase Date")
                    OutlinedTextField(
                        value = purchaseDate,
                        onValueChange = { },
                        readOnly = true,
                        placeholder = { Text("Select date", color = textSecondary.copy(alpha = 0.6f)) },

                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(imageVector = Icons.Outlined.CalendarToday, contentDescription = null, tint = primaryGreen)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF9FBFA),
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color(0xFFE5EBE8),
                            focusedBorderColor = primaryGreen,
                            disabledTextColor = textPrimary,
                            disabledBorderColor = Color(0xFFE5EBE8)
                        ),
                        modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                        enabled = false
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Quantity
                    CustomLabel("Quantity / Weight")
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        placeholder = { Text("e.g. 2 kg, 5 items", color = textSecondary.copy(alpha = 0.6f)) },

                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF9FBFA),
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color(0xFFE5EBE8),
                            focusedBorderColor = primaryGreen
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Storage Type Selector
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Storage Location",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StorageChoiceCard(
                            title = "Fridge",
                            icon = Icons.Outlined.Kitchen,
                            isSelected = selectedStorage == "Fridge",
                            onClick = { selectedStorage = "Fridge" },
                            modifier = Modifier.weight(1f)
                        )
                        StorageChoiceCard(
                            title = "Pantry",
                            icon = Icons.Outlined.HorizontalSplit,
                            isSelected = selectedStorage == "Room Temperature",
                            onClick = { selectedStorage = "Room Temperature" },
                            modifier = Modifier.weight(1f)
                        )
                        StorageChoiceCard(
                            title = "Freezer",
                            icon = Icons.Outlined.AcUnit,
                            isSelected = selectedStorage == "Freezer",
                            onClick = { selectedStorage = "Freezer" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Button
            Button(
                onClick = {
                    if (selectedProduct == null && customName.isBlank()) {
                        Toast.makeText(context, "Please provide a name", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (purchaseDate.isBlank()) {
                        Toast.makeText(context, "Select purchase date", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    
                    isLoading = true
                    coroutineScope.launch {
                        try {
                            val request = AddUnlabeledProductRequest(
                                user_id = sharedPrefs.getInt("user_id", -1),
                                category = category,
                                item_name = selectedProduct?.first ?: "",
                                custom_name = customName,
                                purchase_date = purchaseDate,
                                quantity = quantity,
                                storage_type = selectedStorage
                            )
                            val response = RetrofitClient.apiService.addUnlabeledProduct(request)
                            if (response.isSuccessful) {
                                Toast.makeText(context, "Product added successfully!", Toast.LENGTH_SHORT).show()
                                onSaveClick()
                            } else {
                                val errorBody = response.errorBody()?.string()
                                val message = try {
                                    val json = org.json.JSONObject(errorBody ?: "")
                                    json.getString("message")
                                } catch (e: Exception) {
                                    "Error: ${response.code()}"
                                }
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Connectivity Issue: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Save, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Add to Shelf",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            if (millis > System.currentTimeMillis()) {
                                Toast.makeText(context, "Purchase date cannot be in the future", Toast.LENGTH_SHORT).show()
                            } else {
                                val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                                purchaseDate = sdf.format(java.util.Date(millis))
                            }
                        }
                        showDatePicker = false
                    }
                ) { Text("Confirm", fontWeight = FontWeight.Bold, color = primaryGreen) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel", color = Color.Gray) }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun CustomLabel(text: String) {
    val textSecondary = Color(0xFF5A6D66)
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = textSecondary,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}


@Composable
fun StorageChoiceCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryGreen = Color(0xFF0D614E)
    val selectionColor = if (isSelected) primaryGreen else Color(0xFFF9FBFA)
    val contentColor = if (isSelected) Color.White else Color(0xFF6F7E7A)
    
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = selectionColor,
        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE5EBE8)),
        modifier = modifier.height(90.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}
