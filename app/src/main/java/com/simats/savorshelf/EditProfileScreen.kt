package com.simats.savorshelf

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import kotlinx.coroutines.launch
import com.simats.savorshelf.api.RetrofitClient
import com.simats.savorshelf.api.UpdateProfileRequest
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onHomeClick: () -> Unit,
    onProductsClick: () -> Unit,
    onCameraClick: () -> Unit,
    onFreshnessClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val bgColor = Color(0xFFE9F0EC)
    val textPrimary = Color(0xFF141D1C)
    val textSecondary = Color(0xFF5A6D66) // Darker grey for better visibility

    val primaryGreen = Color(0xFF0D614E)

    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    var fullName by remember { mutableStateOf(sharedPrefs.getString("user_name", "User") ?: "User") }
    var email by remember { mutableStateOf(sharedPrefs.getString("user_email", "user@example.com") ?: "user@example.com") }
    
    var showPhotoPopup by remember { mutableStateOf(false) }
    var currentAvatarRes by remember { mutableIntStateOf(sharedPrefs.getInt("avatar_res", -1)) }
    
    var isSaving by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val avatars = remember {
        listOf(
            R.drawable.av_gen_1, R.drawable.av_gen_2, R.drawable.av_gen_3, R.drawable.av_gen_4,
            R.drawable.av_gen_5, R.drawable.av_gen_6, R.drawable.av_gen_7, R.drawable.av_gen_8,
            R.drawable.av_gen_9, R.drawable.av_gen_10, R.drawable.av_gen_11, R.drawable.av_gen_12,
            R.drawable.av_gen_13, R.drawable.av_gen_14, R.drawable.av_gen_15, R.drawable.av_gen_16,
            R.drawable.av_gen_17,
            R.drawable.av_veg_broccoli, R.drawable.av_veg_carrot, R.drawable.av_fruit_apple, 
            R.drawable.av_fruit_strawberry, R.drawable.av_animal_lion, R.drawable.av_animal_panda,
            R.drawable.av_human_f, R.drawable.av_human_m
        )
    }

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(
                selectedTab = 4,
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
                    Column {
                        Text(
                            text = "Profile Settings",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Manage your account information",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(primaryGreen)
                            .clickable { showPhotoPopup = true },
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentAvatarRes != -1) {
                            NetworkImage(
                                model = currentAvatarRes,
                                contentDescription = "Profile Photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text("K", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(text = fullName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Change profile picture", 
                        fontSize = 14.sp, 
                        fontWeight = FontWeight.Medium,
                        color = primaryGreen,
                        modifier = Modifier.clickable { showPhotoPopup = true }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Edit Profile", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Text("Full Name", fontSize = 12.sp, color = textPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE5EBE8),
                            focusedBorderColor = primaryGreen,
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary
                        ),
                        trailingIcon = { Icon(Icons.Outlined.Person, contentDescription = null, tint = Color(0xFFA1AFAB)) },
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Email Address", fontSize = 12.sp, color = textPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledContainerColor = Color(0xFFF9FAF9),
                            disabledBorderColor = Color(0xFFE5EBE8),
                            disabledTextColor = textPrimary,
                            disabledTrailingIconColor = textPrimary
                        ),
                        trailingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                        singleLine = true
                    )
                    Text("Email cannot be changed.", fontSize = 10.sp, color = textSecondary, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 4.dp, start = 4.dp))

                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = {
                            if (isSaving) return@Button
                            if (fullName.isBlank()) {
                                Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            
                            isSaving = true
                            coroutineScope.launch {
                                try {
                                    val requestUserId = sharedPrefs.getInt("user_id", -1)
                                    val req = UpdateProfileRequest(user_id = requestUserId, full_name = fullName)
                                    val response = RetrofitClient.apiService.updateProfile(req)
                                    
                                    if (response.isSuccessful) {
                                        sharedPrefs.edit { putString("user_name", fullName) }
                                        Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                                        onSaveClick()
                                    } else {
                                        Toast.makeText(context, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Network Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isSaving = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Save Changes", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Version 1.0.0 • © 2026 SAVORSHELF",
                fontSize = 11.sp,
                color = textSecondary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )


            
        }
    }


    if (showPhotoPopup) {
        AlertDialog(
            onDismissRequest = { showPhotoPopup = false },
            containerColor = Color(0xFF1B2625),
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    text = "SELECT PROFILE PICTURE",
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFFCC00),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(avatars) { avatarRes ->
                            val interactionSource = remember { MutableInteractionSource() }
                            val isPressed by interactionSource.collectIsPressedAsState()
                            val scale by animateFloatAsState(if (isPressed) 0.9f else 1f, label = "scale")

                            val isSelected = currentAvatarRes == avatarRes
                            
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .scale(scale)
                                    .shadow(
                                        elevation = if (isSelected) 8.dp else 4.dp,
                                        shape = RoundedCornerShape(16.dp),
                                        clip = false
                                    )
                                    .background(Color.White, RoundedCornerShape(16.dp))
                                    .border(
                                        width = if (isSelected) 3.dp else 0.dp,
                                        color = if (isSelected) Color(0xFFFFCC00) else Color.Transparent,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable(
                                        interactionSource = interactionSource,
                                        indication = null
                                    ) {
                                        currentAvatarRes = avatarRes
                                        sharedPrefs.edit { putInt("avatar_res", avatarRes) }
                                        showPhotoPopup = false
                                    }
                            ) {
                                NetworkImage(
                                    model = avatarRes,
                                    contentDescription = "Avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(if (isSelected) 3.dp else 0.dp)
                                        .clip(RoundedCornerShape(if (isSelected) 13.dp else 16.dp))
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPhotoPopup = false }) {
                    Text("Close", color = Color(0xFFFFCC00), fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

}
