package com.simats.savorshelf

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.work.*
import com.simats.savorshelf.api.DeleteProductRequest
import com.simats.savorshelf.api.RetrofitClient
import com.simats.savorshelf.ui.theme.SavorShelfTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.content.edit
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install the system splash screen
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        // Request Notification Permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
        
        // Schedule Notification Worker
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "NotificationPolling",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
        
        // Immediate check on startup
        val oneTimeRequest = OneTimeWorkRequestBuilder<NotificationWorker>().build()
        WorkManager.getInstance(this).enqueue(oneTimeRequest)

        // Initialize high-precision alarms
        AlarmHelper.scheduleNextAlarm(this)

        enableEdgeToEdge()
        
        setContent {
            SavorShelfTheme {
                // State to control which screen to show
                var showMainContent by remember { mutableStateOf(false) }

                if (showMainContent) {
                    MainScreen()
                } else {
                    SplashScreenContent(
                        onTimeout = { showMainContent = true }
                    )
                }
            }
        }
    }
}

@Composable
fun SplashScreenContent(onTimeout: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f, 
        animationSpec = tween(durationMillis = 1000),
        label = "Splash Alpha"
    )

    // Simulate a delay for the splash screen
    LaunchedEffect(Unit) {
        startAnimation = true
        delay(3500) // Slightly longer for dot animation
        onTimeout()
    }

    // Full screen splash image
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .alpha(alphaAnim)
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_screen),
            contentDescription = "Splash Screen",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        
        // Animated Dots and Powered by Text
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DotAnimation(delay = 0)
                DotAnimation(delay = 200)
                DotAnimation(delay = 400)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Powered by SIMATS Engineering",
                color = Color(0xFF0D614E).copy(alpha = 0.8f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.8.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DotAnimation(delay: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "Dot")
    val yOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, delayMillis = delay, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Y Offset"
    )

    Box(
        modifier = Modifier
            .size(10.dp)
            .offset(y = yOffset.dp)
            .background(Color(0xFF0D614E), shape = CircleShape)
    )
}
@Composable
fun MainScreen() {
    val backStack = remember { mutableStateListOf(1) }
    val currentScreen = remember {
        object {
            var intValue: Int
                get() = backStack.lastOrNull() ?: 1
                set(value) {
                    val rootScreens = listOf(1, 3, 5, 12)
                    if (backStack.lastOrNull() != value) {
                        if (backStack.size >= 2 && backStack[backStack.size - 2] == value) {
                            // User clicked a generic in-app back button to the previous screen
                            backStack.removeAt(backStack.size - 1)
                        } else if (value in rootScreens) {
                            // Navigating to a root screen clears the history stack
                            backStack.clear()
                            backStack.add(value)
                        } else {
                            // Pushing a new screen
                            backStack.add(value)
                        }
                    }
                }
        }
    }
    
    val selectedUnlabeledCategory = remember { mutableStateOf("") }
    
    // Overview Pager Logic
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    
    // Sync pager with currentScreen if needed, but easier to just use the pager for screens 1-3
    val previousScreenForTerms = remember { mutableIntStateOf(1) }
    
    val selectedProductId = remember { mutableStateOf<String?>(null) }
    val enteredEmail = remember { mutableStateOf("") }
    val scannedExpiryDate = remember { mutableStateOf("2/28/2026") }
    val scannedMfgDate = remember { mutableStateOf("") }
    val scannedLotNumber = remember { mutableStateOf("") }

    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
    var backPressTime by remember { mutableLongStateOf(0L) }

    BackHandler(enabled = true) {
        val current = backStack.lastOrNull() ?: 1
        if (current == 12 || current in 1..3 || current == 5) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - backPressTime < 2000) {
                (context as? android.app.Activity)?.finish()
            } else {
                Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT).show()
                backPressTime = currentTime
            }
        } else {
            if (backStack.size > 1) {
                backStack.removeAt(backStack.size - 1)
            } else {
                (context as? android.app.Activity)?.finish()
            }
        }
    }

    when (currentScreen.intValue) {
        1, 2, 3 -> {
            // Sync internal screen state when swiping
            LaunchedEffect(pagerState.currentPage) {
                val targetScreen = pagerState.currentPage + 1
                if (currentScreen.intValue != targetScreen) {
                    currentScreen.intValue = targetScreen
                }
            }
            
            // Sync pager when currentScreen changes (e.g. from back button)
            LaunchedEffect(currentScreen.intValue) {
                if (currentScreen.intValue in 1..3) {
                    val targetPage = currentScreen.intValue - 1
                    if (pagerState.currentPage != targetPage) {
                        pagerState.scrollToPage(targetPage)
                    }
                }
            }

            val currentBg = if (pagerState.currentPage < 2) Color(0xFFF4F4F0) else Color.White

            Column(modifier = Modifier.fillMaxSize().background(currentBg)) {
                Box(modifier = Modifier.weight(1f)) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        userScrollEnabled = true
                    ) { page ->
                        when (page) {
                            0 -> IntroScreen1()
                            1 -> IntroScreen2()
                            2 -> GetStartedScreen(
                                onGetStartedClick = { currentScreen.intValue = 4 },
                                onSignInClick = { currentScreen.intValue = 5 }
                            )
                        }
                    }

                    // Skip intro button at the top corner
                    if (pagerState.currentPage < 2) {
                        Text(
                            text = "Skip intro",
                            color = Color(0xFF999999),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .statusBarsPadding()
                                .padding(16.dp)
                                .clickable { 
                                    coroutineScope.launch { pagerState.animateScrollToPage(2) } 
                                }
                                .padding(8.dp)
                        )
                    }
                }
                
                // Static Navigation Controls (Only for Intro 1 & 2)
                if (pagerState.currentPage < 2) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PagingIndicators(currentPage = pagerState.currentPage)
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Button(
                            onClick = { 
                                coroutineScope.launch { 
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1) 
                                } 
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D614E)),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text("Next", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Powered by Text at the bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(bottom = 16.dp, top = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Powered by SIMATS Engineering",
                        color = Color(0xFF0D614E).copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
        4 -> {
            CreateAccountScreen(
                onCreateAccountClick = { currentScreen.intValue = 7 },
                onSignInClick = { currentScreen.intValue = 5 },
                onTermsClick = {
                    previousScreenForTerms.intValue = 4
                    currentScreen.intValue = 30
                }
            )
        }
        5 -> {
            LoginScreen(
                onSignInClick = { currentScreen.intValue = 36 },
                onSignUpClick = { currentScreen.intValue = 4 },
                onForgotPasswordClick = { currentScreen.intValue = 8 }
            )
        }
        6 -> {
             // VerifyEmailScreen placeholder
        }
        7 -> {
            AccountCreatedScreen()
            LaunchedEffect(Unit) {
                delay(3000)
                currentScreen.intValue = 5
            }
        }
        8 -> {
            ForgetPasswordScreen(
                onBackClick = { currentScreen.intValue = 5 },
                onSendOtpClick = { 
                    enteredEmail.value = it
                    currentScreen.intValue = 9 
                },
                onLoginClick = { currentScreen.intValue = 5 }
            )
        }
        9 -> {
            VerifyOtpScreen(
                email = enteredEmail.value,
                onBackClick = { currentScreen.intValue = 8 },
                onVerifyClick = { currentScreen.intValue = 10 },
                onResendClick = { /* Handle resend */ }
            )
        }
        10 -> {
            ResetPasswordScreen(
                email = enteredEmail.value,
                onBackClick = { currentScreen.intValue = 9 },
                onResetPasswordClick = { currentScreen.intValue = 11 }
            )
        }
        11 -> {
            PasswordResetSuccessScreen(
                onBackToLoginClick = { currentScreen.intValue = 5 }
            )
        }
        12 -> {
            DashboardScreen(
                onNotificationClick = { currentScreen.intValue = 13 },
                onHomeClick = { /* Already here */ },
                onProductsClick = { currentScreen.intValue = 15 },
                onCameraClick = { currentScreen.intValue = 16 },
                onFreshnessClick = { currentScreen.intValue = 17 },
                onSettingsClick = { currentScreen.intValue = 18 },
                onViewAllClick = { currentScreen.intValue = 14 },
                onProductClick = { id ->
                    selectedProductId.value = id
                    currentScreen.intValue = 24
                },
                onPackagedLabeledClick = { currentScreen.intValue = 21 },
                onFreshUnlabeledClick = { currentScreen.intValue = 19 },
                onPremiumUpgradeClick = { currentScreen.intValue = 36 }
            )
        }
        13 -> {
            NotificationsScreen(
                onBackClick = { currentScreen.intValue = 12 },
                onHomeClick = { currentScreen.intValue = 12 },
                onProductsClick = { currentScreen.intValue = 15 },
                onCameraClick = { currentScreen.intValue = 16 },
                onFreshnessClick = { currentScreen.intValue = 17 },
                onSettingsClick = { currentScreen.intValue = 18 },
                onCustomizeAlertsClick = { currentScreen.intValue = 27 }
            )
        }
        14, 15 -> {
            ProductListScreen(
                onProductClick = { product ->
                    selectedProductId.value = product.id
                    currentScreen.intValue = 24 
                },
                onDeleteProduct = { product -> 
                    coroutineScope.launch {
                        try {
                            val idInt = product.id.toIntOrNull() ?: -1
                            if (idInt != -1) {
                                RetrofitClient.apiService.updateItemStatus(
                                    com.simats.savorshelf.api.UpdateItemStatusRequest(idInt, "consumed")
                                )
                                Toast.makeText(context, "${product.name} marked as consumed!", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onBackClick = { currentScreen.intValue = 12 },
                onHomeClick = { currentScreen.intValue = 12 },
                onProductsClick = { /* Already on Products */ },
                onCameraClick = { currentScreen.intValue = 16 },
                onFreshnessClick = { currentScreen.intValue = 17 },
                onSettingsClick = { currentScreen.intValue = 18 }
            )
        }
        16 -> {
            ScanNewItemScreen(
                onBackClick = { currentScreen.intValue = 12 },
                onHomeClick = { currentScreen.intValue = 12 },
                onProductsClick = { currentScreen.intValue = 15 },
                onCameraClick = { /* Already on Scan page */ },
                onFreshnessClick = { currentScreen.intValue = 17 },
                onSettingsClick = { currentScreen.intValue = 18 },
                onLabeledProductClick = { currentScreen.intValue = 21 },
                onUnlabeledProductClick = { currentScreen.intValue = 19 }
            )
        }
        17 -> {
            FreshnessScreen(
                onBackClick = { currentScreen.intValue = 12 },
                onStartAddingClick = { currentScreen.intValue = 16 },
                onHomeClick = { currentScreen.intValue = 12 },
                onProductsClick = { currentScreen.intValue = 15 },
                onCameraClick = { currentScreen.intValue = 16 },
                onFreshnessClick = { /* Already here */ },
                onSettingsClick = { currentScreen.intValue = 18 },
                onProductClick = { product ->
                    selectedProductId.value = product.id
                    currentScreen.intValue = 24
                },
                onDeleteProduct = { product -> 
                    coroutineScope.launch {
                        try {
                            val idInt = product.id.toIntOrNull() ?: -1
                            if (idInt != -1) {
                                RetrofitClient.apiService.updateItemStatus(
                                    com.simats.savorshelf.api.UpdateItemStatusRequest(idInt, "consumed")
                                )
                                Toast.makeText(context, "${product.name} recorded as consumed!", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
            )
        }
        18 -> {
            SettingsScreen(
                onBackClick = { currentScreen.intValue = 12 },
                onHomeClick = { currentScreen.intValue = 12 },
                onProductsClick = { currentScreen.intValue = 15 },
                onCameraClick = { currentScreen.intValue = 16 },
                onFreshnessClick = { currentScreen.intValue = 17 },
                onSettingsClick = { /* Already here */ },
                onEditProfileClick = { currentScreen.intValue = 25 },
                onChangePasswordClick = { currentScreen.intValue = 26 },
                onAlertTimingClick = { currentScreen.intValue = 27 },
                onAboutClick = { currentScreen.intValue = 28 },
                onPrivacyClick = { currentScreen.intValue = 29 },
                onTermsClick = {
                    previousScreenForTerms.intValue = 18
                    currentScreen.intValue = 30 
                },
                onLogoutClick = { currentScreen.intValue = 33 },
                onDeleteAccountClick = { currentScreen.intValue = 31 }
            )
        }
        19 -> {
            UnlabeledProductsScreen(
                onBackClick = { 
                    if (backStack.size >= 2) currentScreen.intValue = backStack[backStack.size - 2] 
                    else currentScreen.intValue = 12 
                }, 
                onCategoryClick = { category ->
                    selectedUnlabeledCategory.value = category
                    currentScreen.intValue = 20
                }
            )
        }
        20 -> {
            AddUnlabeledItemsScreen(
                category = selectedUnlabeledCategory.value,
                onBackClick = { currentScreen.intValue = 19 },
                onSaveClick = {
                    currentScreen.intValue = 15
                }
            )
        }
        21 -> {
            AddNewProductsScreen(
                onBackClick = { 
                    if (backStack.size >= 2) currentScreen.intValue = backStack[backStack.size - 2] 
                    else currentScreen.intValue = 16 
                },
                onConfirmClick = { date, mfg, lot ->
                    scannedExpiryDate.value = date
                    scannedMfgDate.value = mfg
                    scannedLotNumber.value = lot
                    currentScreen.intValue = 22 
                }
            )
        }
        22 -> {
            ConfirmProductScreen(
                detectedDate = scannedExpiryDate.value,
                detectedMfgDate = scannedMfgDate.value,
                detectedLotNumber = scannedLotNumber.value,
                onBackClick = { currentScreen.intValue = 21 },
                onManualEntryClick = { currentScreen.intValue = 23 },
                onSaveClick = { productName, category, expiryDate, mfgDate, batchNumber, storageType, quantity ->
                    coroutineScope.launch {
                        try {
                            val uId = sharedPrefs.getInt("user_id", -1)
                            if (uId == -1) {
                                Toast.makeText(context, "User session expired", Toast.LENGTH_SHORT).show()
                                return@launch
                            }
                            
                            val frontFile = File(context.cacheDir, "front_image.jpg")
                            val expiryFile = File(context.cacheDir, "expiry_image.jpg")
                            
                            val emptyFile = File.createTempFile("empty", ".jpg", context.cacheDir)
                            val fFile = if (frontFile.exists()) frontFile else emptyFile
                            val eFile = if (expiryFile.exists()) expiryFile else emptyFile

                            val frontBody = MultipartBody.Part.createFormData("front_image", fFile.name, fFile.asRequestBody("image/jpeg".toMediaTypeOrNull()))
                            val expiryBody = MultipartBody.Part.createFormData("expiry_image", eFile.name, eFile.asRequestBody("image/jpeg".toMediaTypeOrNull()))

                            val uIdBody = uId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                            val nameBody = productName.toRequestBody("text/plain".toMediaTypeOrNull())
                            val catBody = category.toRequestBody("text/plain".toMediaTypeOrNull())
                            val storageBody = storageType.toRequestBody("text/plain".toMediaTypeOrNull())
                            val expBody = expiryDate.toRequestBody("text/plain".toMediaTypeOrNull())
                            val mfgBody = mfgDate.toRequestBody("text/plain".toMediaTypeOrNull())
                            val lotBody = batchNumber.toRequestBody("text/plain".toMediaTypeOrNull())
                            val quantBody = quantity.toRequestBody("text/plain".toMediaTypeOrNull())

                            val response = RetrofitClient.apiService.addLabeledProduct(
                                frontBody, expiryBody, uIdBody, nameBody, catBody, storageBody, expBody, mfgBody, lotBody, quantBody
                            )

                            if (response.isSuccessful) {
                                Toast.makeText(context, "Product saved!", Toast.LENGTH_SHORT).show()
                                currentScreen.intValue = 15
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
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }
        23 -> {
            ManualEntryScreen(
                onContinueClick = { date -> 
                    scannedExpiryDate.value = date
                    currentScreen.intValue = 22 
                },
                onBackToScanClick = { currentScreen.intValue = 21 }
            )
        }
        24 -> {
            ProductDetailsScreen(
                productId = selectedProductId.value,
                onBackClick = { currentScreen.intValue = 15 },
                onDeleteClick = {
                    currentScreen.intValue = 15
                }
            )
        }
        25 -> {
            EditProfileScreen(
                onBackClick = { currentScreen.intValue = 18 },
                onSaveClick = { currentScreen.intValue = 35 },
                onHomeClick = { currentScreen.intValue = 12 },
                onProductsClick = { currentScreen.intValue = 15 },
                onCameraClick = { currentScreen.intValue = 16 },
                onFreshnessClick = { currentScreen.intValue = 17 },
                onSettingsClick = { currentScreen.intValue = 18 }
            )
        }
        26 -> {
            ChangePasswordScreen(
                onBackClick = { currentScreen.intValue = 18 },
                onUpdateClick = { currentScreen.intValue = 35 }
            )
        }
        27 -> {
            AlertTimingScreen(
                onBackClick = { 
                    if (backStack.size >= 2) currentScreen.intValue = backStack[backStack.size - 2]
                    else currentScreen.intValue = 18 
                },
                onSaveClick = { currentScreen.intValue = 35 }
            )
        }
        28 -> {
            AboutAppScreen(
                onBackClick = { currentScreen.intValue = 18 },
                onPrivacyPolicyClick = { currentScreen.intValue = 29 },
                onTermsClick = { 
                    previousScreenForTerms.intValue = 28
                    currentScreen.intValue = 30 
                }
            )
        }
        29 -> {
            PrivacyPolicyScreen(
                onBackClick = { currentScreen.intValue = 18 }
            )
        }
        30 -> {
            TermsAndConditionsScreen(
                onBackClick = { currentScreen.intValue = previousScreenForTerms.intValue }
            )
        }
        31 -> {
            DeleteAccountScreen(
                onCancelClick = { currentScreen.intValue = 18 },
                onDeleteConfirm = {
                    sharedPrefs.edit {
                        putBoolean("is_logged_in", false)
                        putInt("user_id", -1)
                        putString("user_name", "")
                        putString("user_email", "")
                    }
                    currentScreen.intValue = 32
                }
            )
        }
        32 -> {
            AccountDeletedScreen(
                onComplete = { currentScreen.intValue = 3 }
            )
        }
        33 -> {
            LogoutConfirmationScreen(
                onCancelClick = { currentScreen.intValue = 18 },
                onLogoutConfirm = {
                    sharedPrefs.edit {
                        putBoolean("is_logged_in", false)
                        putInt("user_id", -1)
                        putString("user_name", "")
                        putString("user_email", "")
                    }
                    currentScreen.intValue = 34
                }
            )
        }
        34 -> {
            LogoutSuccessScreen(
                onComplete = { currentScreen.intValue = 5 }
            )
        }
        35 -> {
            SuccessScreen(
                onComplete = { currentScreen.intValue = 18 }
            )
        }
        36 -> {
            SubscriptionScreen(
                onBackClick = { currentScreen.intValue = 12 },
                onStartPremiumClick = { currentScreen.intValue = 12 }
            )
        }
    }
}

@Composable
fun SuccessScreen(onComplete: () -> Unit) {
    val backgroundUrl = "https://image2url.com/r2/default/images/1772165942529-0db3e05f-ebef-48b7-9133-fc8662d9a661.png"
    val greenColor = Color(0xFF00C853)
    val textDark = Color(0xFF1C2C39)
    val textGray = Color(0xFF6F7E7A)

    LaunchedEffect(Unit) {
        delay(2000)
        onComplete()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NetworkImage(
            model = backgroundUrl,
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
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
                            .size(80.dp)
                            .background(color = greenColor, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Check",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "SUCCESS!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textDark,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Your changes have been\nupdated successfully.\nRedirecting to settings screen...",
                        fontSize = 14.sp,
                        color = textGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Loading Dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(6.dp).background(Color(0xFF64B5F6), CircleShape))
                        Box(modifier = Modifier.size(6.dp).background(Color(0xFF4FC3F7), CircleShape))
                        Box(modifier = Modifier.size(6.dp).background(Color(0xFF29B6F6), CircleShape))
                    }
                }
            }
        }
    }
}
