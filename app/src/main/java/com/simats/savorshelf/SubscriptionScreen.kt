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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding

@Composable
fun SubscriptionScreen(
    onBackClick: () -> Unit = {},
    onStartPremiumClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? android.app.Activity
    
    val primaryGreen = Color(0xFF0D614E)
    val mintBg = Color(0xFFDFF6E9) // Fresh Mint Green
    val textPrimary = Color(0xFF141D1C)
    val textSecondary = Color(0xFF4A5D57)

    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            mintBg,
            Color.White
        )
    )
    val goldColor = Color(0xFFD4AF37) // A slightly darker gold for white bg
    val lightBlue = Color(0xFF0284C7)
    
    val logoUrl = "https://image2url.com/r2/default/images/1772164873232-c166ef95-c445-4f7d-9852-4f48d008db1c.png"

    // Billing Logic State
    var productDetailsState by remember { mutableStateOf<ProductDetails?>(null) }
    var billingClientState by remember { mutableStateOf<BillingClient?>(null) }

    // Initialize BillingClient
    DisposableEffect(Unit) {
        logDebugInformation(context)
        
        val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
            Log.d("SubscriptionScreen", "onPurchasesUpdated: ${billingResult.responseCode}")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    handlePurchase(purchase, billingClientState, context, onStartPremiumClick)
                }
            } else {
                handleBillingError(billingResult, context)
            }
        }

        val client = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
            .build()
        
        billingClientState = client

        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    querySubscriptionDetails(client) { details ->
                        productDetailsState = details
                    }
                } else {
                    Log.e("SubscriptionScreen", "Billing setup failed: ${billingResult.debugMessage}")
                }
            }
            override fun onBillingServiceDisconnected() {
                Log.d("SubscriptionScreen", "Billing service disconnected")
            }
        })

        onDispose {
            client.endConnection()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo Section
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = Color.White.copy(alpha = 0.1f)
                     )
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
            ) {
                NetworkImage(
                    model = logoUrl,
                    contentDescription = "SavorShelf Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = "SavorShelf",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textPrimary,
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Premium Badge
            Surface(
                color = goldColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp),
                border = null,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "PREMIUM",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = goldColor,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Description
            Text(
                text = "Unlock smart features designed to track\nexpiry and reduce food waste effectively",
                fontSize = 15.sp,
                color = textSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Features Column
            FeatureItem(
                icon = Icons.Default.FlashOn,
                iconTint = goldColor,
                title = "AI Expiry Scanning",
                subtitle = "Automated date extraction from labels"
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            FeatureItem(
                icon = Icons.Default.NotificationsActive,
                iconTint = Color(0xFFF43F5E), // Rose color
                title = "Smart Freshness Alerts",
                subtitle = "Real-time notifications before items expire"
            )

            Spacer(modifier = Modifier.height(16.dp))

            FeatureItem(
                icon = Icons.Default.Psychology,
                iconTint = lightBlue,
                title = "Insights & Analytics",
                subtitle = "Track your waste reduction impact"
            )

            Spacer(modifier = Modifier.height(16.dp))

            FeatureItem(
                icon = Icons.Default.Diamond,
                iconTint = Color(0xFFA855F7), // Purple
                title = "Unlimited Storage",
                subtitle = "Manage thousands of items hassle-free"
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(16.dp))

            // Pricing Card (Medium Mint Green)
            Surface(
                color = Color(0xFFB9E8CE),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Premium Plan",
                        color = primaryGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "₹99 / month",
                        color = primaryGreen,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CTA Button
            Button(
                onClick = {
                    if (activity != null && billingClientState != null && productDetailsState != null) {
                        launchSubscriptionFlow(activity, billingClientState!!, productDetailsState!!)
                    } else {
                        Toast.makeText(context, "Subscription not available. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(28.dp),
                        spotColor = primaryGreen.copy(alpha = 0.3f)
                    ),
                colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
                shape = RoundedCornerShape(28.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "START PREMIUM",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Arrow",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer Links
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "By continuing, you agree to our ",
                    fontSize = 12.sp,
                    color = textSecondary.copy(alpha = 0.6f)
                )
                Text(
                    text = "Terms & Privacy Policy",
                    fontSize = 12.sp,
                    color = primaryGreen,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { /* Add navigation */ }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Dismiss Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .height(48.dp)
                    .clickable { onBackClick() },
                shape = RoundedCornerShape(24.dp),
                color = primaryGreen.copy(alpha = 0.05f),
                border = BorderStroke(1.dp, primaryGreen.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Maybe later",
                        fontSize = 16.sp,
                        color = primaryGreen,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

private const val SUBSCRIPTION_SKU = "univault_premium_subscription"
private const val TEST_SUBSCRIPTION_SKU = "android.test.purchased"

private fun logDebugInformation(context: Context) {
    Log.d("SubscriptionScreen", "=== DEBUG INFORMATION ===")
    Log.d("SubscriptionScreen", "Package name: ${context.packageName}")
    try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        Log.d("SubscriptionScreen", "Version code: ${packageInfo.longVersionCode}")
        Log.d("SubscriptionScreen", "Version name: ${packageInfo.versionName}")
    } catch (e: Exception) {
        Log.w("SubscriptionScreen", "Unable to get package info: ${e.message}")
    }
    Log.d("SubscriptionScreen", "Product ID: $SUBSCRIPTION_SKU")
    Log.d("SubscriptionScreen", "=========================")
}

private fun querySubscriptionDetails(client: BillingClient, onResult: (ProductDetails?) -> Unit) {
    val productList = listOf(
        QueryProductDetailsParams.Product.newBuilder()
            .setProductId(SUBSCRIPTION_SKU)
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
    )
    val params = QueryProductDetailsParams.newBuilder().setProductList(productList).build()

    client.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
            onResult(productDetailsList[0])
        } else {
            // Try test product
            val testProductList = listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(TEST_SUBSCRIPTION_SKU)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            )
            val testParams = QueryProductDetailsParams.newBuilder().setProductList(testProductList).build()
            client.queryProductDetailsAsync(testParams) { testResult, testList ->
                if (testResult.responseCode == BillingClient.BillingResponseCode.OK && testList.isNotEmpty()) {
                    onResult(testList[0])
                } else {
                    onResult(null)
                }
            }
        }
    }
}

private fun launchSubscriptionFlow(activity: android.app.Activity, client: BillingClient, details: ProductDetails) {
    val productDetailsParamsList = if (details.productType == BillingClient.ProductType.SUBS) {
        val selectedOffer = details.subscriptionOfferDetails?.getOrNull(0) ?: return
        listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(details)
                .setOfferToken(selectedOffer.offerToken)
                .build()
        )
    } else {
        listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(details)
                .build()
        )
    }

    val billingFlowParams = BillingFlowParams.newBuilder()
        .setProductDetailsParamsList(productDetailsParamsList)
        .build()

    client.launchBillingFlow(activity, billingFlowParams)
}

private fun handlePurchase(purchase: Purchase, client: BillingClient?, context: Context, onSuccess: () -> Unit) {
    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
        if (!purchase.isAcknowledged && client != null) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            client.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    completeSubscription(context, onSuccess)
                }
            }
        } else {
            completeSubscription(context, onSuccess)
        }
    }
}

private fun completeSubscription(context: Context, onSuccess: () -> Unit) {
    Toast.makeText(context, "Subscription successful! Welcome to Premium!", Toast.LENGTH_LONG).show()
    val sharedPref = context.getSharedPreferences("subscription_prefs", Context.MODE_PRIVATE)
    sharedPref.edit().apply {
        putBoolean("is_premium_user", true)
        putLong("subscription_time", System.currentTimeMillis())
        apply()
    }
    onSuccess()
}

private fun handleBillingError(billingResult: BillingResult, context: Context) {
    when (billingResult.responseCode) {
        BillingClient.BillingResponseCode.USER_CANCELED -> {
            Toast.makeText(context, "Purchase canceled", Toast.LENGTH_SHORT).show()
        }
        BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
            Toast.makeText(context, "You already have an active subscription", Toast.LENGTH_SHORT).show()
        }
        else -> {
            Toast.makeText(context, "Purchase failed: ${billingResult.debugMessage}", Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
fun FeatureItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 2.dp,
        border = null
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconTint.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF141D1C)
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color(0xFF5A6D66),
                    fontWeight = FontWeight.Medium
                )
            }

            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Included",
                tint = Color(0xFF22C55E),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
