package com.birdtracks.farmbird.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.birdtracks.farmbird.ui.navigation.Screen
import com.birdtracks.farmbird.viewmodel.AppViewModel
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val gradient: List<Color>
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navController: NavController, viewModel: AppViewModel) {
    val pages = listOf(
        OnboardingPage(
            "Track Wild Birds",
            "Log your bird sightings, explore species details and follow migration routes across the globe.",
            Icons.Filled.NaturePeople,
            listOf(Color(0xFF1B5E20), Color(0xFF388E3C))
        ),
        OnboardingPage(
            "Manage Your Poultry",
            "Keep track of your chickens, ducks, geese and quails. Manage groups, monitor health and plan breeding.",
            Icons.Filled.Egg,
            listOf(Color(0xFF0D47A1), Color(0xFF1565C0))
        ),
        OnboardingPage(
            "Record Eggs & Feeding",
            "Track daily egg production, manage feed stocks, plan feeding schedules and generate detailed reports.",
            Icons.Filled.BarChart,
            listOf(Color(0xFFE65100), Color(0xFFF57C00))
        )
    )

    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            OnboardingPage(page = pages[page])
        }

        // Indicators + Buttons — sits on the dark bottom of the gradient, always readable
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.25f))
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Page indicators
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pages.indices.forEach { index ->
                    val isSelected = index == pagerState.currentPage
                    val width by animateDpAsState(if (isSelected) 24.dp else 8.dp, tween(300), label = "dot")
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(if (isSelected) Color.White else Color.White.copy(alpha = 0.4f))
                    )
                }
            }

            // Buttons
            if (pagerState.currentPage < pages.size - 1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = {
                        viewModel.setOnboardingComplete()
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }) {
                        Text("Skip", color = Color.White.copy(alpha = 0.7f))
                    }
                    Button(
                        onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF1B5E20)
                        )
                    ) {
                        Text("Next", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Filled.ArrowForward, null, modifier = Modifier.size(18.dp))
                    }
                }
            } else {
                Button(
                    onClick = {
                        viewModel.setOnboardingComplete()
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF1B5E20)
                    )
                ) {
                    Text("Get Started", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun OnboardingPage(page: OnboardingPage) {
    // Gradient stays within the page's own dark colors — no white fade so white text is always readable
    val darkEnd = Color(0xFF0A0A0A)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(page.gradient[0], page.gradient[1], page.gradient[1].copy(alpha = 0.85f), darkEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, start = 32.dp, end = 32.dp, bottom = 160.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(80.dp)
                )
            }
            Spacer(Modifier.height(48.dp))
            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = page.description,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
            )
        }
    }
}
