package com.birdtracks.farmbird.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.birdtracks.farmbird.ui.navigation.Screen
import com.birdtracks.farmbird.ui.theme.*
import com.birdtracks.farmbird.viewmodel.AppViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, viewModel: AppViewModel) {
    val isOnboardingComplete by viewModel.isOnboardingComplete.collectAsState()
    var startAnimation by remember { mutableStateOf(false) }
    var showText by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.3f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )
    val rotation by animateFloatAsState(
        targetValue = if (startAnimation) 0f else -180f,
        animationSpec = tween(800, easing = EaseOutBack),
        label = "rotation"
    )
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(600),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        delay(200)
        startAnimation = true
        delay(600)
        showText = true
        delay(1800)
        navController.navigate(if (isOnboardingComplete) Screen.Dashboard.route else Screen.Onboarding.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFF1B5E20), Color(0xFF2D6A4F), Color(0xFF52B788)))
            ),
        contentAlignment = Alignment.Center
    ) {
        // Decorative circles
        Box(
            modifier = Modifier
                .size(300.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-60).dp)
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
                .align(Alignment.BottomStart)
                .offset(x = (-40).dp, y = 40.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .scale(scale)
                    .rotate(rotation)
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Air,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }

            AnimatedVisibility(
                visible = showText,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 2 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "BirdTrack Farm",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Track birds and manage your poultry.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Loading indicator at bottom
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            color = Color.White.copy(alpha = 0.6f),
            strokeWidth = 2.dp,
            trackColor = Color.White.copy(alpha = 0.2f)
        )
    }
}
