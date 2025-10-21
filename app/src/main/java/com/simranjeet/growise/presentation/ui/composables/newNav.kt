package com.simranjeet.growise.presentation.ui.composables

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
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
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.simranjeet.growise.presentation.ui.theme.GroWiseTheme
import kotlin.math.PI
import kotlin.math.sin

@RequiresApi(Build.VERSION_CODES.S)
fun getRenderEffect(): RenderEffect {
    val blurEffect = RenderEffect.createBlurEffect(80f, 80f, Shader.TileMode.MIRROR)

    val alphaMatrix = RenderEffect.createColorFilterEffect(
        ColorMatrixColorFilter(
            ColorMatrix(
                floatArrayOf(
                    1f, 0f, 0f, 0f, 0f,
                    0f, 1f, 0f, 0f, 0f,
                    0f, 0f, 1f, 0f, 0f,
                    0f, 0f, 0f, 50f, -5000f
                )
            )
        )
    )

    return RenderEffect.createChainEffect(alphaMatrix, blurEffect)
}

@Composable
fun MainScreen() {
    val isMenuExtended = remember { mutableStateOf(false) }
    val selectedTab = remember { mutableStateOf(0) }

    // Start animation automatically when screen opens
    LaunchedEffect(Unit) {
        isMenuExtended.value = true
    }

    val fabAnimationProgress by animateFloatAsState(
        targetValue = if (isMenuExtended.value) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
    )

    val clickAnimationProgress by animateFloatAsState(
        targetValue = if (isMenuExtended.value) 1f else 0f,
        animationSpec = tween(durationMillis = 40, easing = LinearEasing)
    )

    val renderEffect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getRenderEffect().asComposeRenderEffect()
    } else null

    MainScreen(
        renderEffect = renderEffect,
        fabAnimationProgress = fabAnimationProgress,
        clickAnimationProgress = clickAnimationProgress,
        selectedTab = selectedTab.value,
        onTabSelected = { tab -> selectedTab.value = tab },
        toggleAnimation = { isMenuExtended.value = !isMenuExtended.value }
    )
}

@Composable
fun MainScreen(
    renderEffect: androidx.compose.ui.graphics.RenderEffect?,
    fabAnimationProgress: Float = 0f,
    clickAnimationProgress: Float = 0f,
    selectedTab: Int = 0,
    onTabSelected: (Int) -> Unit = {},
    toggleAnimation: () -> Unit = {}
) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(bottom = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        TabSlider(
            renderEffect = renderEffect,
            animationProgress = fabAnimationProgress,
            selectedTab = selectedTab,
            onTabSelected = onTabSelected
        )
        Circle(
            color = Color.White,
            animationProgress = clickAnimationProgress
        )
    }
}

@Composable
fun Circle(color: Color, animationProgress: Float) {
    val animationValue = sin(PI * animationProgress).toFloat()
    Box(
        modifier = Modifier
            .padding(10.dp)
            .size(56.dp)
            .scale(2 - animationValue)
            .border(
                width = 2.dp,
                color = color.copy(alpha = color.alpha * animationValue),
                shape = CircleShape
            )
    )
}

@Composable
fun TabSlider(
    animationProgress: Float = 0f,
    renderEffect: androidx.compose.ui.graphics.RenderEffect? = null,
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
            .wrapContentSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier.size(100.dp)
        ) {
            Box(
                modifier = Modifier.size(100.dp)
                    .scale(FastOutSlowInEasing.transform(0f, 1f, animationProgress))
                    .clip(RoundedCornerShape(35.dp))
                    .background(Color(0xFF1C1C1C))
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(horizontal = 40.dp)
                .scale(FastOutSlowInEasing.transform(0f, 1f, animationProgress))
                .clip(RoundedCornerShape(35.dp))
                .background(Color(0xFF1C1C1C))
                .padding(8.dp)
                .onGloballyPositioned { coordinates ->
                    with(density) {
                        tabWidth.value = (coordinates.size.width / 4f)
                    }
                }
        ) {
            Box(
                modifier = Modifier
                    .offset(x = with(density) { (tabWidth.value * indicatorOffset).toDp() })
                    .width(with(density) { tabWidth.value.toDp() })
                    .fillMaxHeight()
                    .padding(4.dp)
                    .scale(scaleX = 1f, scaleY = indicatorScaleY)
                    .clip(RoundedCornerShape(35.dp))
                    .background(Color.White)
            )

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                TabItem(
                    icon = Icons.Filled.Home,
                    outlinedIcon = Icons.Outlined.Home,
                    isSelected = selectedTab == 0,
                    animationProgress = animationProgress,
                    onClick = { onTabSelected(0) },
                    opacity = LinearEasing.transform(0.2f, 1f, animationProgress)
                )
                Spacer(modifier = Modifier.width(0.dp))

                TabItem(
                    icon = Icons.Filled.ShoppingCart,
                    outlinedIcon = Icons.Outlined.ShoppingCart,
                    isSelected = selectedTab == 1,
                    animationProgress = animationProgress,
                    onClick = { onTabSelected(1) },
                    opacity = LinearEasing.transform(0.4f, 1f, animationProgress)
                )
                Spacer(modifier = Modifier.width(0.dp))

                TabItem(
                    icon = Icons.Filled.AccountBox,
                    outlinedIcon = Icons.Outlined.AccountBox,
                    isSelected = selectedTab == 2,
                    animationProgress = animationProgress,
                    onClick = { onTabSelected(2) },
                    opacity = LinearEasing.transform(0.6f, 1f, animationProgress)
                )
                Spacer(modifier = Modifier.width(0.dp))

                TabItem(
                    icon = Icons.Filled.Settings,
                    outlinedIcon = Icons.Outlined.Settings,
                    isSelected = selectedTab == 3,
                    animationProgress = animationProgress,
                    onClick = { onTabSelected(3) },
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
    animationProgress: Float,
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

@Composable
@Preview(device = "id:pixel_4a", showBackground = true, backgroundColor = 0xFF3A2F6E)
private fun MainScreenPreview() {
    GroWiseTheme { MainScreen() }
}