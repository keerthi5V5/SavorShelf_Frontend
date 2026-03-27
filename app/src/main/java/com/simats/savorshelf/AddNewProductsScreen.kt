package com.simats.savorshelf

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MultipartBody
import com.simats.savorshelf.api.RetrofitClient

enum class ImageSourceType {
    FRONT, EXPIRY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewProductsScreen(
    onBackClick: () -> Unit,
    onConfirmClick: (String, String, String) -> Unit = { _, _, _ -> }
) {
    val bgColor = Color(0xFFE9F0EC)
    val textPrimary = Color(0xFF141D1C)
    val textSecondary = Color(0xFF5A6D66)

    val primaryGreen = Color(0xFF0D614E)

    val context = LocalContext.current

    // Local states
    val frontImageBitmapState = remember { mutableStateOf<Bitmap?>(null) }
    val frontImageUriState = remember { mutableStateOf<Uri?>(null) }
    val expiryImageBitmapState = remember { mutableStateOf<Bitmap?>(null) }
    val expiryImageUriState = remember { mutableStateOf<Uri?>(null) }
    val detectedDateState = remember { mutableStateOf<String?>(null) }
    val detectedMfgDateState = remember { mutableStateOf<String?>(null) }
    val detectedLotNumberState = remember { mutableStateOf<String?>(null) }
    val isScanningState = remember { mutableStateOf(false) }

    var activeImageSource by remember { mutableStateOf<ImageSourceType?>(null) }
    
    // Permission Dialogs
    var showCameraPermissionDialog by remember { mutableStateOf(false) }
    
    // Recognizing Text
    val coroutineScope = rememberCoroutineScope()
    fun performRemoteOcr(ctx: android.content.Context, uri: Uri?, bitmap: Bitmap?) {
        coroutineScope.launch {
            isScanningState.value = true
            detectedDateState.value = "Processing on server..."
            try {
                val file = File(ctx.cacheDir, "scan_image.jpg")
                val outputStream = FileOutputStream(file)
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                } else if (uri != null) {
                    val inputStream = ctx.contentResolver.openInputStream(uri)
                    inputStream?.copyTo(outputStream)
                    inputStream?.close()
                }
                outputStream.close()

                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

                val response = RetrofitClient.apiService.scanProductInfo(body)
                if (response.isSuccessful) {
                    val scanResponse = response.body()
                    val fullText = scanResponse?.detected_text ?: ""
                    
                    val serverExpiry = scanResponse?.extracted_data?.expiry_date
                    val serverMfg = scanResponse?.extracted_data?.mfg_date
                    val serverLot = scanResponse?.extracted_data?.lot_number
                    
                    // 1. Expiry Extraction
                    if (!serverExpiry.isNullOrBlank()) {
                        detectedDateState.value = serverExpiry
                    } else if (fullText.isNotBlank()) {
                         val expRegex = Regex("""(?i)(?:EXP|USE BY|BEST BEFORE|ED)[\s:]*(\d{1,2}[/.-]\d{1,2}[/.-](?:\d{2}|\d{4})|\d{4}[/.-]\d{1,2}[/.-]\d{1,2})""")
                         val match = expRegex.find(fullText)
                         if (match != null) {
                             detectedDateState.value = match.groupValues[1]
                         } else {
                             val fallbackRegex = Regex("""\b(\d{1,2}[/.-]\d{1,2}[/.-](?:\d{2}|\d{4})|\d{4}[/.-]\d{1,2}[/.-]\d{1,2})\b""")
                             detectedDateState.value = fallbackRegex.find(fullText)?.value ?: "Date not clearly found. Please rescan."
                         }
                    } else {
                        detectedDateState.value = "Date not clearly found. Please rescan."
                    }

                    // 2. MFG Date Extraction
                    if (!serverMfg.isNullOrBlank()) {
                        detectedMfgDateState.value = serverMfg
                    } else if (fullText.isNotBlank()) {
                        val mfgRegex = Regex("""(?i)(?:MFG|MFD|PKD|PKT|DATE OF PACK)[\s:]*(\d{1,2}[/.-]\d{1,2}[/.-](?:\d{2}|\d{4})|\d{4}[/.-]\d{1,2}[/.-]\d{1,2})""")
                        detectedMfgDateState.value = mfgRegex.find(fullText)?.groupValues?.get(1)
                    }

                    // 3. Lot/Batch Extraction
                    if (!serverLot.isNullOrBlank()) {
                        detectedLotNumberState.value = serverLot
                    } else if (fullText.isNotBlank()) {
                        val lotRegex = Regex("""(?i)(?:LOT|BATCH|BN|B\.?NO|L\.)[\s:]*([A-Z0-9-]+)""")
                        detectedLotNumberState.value = lotRegex.find(fullText)?.groupValues?.get(1)
                    }
                } else {
                    detectedDateState.value = "Server error: ${response.code()}"
                }
            } catch (e: Exception) {
                detectedDateState.value = "Network error: ${e.message}"
            } finally {
                isScanningState.value = false
            }
        }
    }

    fun saveToCache(ctx: android.content.Context, filename: String, uri: Uri?, bitmap: Bitmap?) {
        try {
            val file = File(ctx.cacheDir, filename)
            val outputStream = FileOutputStream(file)
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            } else if (uri != null) {
                val inputStream = ctx.contentResolver.openInputStream(uri)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
            }
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Camera Launcher
    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            when (activeImageSource) {
                ImageSourceType.FRONT -> {
                    frontImageBitmapState.value = bitmap
                    frontImageUriState.value = null // clear out uri if using bitmap
                    saveToCache(context, "front_image.jpg", null, bitmap)
                }
                ImageSourceType.EXPIRY -> {
                    expiryImageBitmapState.value = bitmap
                    expiryImageUriState.value = null
                    saveToCache(context, "expiry_image.jpg", null, bitmap)
                    // Immediately try OCR
                    performRemoteOcr(context, null, bitmap)
                }
                null -> {}
            }
        }
    }

    // Camera Permission Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            takePictureLauncher.launch(null)
        } else {
            Toast.makeText(context, "Camera permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

    // Gallery Launcher (Photo Picker)
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            when (activeImageSource) {
                ImageSourceType.FRONT -> {
                    frontImageUriState.value = uri
                    frontImageBitmapState.value = null // clear out bitmap if using uri
                    saveToCache(context, "front_image.jpg", uri, null)
                }
                ImageSourceType.EXPIRY -> {
                    expiryImageUriState.value = uri
                    expiryImageBitmapState.value = null
                    saveToCache(context, "expiry_image.jpg", uri, null)
                    // Immediately try OCR
                    performRemoteOcr(context, uri, null)
                }
                null -> {}
            }
        }
    }

    // Explicit Gallery Permission Launcher (No longer needed with Photo Picker)

    val sharedPrefs = context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)


    fun launchCameraFlow() {
        val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.CAMERA
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            takePictureLauncher.launch(null)
        } else {
            val hasSeenCameraDialog = sharedPrefs.getBoolean("has_seen_camera", false)
            if (hasSeenCameraDialog) {
                cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            } else {
                showCameraPermissionDialog = true
            }
        }
    }

    fun launchGalleryFlow() {
        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    if (showCameraPermissionDialog) {
        CameraPermissionScreen(
            onAllowClick = {
                showCameraPermissionDialog = false
                sharedPrefs.edit().putBoolean("has_seen_camera", true).apply()
                cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            },
            onDenyClick = { 
                showCameraPermissionDialog = false 
                sharedPrefs.edit().putBoolean("has_seen_camera", true).apply()
            }
        )
        return
    }


    Scaffold(
        containerColor = bgColor,
        bottomBar = {
            // Only show Confirm & Continue if both images are captured (front + expiry)
            val hasFrontImage = frontImageBitmapState.value != null || frontImageUriState.value != null
            val hasExpiryImage = expiryImageBitmapState.value != null || expiryImageUriState.value != null
            
            if (hasFrontImage && hasExpiryImage) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .navigationBarsPadding()
                ) {
                    Button(
                        onClick = { onConfirmClick(detectedDateState.value ?: "", detectedMfgDateState.value ?: "", detectedLotNumberState.value ?: "") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Confirm & Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
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
                        text = "Scan Labeled Item",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            // How to scan Section
            if (frontImageBitmapState.value == null && frontImageUriState.value == null && expiryImageBitmapState.value == null && expiryImageUriState.value == null) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "Info",
                            tint = Color(0xFFE26027),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "How to scan",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFFE26027)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "To accurately track your pantry, please provide two clear images: the product front view and the expiry date label.",
                                fontSize = 13.sp,
                                color = textSecondary,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StepCircle("1", "FRONT VIEW")
                                Spacer(modifier = Modifier.width(16.dp))
                                StepCircle("2", "EXPIRY LABEL")
                            }
                        }
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(primaryGreen, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("1", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "How to scan", fontWeight = FontWeight.Bold, color = textPrimary)
                            Text(
                                "Upload two images: (1) Product front view and (2) Expiry date label. Our AI will identify the product and extract the expiry date.",
                                color = textSecondary, fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium
                            )

                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Front Image Upload Area
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Inventory2,
                            contentDescription = null,
                            tint = primaryGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Product Front Image",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = textPrimary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFCE8E8), RoundedCornerShape(16.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Required", fontSize = 10.sp, color = Color(0xFFE53935), fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val modelFront = frontImageBitmapState.value ?: frontImageUriState.value
                    if (modelFront == null) {
                        UploadPlaceholder(
                            title = "Upload Product Photo",
                            subtitle = "Clear front view of the product",
                            onCameraClick = {
                                activeImageSource = ImageSourceType.FRONT
                                launchCameraFlow()
                            },
                            onGalleryClick = {
                                activeImageSource = ImageSourceType.FRONT
                                launchGalleryFlow()
                            }
                        )
                    } else {
                        // Show uploaded front image
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            NetworkImage(
                                model = modelFront,
                                contentDescription = "Front Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(primaryGreen, RoundedCornerShape(16.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Uploaded", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Change image",
                            color = textSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    activeImageSource = ImageSourceType.FRONT
                                    launchCameraFlow()
                                }
                                .padding(8.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold
                        )

                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Expiry Date Label Upload Area
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFFF6ED), // Orange tint
                border = BorderStroke(1.dp, Color(0xFFFFE0C2)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.CameraAlt,
                            contentDescription = null,
                            tint = Color(0xFFE26027),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Expiry Date Label",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = textPrimary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFCE8E8), RoundedCornerShape(16.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Required", fontSize = 10.sp, color = Color(0xFFE53935), fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val modelExpiry = expiryImageBitmapState.value ?: expiryImageUriState.value
                    if (modelExpiry == null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFFFBCA1).copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(Color(0xFFFFE8DD), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Image,
                                        contentDescription = null,
                                        tint = Color(0xFFE26027),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Focus on Expiry Date", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = textPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Clear photo of the expiry date label", fontSize = 12.sp, color = textSecondary)
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Button(
                                    onClick = {
                                        activeImageSource = ImageSourceType.EXPIRY
                                        launchCameraFlow()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D614E)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Icon(Icons.Outlined.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Start Scan", fontSize = 13.sp)
                                }
                            }
                        }
                    } else {
                        // Imaged Uploaded + Extracted Details
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            NetworkImage(
                                model = modelExpiry,
                                contentDescription = "Expiry Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        if (detectedDateState.value != null) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                border = BorderStroke(1.dp, primaryGreen.copy(alpha=0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.Verified,
                                            contentDescription = null,
                                            tint = primaryGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Date Detected!",
                                            fontSize = 12.sp,
                                            color = textPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "EXP: ${detectedDateState.value}",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = textPrimary
                                    )
                                    if (!detectedMfgDateState.value.isNullOrEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "MFG: ${detectedMfgDateState.value}",
                                            fontSize = 14.sp,
                                            color = textSecondary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    if (!detectedLotNumberState.value.isNullOrEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Lot: ${detectedLotNumberState.value}",
                                            fontSize = 14.sp,
                                            color = textSecondary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Rescan expiry date",
                            color = textSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    activeImageSource = ImageSourceType.EXPIRY
                                    launchCameraFlow()
                                }
                                .padding(8.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold
                        )

                    }
                }
            }
            
            // Example Images Section
            if (frontImageBitmapState.value == null && frontImageUriState.value == null && expiryImageBitmapState.value == null && expiryImageUriState.value == null) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "EXAMPLE IMAGES",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = textSecondary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Front Image Example
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(0.8f) 
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            NetworkImage(
                                model = "https://image2url.com/r2/default/images/1772077653265-7ca43531-9f4f-4426-808b-ae94051f9215.png",
                                contentDescription = "Example Front Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(8.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("Front", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                            }
                        }

                        // Expiry Image Example
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(0.8f)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            NetworkImage(
                                model = R.drawable.ex_expiry,
                                contentDescription = "Example Expiry Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(8.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("Expiry", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFAEBE1), 
                        border = BorderStroke(1.dp, Color(0xFFEADBCE)), 
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Lightbulb,
                                contentDescription = null,
                                tint = Color(0xFFE26027),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tip: Take clear, well-lit photos for best results",
                                fontSize = 11.sp,
                                color = Color(0xFFE26027),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
}

@Composable
fun StepCircle(number: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .background(Color(0xFFE26027), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(number, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, color = Color(0xFFE26027), fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun UploadPlaceholder(
    title: String,
    subtitle: String,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE5EBE8)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFF1F5F3), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Inventory2,
                    contentDescription = null,
                    tint = Color(0xFFA1AFAB),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color(0xFF1B2625))
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, fontSize = 12.sp, color = Color(0xFF6F7E7A))
            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.Center) {
                Button(
                    onClick = onCameraClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D614E)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Outlined.CameraAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Camera", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = onGalleryClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5EBE8)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Outlined.FileUpload, contentDescription = null, tint = Color(0xFF1B2625), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Gallery", color = Color(0xFF1B2625), fontSize = 12.sp)
                }
            }
        }
    }
}
