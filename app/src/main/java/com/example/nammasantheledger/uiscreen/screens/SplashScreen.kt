package com.example.nammasantheledger.uiscreen.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

// ─── Colors ───────────────────────────────────────────────────────────────────
private val SpGreenDark  = Color(0xFF14532D)
private val SpGreenMid   = Color(0xFF15803D)
private val SpGreenLight = Color(0xFF16A34A)
private val SpAccent     = Color(0xFF4ADE80)
private val SpWhite      = Color.White
private val SpWhiteDim   = Color.White.copy(alpha = 0.55f)

@Composable
fun SplashScreen(navController: NavController) {

    LaunchedEffect(key1 = true) {
        delay(3000L)
        navController.navigate("login") {
            popUpTo("splash") { inclusive = true }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "sp_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = 1.06f,
        animationSpec = infiniteRepeatable(
            animation  = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sp_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SpGreenDark, SpGreenMid, SpGreenLight)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier            = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── ILLUSTRATION ──────────────────────────────────────────────
            Box(
                modifier         = Modifier
                    .fillMaxWidth()
                    .weight(0.42f),
                contentAlignment = Alignment.Center
            ) {
                SpIllustration()
            }

            // ── LOGO + TITLE ──────────────────────────────────────────────
            Column(
                modifier            = Modifier
                    .weight(0.44f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // Pulsing ₹ circle
                Box(
                    modifier         = Modifier
                        .scale(pulseScale)
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(SpWhite),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier         = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(SpAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = "₹",
                            fontSize   = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = SpGreenDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text          = "Namma Santhe",
                    fontSize      = 28.sp,
                    fontWeight    = FontWeight.ExtraBold,
                    color         = SpWhite,
                    textAlign     = TextAlign.Center,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text          = "Ledger",
                    fontSize      = 28.sp,
                    fontWeight    = FontWeight.ExtraBold,
                    color         = SpAccent,
                    textAlign     = TextAlign.Center,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text      = "Simplified Digital Khata",
                    fontSize  = 14.sp,
                    color     = SpWhiteDim,
                    textAlign = TextAlign.Center
                )
                Text(
                    text      = "for Small Vendors",
                    fontSize  = 14.sp,
                    color     = SpWhiteDim,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Dot indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(SpWhite)
                    )
                    repeat(2) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(SpWhiteDim)
                        )
                    }
                }
            }

            // ── LOADING ───────────────────────────────────────────────────
            Box(
                modifier         = Modifier
                    .weight(0.14f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                SpLoadingDots()
            }
        }
    }
}

// ─── Illustration ─────────────────────────────────────────────────────────────
@Composable
private fun SpIllustration() {
    Box(contentAlignment = Alignment.Center) {

        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.07f))
        )

        Box(
            modifier         = Modifier
                .size(145.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.11f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector        = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint               = Color.White,
                    modifier           = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Icon(
                    imageVector        = Icons.Default.Star,
                    contentDescription = null,
                    tint               = Color(0xFF4ADE80),
                    modifier           = Modifier.size(22.dp)
                )
            }
        }

        SpBubble((-68).dp, (-44).dp, Icons.Default.Favorite,     Color.White)
        SpBubble(  68.dp,  (-44).dp, Icons.Default.Add,          Color(0xFF4ADE80))
        SpBubble((-62).dp,   50.dp,  Icons.Default.Person,       Color.White)
        SpBubble(  62.dp,    50.dp,  Icons.Default.Edit,         Color(0xFF4ADE80))
    }
}

// ─── Bubble ───────────────────────────────────────────────────────────────────
@Composable
private fun SpBubble(offsetX: Dp, offsetY: Dp, icon: ImageVector, tint: Color) {
    Box(
        modifier = Modifier
            .offset(x = offsetX, y = offsetY)
            .size(38.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = tint,
            modifier           = Modifier.size(20.dp)
        )
    }
}

// ─── Loading Dots ─────────────────────────────────────────────────────────────
@Composable
private fun SpLoadingDots() {
    val tr = rememberInfiniteTransition(label = "sp_load")
    val a1 by tr.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(500), RepeatMode.Reverse, StartOffset(0)
        ), label = "sp_a1"
    )
    val a2 by tr.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(500), RepeatMode.Reverse, StartOffset(160)
        ), label = "sp_a2"
    )
    val a3 by tr.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(500), RepeatMode.Reverse, StartOffset(320)
        ), label = "sp_a3"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            listOf(a1, a2, a3).forEach { alpha ->
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = alpha))
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = "Loading...", color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp)
    }
}