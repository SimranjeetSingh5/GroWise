//package com.simranjeet.growise.presentation.ui.composables
//
//import android.graphics.ColorMatrix
//import android.graphics.ColorMatrixColorFilter
//import android.graphics.RenderEffect
//import android.graphics.Shader
//import android.os.Build
//import androidx.annotation.RequiresApi
//import androidx.compose.animation.core.FastOutSlowInEasing
//import androidx.compose.animation.core.LinearEasing
//import androidx.compose.animation.core.animateFloatAsState
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.AccountBox
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Email
//import androidx.compose.material.icons.filled.Settings
//import androidx.compose.material.icons.filled.ShoppingCart
//import androidx.compose.material3.FloatingActionButton
//import androidx.compose.material3.FloatingActionButtonDefaults
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.paint
//import androidx.compose.ui.draw.rotate
//import androidx.compose.ui.draw.scale
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.asComposeRenderEffect
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.lerp
//import com.simranjeet.growise.R
//import com.simranjeet.growise.presentation.ui.theme.GroWiseTheme
//import kotlin.math.PI
//import kotlin.math.sin
//
//@RequiresApi(Build.VERSION_CODES.S)
//fun getRenderEffect(): RenderEffect {
//    val blurEffect = RenderEffect.createBlurEffect(80f, 80f, Shader.TileMode.MIRROR)
//
//    val alphaMatrix = RenderEffect.createColorFilterEffect(
//        ColorMatrixColorFilter(
//            ColorMatrix(
//                floatArrayOf(
//                    1f, 0f, 0f, 0f, 0f,
//                    0f, 1f, 0f, 0f, 0f,
//                    0f, 0f, 1f, 0f, 0f,
//                    0f, 0f, 0f, 50f, -5000f
//                )
//            )
//        )
//    )
//
//    return RenderEffect.createChainEffect(alphaMatrix, blurEffect)
//}
//
//@Composable
//fun MainScreen() {
//    val isMenuExtended = remember { mutableStateOf(false) }
//    val selectedTab = remember { mutableStateOf("home") }
//
//    // Start animation automatically when screen opens
//    LaunchedEffect(Unit) {
//        isMenuExtended.value = true
//    }
//
//    val fabAnimationProgress by animateFloatAsState(
//        targetValue = if (isMenuExtended.value) 1f else 0f,
//        animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
//    )
//
//    val clickAnimationProgress by animateFloatAsState(
//        targetValue = if (isMenuExtended.value) 1f else 0f,
//        animationSpec = tween(durationMillis = 400, easing = LinearEasing)
//    )
//
//    val renderEffect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//        getRenderEffect().asComposeRenderEffect()
//    } else null
//
//    MainScreen(
//        renderEffect = renderEffect,
//        fabAnimationProgress = fabAnimationProgress,
//        clickAnimationProgress = clickAnimationProgress,
//        onTabSelected = { tab ->
//            selectedTab.value = tab
//        },
//        toggleAnimation = {
//            isMenuExtended.value = !isMenuExtended.value
//        }
//    )
//}
//
//@Composable
//fun MainScreen(
//    renderEffect: androidx.compose.ui.graphics.RenderEffect?,
//    fabAnimationProgress: Float = 0f,
//    clickAnimationProgress: Float = 0f,
//    onTabSelected: (String) -> Unit = {},
//    toggleAnimation: () -> Unit = {}
//) {
//    Box(
//        Modifier
//            .fillMaxSize()
//            .padding(bottom = 24.dp),
//        contentAlignment = Alignment.BottomCenter
//    ) {
//        FabGroup(renderEffect = renderEffect, animationProgress = fabAnimationProgress)
//        FabGroup(
//            renderEffect = null,
//            animationProgress = fabAnimationProgress,
//            toggleAnimation = toggleAnimation,
//            onTabSelected = onTabSelected
//        )
//        Circle(
//            color = Color.White,
//            animationProgress = clickAnimationProgress
//        )
//    }
//}
//
//@Composable
//fun Circle(color: Color, animationProgress: Float) {
//    val animationValue = sin(PI * animationProgress).toFloat()
//    Box(
//        modifier = Modifier
//            .padding(10.dp)
//            .size(56.dp)
//            .scale(2 - animationValue)
//            .border(
//                width = 2.dp,
//                color = color.copy(alpha = color.alpha * animationValue),
//                shape = CircleShape
//            )
//    )
//}
//
//
//@Composable
//fun FabGroup(
//    animationProgress: Float = 0f,
//    renderEffect: androidx.compose.ui.graphics.RenderEffect? = null,
//    toggleAnimation: () -> Unit = {},
//    onTabSelected: (String) -> Unit = {}
//) {
//    Box(
//        Modifier
//            .fillMaxSize()
//            .graphicsLayer { this.renderEffect = renderEffect }
//            .padding(bottom = 10.dp),
//        contentAlignment = Alignment.BottomCenter
//    ) {
//        AnimatedFab(
//            icon = Icons.Default.AccountBox,
//            modifier = Modifier.padding(
//                bottom = lerp(0.dp, 72.dp, FastOutSlowInEasing.transform(animationProgress)),
//                end = lerp(0.dp, 210.dp, FastOutSlowInEasing.transform(animationProgress))
//            ),
//            opacity = LinearEasing.transform(0.2f, 0.7f, animationProgress),
//            onClick = { onTabSelected("account") }
//        )
//
//        AnimatedFab(
//            icon = Icons.Default.Settings,
//            modifier = Modifier.padding(
//                bottom = lerp(0.dp, 88.dp, FastOutSlowInEasing.transform(animationProgress))
//            ),
//            opacity = LinearEasing.transform(0.3f, 0.8f, animationProgress),
//            onClick = { onTabSelected("settings") }
//        )
//
//        AnimatedFab(
//            icon = Icons.Default.ShoppingCart,
//            modifier = Modifier.padding(
//                bottom = lerp(0.dp, 72.dp, FastOutSlowInEasing.transform(animationProgress)),
//                start = lerp(0.dp, 210.dp, FastOutSlowInEasing.transform(animationProgress))
//            ),
//            opacity = LinearEasing.transform(0.4f, 0.9f, animationProgress),
//            onClick = { onTabSelected("shop") }
//        )
//
//        AnimatedFab(
//            modifier = Modifier.scale(1f - LinearEasing.transform(0.5f, 0.85f, animationProgress)),
//            icon = Icons.Default.Add,
//            onClick = {},
//            backgroundColor = Color.Transparent
//        )
//
//        AnimatedFab(
//            icon = Icons.Default.Add,
//            modifier = Modifier
//                .rotate(225 * FastOutSlowInEasing.transform(animationProgress)),
//            onClick = toggleAnimation,
//            backgroundColor = Color.Transparent
//        )
//    }
//}
//
//private fun androidx.compose.animation.core.Easing.transform(start: Float, end: Float, fraction: Float): Float {
//    return androidx.compose.ui.util.lerp(start, end, this.transform(fraction))
//}
//
//@Composable
//fun AnimatedFab(
//    modifier: Modifier,
//    icon: ImageVector? = null,
//    opacity: Float = 1f,
//    backgroundColor: Color = Color.White,
//    onClick: () -> Unit = {}
//) {
//    FloatingActionButton(
//        onClick = onClick,
//        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
//        modifier = modifier.scale(1.25f),
//        containerColor = Color.White
//    ) {
//        icon?.let {
//            Icon(
//                imageVector = it,
//                contentDescription = null,
//                tint = Color.White.copy(alpha = opacity)
//            )
//        }
//    }
//}
//
//@Composable
//@Preview(device = "id:pixel_4a", showBackground = true, backgroundColor = 0xFF3A2F6E)
//private fun MainScreenPreview() { GroWiseTheme { MainScreen() } }