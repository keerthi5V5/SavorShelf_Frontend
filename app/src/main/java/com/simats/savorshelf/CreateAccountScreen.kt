package com.simats.savorshelf

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.launch
import com.simats.savorshelf.api.RetrofitClient
import com.simats.savorshelf.api.RegisterRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountScreen(
    onCreateAccountClick: () -> Unit = {},
    onSignInClick: () -> Unit = {},
    onTermsClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val topBackgroundColor = Color(0xFFF1F5F3) // Light greenish background
    val darkGreenColor = Color(0xFF1B4E40)
    val lightGreyTextColor = Color(0xFF5A6D66) // Darker grey for better contrast
    val placeholderColor = Color(0xFF8E9B96) // Slightly lighter but still visible

    val textFieldBackgroundColor = Color(0xFFF8FAF9)
    val primaryGreenColor = Color(0xFF0D614E)
    val logoUrl = "https://image2url.com/r2/default/images/1772164873232-c166ef95-c445-4f7d-9852-4f48d008db1c.png"

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(topBackgroundColor)
            .statusBarsPadding()
    ) {
        // Logo and Title Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NetworkImage(
                model = logoUrl,
                contentDescription = "SavorShelf Logo",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "SavorShelf",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = darkGreenColor
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Smart Expiry Detection & Freshness Estimation",
                fontSize = 12.sp,
                color = lightGreyTextColor,
                fontWeight = FontWeight.Medium
            )
        }

        // White Card Section
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "Create Account",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkGreenColor
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Full Name
                Text(
                    text = "Full Name",
                    fontSize = 14.sp,
                    color = darkGreenColor,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = { Text("Enter your name", color = placeholderColor) },

                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Person",
                            tint = lightGreyTextColor
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = textFieldBackgroundColor,
                        unfocusedContainerColor = textFieldBackgroundColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = darkGreenColor,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Email Address
                Text(
                    text = "Email Address",
                    fontSize = 14.sp,
                    color = darkGreenColor,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("you@example.com", color = placeholderColor) },

                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            tint = lightGreyTextColor
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = textFieldBackgroundColor,
                        unfocusedContainerColor = textFieldBackgroundColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = darkGreenColor,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(16.dp))
                
                // Password
                Text(
                    text = "Password",
                    fontSize = 14.sp,
                    color = darkGreenColor,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("********", color = placeholderColor) },

                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock",
                            tint = lightGreyTextColor
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = lightGreyTextColor
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = textFieldBackgroundColor,
                        unfocusedContainerColor = textFieldBackgroundColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = darkGreenColor,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Confirm Password
                Text(
                    text = "Confirm Password",
                    fontSize = 14.sp,
                    color = darkGreenColor,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = { Text("********", color = placeholderColor) },

                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock",
                            tint = lightGreyTextColor
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                tint = lightGreyTextColor
                            )
                        }
                    },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = textFieldBackgroundColor,
                        unfocusedContainerColor = textFieldBackgroundColor,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = darkGreenColor,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Terms and Conditions
                val termsText = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = lightGreyTextColor)) {
                        append("By continuing you are accepting all\n")
                    }
                    withStyle(style = SpanStyle(color = primaryGreenColor, fontWeight = FontWeight.SemiBold)) {
                        append("terms and conditions")
                    }
                }
                Text(
                    text = termsText,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTermsClick() }
                        .padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Error Message
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                }

                // Create Account Button
                Button(
                    onClick = {
                        if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
                            errorMessage = "Please fill all fields"
                            return@Button
                        }
                        if (password != confirmPassword) {
                            errorMessage = "Passwords do not match"
                            return@Button
                        }
                        
                        coroutineScope.launch {
                            isLoading = true
                            errorMessage = null
                            try {
                                val request = RegisterRequest(fullName, email, password, confirmPassword)
                                val response = RetrofitClient.apiService.register(request)
                                if (response.isSuccessful) {
                                    val registerResponse = response.body()
                                    if (registerResponse?.status == "success") {
                                        sharedPrefs.edit {
                                            putString("user_name", fullName)
                                            putString("user_email", email)
                                        }
                                        onCreateAccountClick()
                                    } else {
                                        errorMessage = registerResponse?.message ?: "Registration failed"
                                    }
                                } else {
                                    val errorBodyString = response.errorBody()?.string()
                                    if (errorBodyString != null) {
                                        try {
                                            val errorResponse = com.google.gson.Gson().fromJson(errorBodyString, com.simats.savorshelf.api.RegisterResponse::class.java)
                                            errorMessage = errorResponse.message ?: "Registration failed: ${response.code()}"
                                        } catch (e: Exception) {
                                            errorMessage = "Registration failed: ${response.code()}"
                                        }
                                    } else {
                                        errorMessage = "Registration failed: ${response.message()}"
                                    }
                                }
                            } catch (e: Exception) {
                                errorMessage = "Network error: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryGreenColor),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "Create Account",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


                // Already have an account text
                val signInText = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = lightGreyTextColor, fontWeight = FontWeight.Medium)) {
                        append("Already have an account? ")
                    }
                    withStyle(style = SpanStyle(color = primaryGreenColor, fontWeight = FontWeight.Bold)) {
                        append("Sign In")
                    }
                }
                
                Text(
                    text = signInText,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .clickable { onSignInClick() }
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun AccountCreatedScreen() {
    val backgroundUrl = "https://image2url.com/r2/default/images/1772165942529-0db3e05f-ebef-48b7-9133-fc8662d9a661.png"
    val brightGreen = Color(0xFF15B277)
    val darkBlueText = Color(0xFF1C2C39)
    val greyGreenText = Color(0xFF5A7268)
    val lightGreyText = Color(0xFF869992)
    val pillBgColor = Color(0xFFF3FBF7)
    val pillBorderColor = brightGreen.copy(alpha = 0.2f)
    val darkGreenText = Color(0xFF13523B)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        NetworkImage(
            model = backgroundUrl,
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
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
                            .size(90.dp)
                            .background(color = brightGreen, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Check",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(28.dp))
                    
                    Text(
                        text = "ACCOUNT CREATED!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = darkBlueText
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Welcome to SavorShelf",
                        fontSize = 14.sp,
                        color = greyGreenText,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Your account has been successfully created",
                        fontSize = 12.sp,
                        color = lightGreyText,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // First Pill
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = pillBgColor, shape = RoundedCornerShape(24.dp))
                            .border(1.dp, pillBorderColor, RoundedCornerShape(24.dp))
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Check",
                            tint = brightGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Profile Created",
                            color = darkGreenText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Second Pill
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = pillBgColor, shape = RoundedCornerShape(24.dp))
                            .border(1.dp, pillBorderColor, RoundedCornerShape(24.dp))
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Check",
                            tint = brightGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Account Verified",
                            color = darkGreenText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    Text(
                        text = "Redirecting to login...",
                        fontSize = 12.sp,
                        color = lightGreyText,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

