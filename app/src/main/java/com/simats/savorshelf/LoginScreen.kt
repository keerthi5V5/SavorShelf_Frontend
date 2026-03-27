package com.simats.savorshelf

import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.platform.LocalContext
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
import androidx.core.content.edit
import kotlinx.coroutines.launch
import com.simats.savorshelf.api.RetrofitClient
import com.simats.savorshelf.api.LoginRequest
import com.simats.savorshelf.api.LoginResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onSignInClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    val topBackgroundColor = Color(0xFFF1F5F3) // Light greenish background
    val darkGreenColor = Color(0xFF1B4E40)
    val lightGreyTextColor = Color(0xFF5A6D66) // Darker grey for better contrast
    val placeholderColor = Color(0xFF8E9B96) // Slightly lighter but still visible

    val textFieldBackgroundColor = Color(0xFFF8FAF9)
    val primaryGreenColor = Color(0xFF0D614E)
    val logoUrl = "https://image2url.com/r2/default/images/1772164873232-c166ef95-c445-4f7d-9852-4f48d008db1c.png"

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

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
            Box(
                modifier = Modifier
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.2f))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
            ) {
                NetworkImage(
                    model = logoUrl,
                    contentDescription = "SavorShelf Logo",
                    modifier = Modifier.size(80.dp)
                )
            }
            
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
                .weight(1f)
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp), // Extra padding to elevate the card from bottom
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "Sign In",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkGreenColor
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Email Address
                Text(
                    text = "Email",
                    fontSize = 14.sp,
                    color = lightGreyTextColor,
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

                Spacer(modifier = Modifier.height(24.dp))
                
                // Password
                Text(
                    text = "Password",
                    fontSize = 14.sp,
                    color = lightGreyTextColor,
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

                Spacer(modifier = Modifier.height(8.dp))

                // Forgot Password
                Text(
                    text = "Forgot Password?",
                    color = primaryGreenColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable { onForgotPasswordClick() }
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

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

                // Sign In Button
                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            errorMessage = "Please enter email and password"
                            return@Button
                        }
                        
                        coroutineScope.launch {
                            isLoading = true
                            errorMessage = null
                            try {
                                val request = LoginRequest(email, password)
                                val response = RetrofitClient.apiService.login(request)
                                if (response.isSuccessful) {
                                    val loginResponse = response.body()
                                    if (loginResponse?.status == "success") {
                                        sharedPrefs.edit {
                                            putString("user_name", loginResponse.user?.full_name ?: "User")
                                            putString("user_email", loginResponse.user?.email ?: email)
                                            if (loginResponse.user?.id != null) {
                                                putInt("user_id", loginResponse.user.id)
                                            }
                                        }
                                        onSignInClick()
                                    } else {
                                        errorMessage = loginResponse?.message ?: "Login failed"
                                    }
                                } else {
                                    val errorBodyString = response.errorBody()?.string()
                                    if (errorBodyString != null) {
                                        try {
                                            val errorResponse = com.google.gson.Gson().fromJson(errorBodyString, LoginResponse::class.java)
                                            errorMessage = errorResponse.message ?: "Login failed: ${response.code()}"
                                        } catch (e: Exception) {
                                            errorMessage = "Login failed: ${response.code()}"
                                        }
                                    } else {
                                        errorMessage = "Login failed: ${response.message()}"
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
                            text = "Sign In",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))


                // Don't have an account text
                val signUpText = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = lightGreyTextColor, fontWeight = FontWeight.Medium)) {
                        append("Don't have an account? ")
                    }
                    withStyle(style = SpanStyle(color = primaryGreenColor, fontWeight = FontWeight.SemiBold)) {
                        append("Sign Up")
                    }
                }
                
                Text(
                    text = signUpText,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .clickable { onSignUpClick() }
                        .padding(bottom = 32.dp, top = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun LoginSuccessScreen() {
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val userName = sharedPrefs.getString("user_name", "User") ?: "User"

    val backgroundUrl = "https://image2url.com/r2/default/images/1772165942529-0db3e05f-ebef-48b7-9133-fc8662d9a661.png"
    val brightGreen = Color(0xFF439A6B)
    val darkBlueText = Color(0xFF14202B)
    val greyGreenText = Color(0xFF677E75)
    val progressBarColor = Color(0xFF4C5D58)

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
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = Color.Black.copy(alpha = 0.15f)
                    ),
                shape = RoundedCornerShape(24.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .shadow(elevation = 16.dp, shape = CircleShape, spotColor = brightGreen.copy(alpha = 0.3f))
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
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Text(
                        text = "LOGIN SUCCESSFUL!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = darkBlueText
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Welcome back, $userName",
                        fontSize = 14.sp,
                        color = greyGreenText,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "Loading",
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Loading your dashboard...",
                            color = brightGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Custom thin progress bar implementation mapped to the image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFFE4E9E6))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.35f)
                                .height(1.dp)
                                .background(progressBarColor)
                        )
                    }
                }
            }
        }
    }
}
