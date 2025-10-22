package com.simranjeet.growise.presentation.ui.composables

import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.simranjeet.growise.GrowiseApp
import com.simranjeet.growise.R
import com.simranjeet.growise.di.DIContainer

@Composable
fun MainScreen(
    onLogoutClicked: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 100.dp),
            contentAlignment = Alignment.Center
        ) {
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavigation()
        }
        LogoutButton(
            onClick = onLogoutClicked,
            modifier = Modifier
                .align(Alignment.TopStart) // This is the key
                .padding(16.dp)
        )
    }
}
@Composable
fun LogoutButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Logout",
            tint = Color.Black
        )
    }
}

@Composable
fun BottomNavigation() {
    val isMenuExtended = remember { mutableStateOf(false) }
    val selectedTab = remember { mutableStateOf(0) }

    // Start animation automatically when screen opens
    LaunchedEffect(Unit) { isMenuExtended.value = true }

    val fabAnimationProgress by animateFloatAsState(
        targetValue = if (isMenuExtended.value) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
    )
    Box(
        Modifier
            .fillMaxSize()
            .padding(bottom = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {

        TabSlider(
            animationProgress = fabAnimationProgress,
            selectedTab = selectedTab.value,
            onTabSelected = { tab -> selectedTab.value = tab }
        )
    }
}

@Composable
fun TabSlider(
    animationProgress: Float = 0f,
    selectedTab: Int = 0,
    onTabSelected: (Int) -> Unit = {}
) {
    val density = LocalDensity.current
    val tabWidth = remember { mutableFloatStateOf(0f) }

    val indicatorOffset by animateFloatAsState(
        targetValue = selectedTab.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    )

    val fraction = indicatorOffset % 1f
    val normalizedFraction = if (fraction > 0.5f) 1f - fraction else fraction
    val indicatorScaleY = 1f - (normalizedFraction * 2f * 0.9f)

    Box(
        Modifier
            .wrapContentSize()
            .padding( vertical = 30.dp),
        contentAlignment = Alignment.BottomCenter
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 30.dp)
                .scale(FastOutSlowInEasing.transform(0f, 1f, animationProgress))
                .clip(RoundedCornerShape(35.dp))
                .background(Color(0xFF1C1C1C))
                .padding(2.dp)
                .onGloballyPositioned { coordinates ->
                    with(density) { tabWidth.floatValue = (coordinates.size.width / 5f) }
                }
        ) {
            Box(
                modifier = Modifier
                    .offset(x = with(density) { (tabWidth.floatValue * indicatorOffset).toDp() })
                    .width(with(density) { tabWidth.floatValue.toDp() })
                    .fillMaxHeight()
                    .padding(4.dp)
                    .scale(scaleX = 1f, scaleY = indicatorScaleY)
                    .clip(CircleShape)
                    .background(Color.White)
            )

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                TabItem(
                    icon = ImageVector.vectorResource(R.drawable.ic_home_filled),
                    outlinedIcon = ImageVector.vectorResource(R.drawable.ic_home),
                    isSelected = selectedTab == 0,
                    onClick = { onTabSelected(0) },
                    opacity = LinearEasing.transform(0.2f, 1f, animationProgress)
                )
                Spacer(modifier = Modifier.width(0.dp))

                TabItem(
                    icon = ImageVector.vectorResource(R.drawable.ic_money_bag_filled),
                    outlinedIcon = ImageVector.vectorResource(R.drawable.ic_money_bag),
                    isSelected = selectedTab == 1,
                    onClick = { onTabSelected(1) },
                    opacity = LinearEasing.transform(0.4f, 1f, animationProgress)
                )
                Spacer(modifier = Modifier.width(0.dp))

                TabItem(
                    icon = ImageVector.vectorResource(R.drawable.ic_piggy_bank_filled),
                    outlinedIcon = ImageVector.vectorResource(R.drawable.ic_piggy_bank),
                    isSelected = selectedTab == 2,
                    onClick = { onTabSelected(2) },
                    opacity = LinearEasing.transform(0.6f, 1f, animationProgress)
                )
                Spacer(modifier = Modifier.width(0.dp))

                TabItem(
                    icon = ImageVector.vectorResource(R.drawable.ic_charts_filled),
                    outlinedIcon = ImageVector.vectorResource(R.drawable.ic_charts),
                    isSelected = selectedTab == 3,
                    onClick = { onTabSelected(3) },
                    opacity = LinearEasing.transform(0.8f, 1f, animationProgress)
                )
                Spacer(modifier = Modifier.width(0.dp))

                TabItem(
                    icon = ImageVector.vectorResource(R.drawable.ic_bot_filled),
                    outlinedIcon = ImageVector.vectorResource(R.drawable.ic_bot),
                    isSelected = selectedTab == 4,
                    onClick = { onTabSelected(4) },
                    opacity = LinearEasing.transform(0.8f, 1f, animationProgress)
                )

            }
        }
    }
}

@Composable
fun TabItem(
    icon: ImageVector,
    outlinedIcon: ImageVector,
    isSelected: Boolean,
    opacity: Float = 1f,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(54.dp)
    ) {
        Icon(
            imageVector = if (isSelected) icon else outlinedIcon,
            contentDescription = null,
            tint = if (isSelected) Color(0xFF1C1C1C) else Color.White.copy(alpha = 0.6f * opacity),
            modifier = Modifier.size(24.dp)
        )
    }
}

private fun androidx.compose.animation.core.Easing.transform(
    start: Float,
    end: Float,
    fraction: Float
): Float {
    return androidx.compose.ui.util.lerp(start, end, this.transform(fraction))
}