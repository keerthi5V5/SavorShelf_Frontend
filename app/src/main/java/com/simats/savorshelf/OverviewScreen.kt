package com.simats.savorshelf

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@Composable
fun IntroScreen1() {
    // Colors based on the provided design
    val backgroundColor = Color(0xFFF4F4F0)
    val charcoalColor = Color(0xFF0D614E)
    val textHeadlineColor = Color(0xFF141D1C)
    val textBodyColor = Color(0xFF5A6D66) // Darker grey for better visibility

    val headerBackgroundColor = Color(0xFFE5E5DF)
    val imageAssetUrl = "https://image2url.com/r2/default/images/1771904985641-1c84a847-8323-4552-8b12-4a632560de8b.png"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header / Branding Pill
        Row(
            modifier = Modifier
                .background(color = headerBackgroundColor, shape = RoundedCornerShape(24.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "SAVORSHELF",
                color = charcoalColor,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        // Primary Image Asset Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(32.dp), spotColor = Color.Black.copy(alpha = 0.15f))
                .background(Color.White, shape = RoundedCornerShape(32.dp))
                .clip(RoundedCornerShape(32.dp))
        ) {
            NetworkImage(
                model = imageAssetUrl,
                contentDescription = "AI Scan Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Text Elements
        Text(
            text = "AI-Powered Freshness",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = textHeadlineColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Our smart vision technology tracks your groceries effortlessly. Simply snap a photo to keep tabs on shelf life and reduce waste.",
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium, // Bolder
            fontSize = 14.sp,
            color = textBodyColor,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )


        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun IntroScreen2() {
    val backgroundColor = Color(0xFFF4F4F0)
    val charcoalColor = Color(0xFF0D614E)
    val textHeadlineColor = Color(0xFF141D1C)
    val textBodyColor = Color(0xFF5A6D66) // Darker grey

    val headerBackgroundColor = Color(0xFFE5E5DF)
    val imageAssetUrl = "https://image2url.com/r2/default/images/1771905170050-6bee2399-1892-486e-a47c-9335e022efeb.png"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 24.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .background(color = headerBackgroundColor, shape = RoundedCornerShape(24.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "SAVORSHELF",
                color = charcoalColor,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(32.dp), spotColor = Color.Black.copy(alpha = 0.15f))
                .background(Color.White, shape = RoundedCornerShape(32.dp))
                .clip(RoundedCornerShape(32.dp))
        ) {
            NetworkImage(
                model = imageAssetUrl,
                contentDescription = "Expiry Detection Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Automated Expiry Detection",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = textHeadlineColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Our advanced AI instantly identifies expiry dates and estimates freshness levels. Say goodbye to forgotten food and reduce waste with a single scan.",
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium, // Bolder
            fontSize = 14.sp,
            color = textBodyColor,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )


        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun PagingIndicators(currentPage: Int) {
    val charcoalColor = Color(0xFF0D614E)
    val inactiveColor = Color(0xFFD9D9D9)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(3) { index ->
            if (index == currentPage) {
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(8.dp)
                        .background(color = charcoalColor, shape = RoundedCornerShape(50))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(color = inactiveColor, shape = CircleShape)
                )
            }
            if (index < 2) Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun GetStartedScreen(
    onGetStartedClick: () -> Unit = {},
    onSignInClick: () -> Unit = {}
) {
    val backgroundColor = Color.White
    val titleDarkGreen = Color(0xFF0D614E)
    val coralColor = Color(0xFFD85D44) // Slightly darker coral for better contrast
    val textBodyColor = Color(0xFF5A6D66) // Darker grey

    val pillBackground = Color(0xFFFCF0EA)
    val imageAssetUrl = "https://image2url.com/r2/default/images/1772089602406-d79a74d4-0933-4015-8e86-8f96b87ec3cc.png"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NetworkImage(
            model = imageAssetUrl,
            contentDescription = "Pantry background",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.3f),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Freshness at your",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = titleDarkGreen,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "Fingertips",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = coralColor,
                    textAlign = TextAlign.Center
                )
            }

            Text(
                text = "Smart pantry management for\nthe modern home.",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = textBodyColor,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Button(
                onClick = onGetStartedClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = titleDarkGreen),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Get Started",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "→",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .background(color = pillBackground, shape = RoundedCornerShape(50))
                        .border(1.dp, coralColor.copy(alpha = 0.2f), RoundedCornerShape(50))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Alerts",
                        tint = coralColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Expiry Alerts",
                        fontSize = 12.sp,
                        color = titleDarkGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Row(
                    modifier = Modifier
                        .background(color = pillBackground, shape = RoundedCornerShape(50))
                        .border(1.dp, coralColor.copy(alpha = 0.2f), RoundedCornerShape(50))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Lists",
                        tint = coralColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Smart Lists",
                        fontSize = 12.sp,
                        color = titleDarkGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            val signInText = buildAnnotatedString {
                withStyle(style = SpanStyle(color = textBodyColor)) {
                    append("Already have an account? ")
                }
                withStyle(style = SpanStyle(color = coralColor, fontWeight = FontWeight.Bold)) {
                    append("Sign In")
                }
            }

            Text(
                text = signInText,
                fontSize = 14.sp,
                modifier = Modifier
                    .clickable { onSignInClick() }
                    .padding(bottom = 16.dp)
            )
        }
    }
}

