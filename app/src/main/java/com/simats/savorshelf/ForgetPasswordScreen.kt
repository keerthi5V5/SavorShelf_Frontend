package com.simats.savorshelf

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.launch
import com.simats.savorshelf.api.RetrofitClient
import com.simats.savorshelf.api.ForgotPasswordRequest
import com.simats.savorshelf.api.ForgotPasswordResponse
import com.simats.savorshelf.api.VerifyOtpRequest
import com.simats.savorshelf.api.VerifyOtpResponse
import com.simats.savorshelf.api.ResetPasswordRequest
import com.simats.savorshelf.api.ResetPasswordResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgetPasswordScreen(
    onBackClick: () -> Unit = {},
    onSendOtpClick: (String) -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    val backgroundColor = Color(0xFFF4F6FF) // Light pastel blue/white background
    val primaryColor = Color(0xFF0D614E) // Indigo/Purple color for icons and buttons
    val darkTextColor = Color(0xFF101223) // Dark navy/black for headings
    val lightTextColor = Color(0xFF5A6D66) // Darker grey for better subtext contrast
    val placeholderColor = Color(0xFF9E9EA8) // Visible placeholder

    val cardBackgroundColor = Color.White
    
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Back Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(color = Color.White, shape = CircleShape)
                    .border(1.dp, Color(0xFFE4E4EB), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = darkTextColor
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Lock Icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(color = primaryColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = "Lock Icon",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Heading
        Text(
            text = "Forgot Password?",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = darkTextColor
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Subtitle
        Text(
            text = "Enter your email to receive an OTP",
            fontSize = 15.sp,
            color = lightTextColor,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // White Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .shadow(
                    elevation = 20.dp, 
                    shape = RoundedCornerShape(24.dp), 
                    spotColor = Color(0xFFC0CDDB).copy(alpha = 0.5f)
                ),
            shape = RoundedCornerShape(24.dp),
            color = cardBackgroundColor,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp, horizontal = 24.dp)
            ) {
                // Email Label
                Text(
                    text = "Email",
                    fontSize = 14.sp,
                    color = Color(0xFF4A4B57),
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Outlined Text Field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { 
                        Text(
                            text = "Enter your email", 
                            color = placeholderColor,
                            fontSize = 15.sp
                        ) 
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Email,
                            contentDescription = "Email Icon",
                            tint = Color(0xFF3F414E) // Dark tinted icon
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = primaryColor,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = primaryColor,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
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
                
                // Send OTP Button
                Button(
                    onClick = {
                        if (email.isBlank()) {
                            errorMessage = "Please enter your email"
                            return@Button
                        }
                        
                        coroutineScope.launch {
                            isLoading = true
                            errorMessage = null
                            try {
                                val request = ForgotPasswordRequest(email)
                                val response = RetrofitClient.apiService.forgotPassword(request)
                                if (response.isSuccessful) {
                                    val forgotPasswordResponse = response.body()
                                    if (forgotPasswordResponse?.status == "success") {
                                        onSendOtpClick(email)
                                    } else {
                                        errorMessage = forgotPasswordResponse?.message ?: "Failed to send OTP"
                                    }
                                } else {
                                    val errorBodyString = response.errorBody()?.string()
                                    if (errorBodyString != null) {
                                        try {
                                            val errorResponse = com.google.gson.Gson().fromJson(errorBodyString, ForgotPasswordResponse::class.java)
                                            errorMessage = errorResponse.message ?: "Failed to send OTP: ${response.code()}"
                                        } catch (e: Exception) {
                                            errorMessage = "Failed to send OTP: ${response.code()}"
                                        }
                                    } else {
                                        errorMessage = "Failed to send OTP: ${response.message()}"
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
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "Send OTP",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold

                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Login Link
                val loginText = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color(0xFF6A6B7A))) {
                        append("Remember password? ")
                    }
                    withStyle(style = SpanStyle(color = primaryColor, fontWeight = FontWeight.Bold)) {
                        append("Login")
                    }
                }
                
                Text(
                    text = loginText,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable { onLoginClick() }
                        .padding(8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyOtpScreen(
    email: String = "user@example.com",
    onBackClick: () -> Unit = {},
    onVerifyClick: () -> Unit = {},
    onResendClick: () -> Unit = {}
) {
    val backgroundColor = Color(0xFFF4F6FF)
    val primaryColor = Color(0xFF0D614E)
    val darkTextColor = Color(0xFF101223)
    val lightTextColor = Color(0xFF5A6D66)

    val cardBackgroundColor = Color.White
    
    // Holds the 6 digit OTP string
    var otpValue by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Back Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(color = Color.White, shape = CircleShape)
                    .border(1.dp, Color(0xFFE4E4EB), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = darkTextColor
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Shield/Check Icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(color = primaryColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = "Verify Icon",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Heading
        Text(
            text = "Verify OTP",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = darkTextColor
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Subtitle
        Text(
            text = "Enter the 6-digit code sent to your email",
            fontSize = 15.sp,
            color = lightTextColor,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // White Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .shadow(
                    elevation = 20.dp, 
                    shape = RoundedCornerShape(24.dp), 
                    spotColor = Color(0xFFC0CDDB).copy(alpha = 0.5f)
                ),
            shape = RoundedCornerShape(24.dp),
            color = cardBackgroundColor,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Verification Code Sent",
                    fontSize = 18.sp,
                    color = darkTextColor,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                val sentText = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = lightTextColor)) {
                        append("We sent a code to ")
                    }
                    withStyle(style = SpanStyle(color = primaryColor, fontWeight = FontWeight.SemiBold)) {
                        append(email)
                    }
                }
                Text(
                    text = sentText,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Single text field customized to look like OTP inputs
                OutlinedTextField(
                    value = otpValue,
                    onValueChange = { if (it.length <= 6) otpValue = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center, 
                        fontSize = 24.sp,
                        letterSpacing = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = primaryColor,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = primaryColor,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Resend Timer
                var timeLeft by remember { mutableStateOf(120) }
                
                LaunchedEffect(key1 = timeLeft) {
                    if (timeLeft > 0) {
                        kotlinx.coroutines.delay(1000L)
                        timeLeft -= 1
                    }
                }
                
                val minutes = timeLeft / 60
                val seconds = timeLeft % 60
                val timeFormatted = "$minutes:${if (seconds < 10) "0$seconds" else seconds}"
                
                val resendText = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = lightTextColor)) {
                        append("otp expires in ")
                    }
                    withStyle(style = SpanStyle(color = primaryColor, fontWeight = FontWeight.Bold)) {
                        append(timeFormatted)
                    }
                }
                Text(
                    text = resendText,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable(enabled = timeLeft == 0) { 
                        if (timeLeft == 0) {
                            onResendClick()
                            timeLeft = 120
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
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

                // Verify Code Button
                Button(
                    onClick = {
                        if (otpValue.isBlank()) {
                            errorMessage = "Please enter the OTP"
                            return@Button
                        }
                        
                        coroutineScope.launch {
                            isLoading = true
                            errorMessage = null
                            try {
                                val request = VerifyOtpRequest(email, otpValue)
                                val response = RetrofitClient.apiService.verifyOtp(request)
                                if (response.isSuccessful) {
                                    val verifyResponse = response.body()
                                    if (verifyResponse?.status == "success") {
                                        onVerifyClick()
                                    } else {
                                        errorMessage = verifyResponse?.message ?: "OTP verification failed"
                                    }
                                } else {
                                    val errorBodyString = response.errorBody()?.string()
                                    if (errorBodyString != null) {
                                        try {
                                            val errorResponse = com.google.gson.Gson().fromJson(errorBodyString, VerifyOtpResponse::class.java)
                                            errorMessage = errorResponse.message ?: "Verification failed: ${response.code()}"
                                        } catch (e: Exception) {
                                            errorMessage = "Verification failed: ${response.code()}"
                                        }
                                    } else {
                                        errorMessage = "Verification failed: ${response.message()}"
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
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "Verify Code",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold

                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    email: String = "user@example.com",
    onBackClick: () -> Unit = {},
    onResetPasswordClick: () -> Unit = {}
) {
    val backgroundColor = Color(0xFFF4F6FF)
    val primaryColor = Color(0xFF0D614E)
    val darkTextColor = Color(0xFF101223)
    val lightTextColor = Color(0xFF5A6D66)
    val placeholderColor = Color(0xFF9E9EA8)

    val cardBackgroundColor = Color.White
    
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Back Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(color = Color.White, shape = CircleShape)
                    .border(1.dp, Color(0xFFE4E4EB), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = darkTextColor
                )
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp)) // Spacing since there is no icon here
        
        // Heading
        Text(
            text = "Reset Password",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = darkTextColor
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Subtitle
        Text(
            text = "Create a new password",
            fontSize = 15.sp,
            color = lightTextColor,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // White Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .shadow(
                    elevation = 20.dp, 
                    shape = RoundedCornerShape(24.dp), 
                    spotColor = Color(0xFFC0CDDB).copy(alpha = 0.5f)
                ),
            shape = RoundedCornerShape(24.dp),
            color = cardBackgroundColor,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp, horizontal = 24.dp)
            ) {
                // New Password Label
                Text(
                    text = "New Password",
                    fontSize = 14.sp,
                    color = Color(0xFF4A4B57),
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // New Password Field
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    placeholder = { 
                        Text(
                            text = "Enter new password", 
                            color = placeholderColor,
                            fontSize = 15.sp
                        ) 
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = "Lock Icon",
                            tint = Color(0xFF3F414E)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                            Icon(
                                imageVector = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (newPasswordVisible) "Hide password" else "Show password",
                                tint = Color(0xFF3F414E)
                            )
                        }
                    },
                    visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color(0xFFE4E4EB),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = primaryColor,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Confirm Password Label
                Text(
                    text = "Confirm Password",
                    fontSize = 14.sp,
                    color = Color(0xFF4A4B57),
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Confirm Password Field
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = { 
                        Text(
                            text = "Confirm new password", 
                            color = placeholderColor,
                            fontSize = 15.sp
                        ) 
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = "Lock Icon",
                            tint = Color(0xFF3F414E)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                tint = Color(0xFF3F414E)
                            )
                        }
                    },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color(0xFFE4E4EB),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = primaryColor,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(32.dp))
                
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
                
                // Reset Password Button
                Button(
                    onClick = {
                        if (newPassword.isBlank() || confirmPassword.isBlank()) {
                            errorMessage = "Please enter both passwords"
                            return@Button
                        }
                        if (newPassword != confirmPassword) {
                            errorMessage = "Passwords do not match"
                            return@Button
                        }
                        
                        coroutineScope.launch {
                            isLoading = true
                            errorMessage = null
                            try {
                                val request = ResetPasswordRequest(email, newPassword, confirmPassword)
                                val response = RetrofitClient.apiService.resetPassword(request)
                                if (response.isSuccessful) {
                                    val resetResponse = response.body()
                                    if (resetResponse?.status == "success") {
                                        onResetPasswordClick()
                                    } else {
                                        errorMessage = resetResponse?.message ?: "Password reset failed"
                                    }
                                } else {
                                    val errorBodyString = response.errorBody()?.string()
                                    if (errorBodyString != null) {
                                        try {
                                            val errorResponse = com.google.gson.Gson().fromJson(errorBodyString, ResetPasswordResponse::class.java)
                                            errorMessage = errorResponse.message ?: "Reset failed: ${response.code()}"
                                        } catch (e: Exception) {
                                            errorMessage = "Reset failed: ${response.code()}"
                                        }
                                    } else {
                                        errorMessage = "Reset failed: ${response.message()}"
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
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "Reset Password",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordResetSuccessScreen(
    onBackToLoginClick: () -> Unit = {}
) {
    val backgroundColor = Color(0xFFF4F6FF)
    val primaryColor = Color(0xFF0D614E)
    val darkTextColor = Color(0xFF101223)
    val lightTextColor = Color(0xFF6A6B7A)
    val cardBackgroundColor = Color.White
    
    val successBgColor = Color(0xFFE8F8F0)
    val successIconColor = Color(0xFF0F824A)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        
        Spacer(modifier = Modifier.height(80.dp))
        
        // Check Icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(color = primaryColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = "Success Icon",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Heading
        Text(
            text = "All Set!!",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = darkTextColor
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Subtitle
        Text(
            text = "Your password has been successfully reset.\nYou can now sign in to your account using your\nnew password.",
            fontSize = 15.sp,
            color = lightTextColor,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
            lineHeight = 22.sp
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // White Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .shadow(
                    elevation = 20.dp, 
                    shape = RoundedCornerShape(24.dp), 
                    spotColor = Color(0xFFC0CDDB).copy(alpha = 0.5f)
                ),
            shape = RoundedCornerShape(24.dp),
            color = cardBackgroundColor,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp, horizontal = 24.dp)
            ) {
                // Success Alert Box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = successBgColor, shape = RoundedCornerShape(12.dp))
                        .border(1.dp, successIconColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = "Success",
                        tint = successIconColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Password Updated Successfully",
                            color = successIconColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Your account is now secured with your new password. Please keep it safe.",
                            color = successIconColor,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Back to Login Button
                Button(
                    onClick = onBackToLoginClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Back to Login",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Security Tip
        val tipText = buildAnnotatedString {
            withStyle(style = SpanStyle(color = darkTextColor.copy(alpha=0.4f))) {
                append("💡 ")
            }
            withStyle(style = SpanStyle(color = darkTextColor, fontWeight = FontWeight.SemiBold)) {
                append("Security Tip: ")
            }
            withStyle(style = SpanStyle(color = lightTextColor)) {
                append("Never share your password with anyone")
            }
        }
        
        Text(
            text = tipText,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}
