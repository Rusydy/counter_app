package com.example.counter_app

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalDensity

enum class ScrollDirection {
    UP,
    DOWN,
    STAY
}

const val homePage = "https://developer.android.com/jetpack/compose/state?hl=en"

@Composable
fun WebViewLayout() {
    var popUpVisible by remember { mutableStateOf(false) }
    var hitCount by remember { mutableStateOf(0) }
    var scrollDirection by remember { mutableStateOf(ScrollDirection.STAY) }
    var isHomeVisible by remember { mutableStateOf(true) }
    var lastTapTimestamp by remember { mutableStateOf(0L) }
    var isTimerRunning by remember { mutableStateOf(false) }    
    var remainingTime by remember { mutableStateOf(5) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.Gray)
                .pointerInput(Unit) {
                    // Scroll Detection
                    detectDragGestures(
                        onDragEnd = {
                            isTimerRunning = true
                            remainingTime = 5
                        },
                        onDrag = { _, dragAmount ->
                            scrollDirection = when {
                                dragAmount.y > 0f -> ScrollDirection.UP
                                dragAmount.y < 0f -> ScrollDirection.DOWN
                                else -> ScrollDirection.STAY
                            }
                        }
                    )

                    // Tap Detection
                    detectTapGestures { offset ->
                        val cornerSize = 48.dp.toPx()
                        val tapX = offset.x
                        val tapY = offset.y

                        if (tapX <= cornerSize && tapY <= cornerSize) {
                            val currentTime = System.currentTimeMillis()
                            if (currentTime - lastTapTimestamp < 500) {
                                hitCount++
                                if (hitCount >= 5) {
                                    popUpVisible = true
                                    hitCount = 0 // Reset the hit count
                                }
                            } else {
                                hitCount = 1
                            }
                            lastTapTimestamp = currentTime
                        } else {
                            hitCount = 0
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Show a webView
                WebViewComponent(
                    url = homePage
                ) {
                    isHomeVisible = it
                }
            }

            HomeFab(
                modifier = Modifier,
                isVisibleBecauseOfScrolling = isHomeVisible
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth().background(Color.LightGray),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // Hidden button that triggers the pop-up
            Button(
                onClick = {
                    popUpVisible = true
                },
                modifier = Modifier
                    .size(0.dp)
                    .background(Color.Transparent)
            ) {
                Text(text = "Hidden Button")
            }
        }
    }

    // Show the pop-up when popUpVisible is true
    if (popUpVisible) {
        AlertDialog(
            onDismissRequest = {
                popUpVisible = false
            },
            title = {
                Text(text = "Pop-up Content", fontWeight = FontWeight.Bold)
            },
            text = {
                Text(text = "This is your pop-up content.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        popUpVisible = false
                    }
                ) {
                    Text(text = "Close")
                }
            }
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun WebViewComponent(
    url: String,
    isHomeVisible: (Boolean) -> Unit
) {
    var webView: WebView? = null
    val scrollState = rememberScrollState()

    LaunchedEffect(scrollState.value) {
        val isHomeFabVisible = scrollState.value == 0
        isHomeVisible.invoke(isHomeFabVisible)
    }

    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
        ,
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                loadUrl(url)
                settings.javaScriptEnabled = true
                webView = this
            }
        }
    )
}

@Composable
private fun HomeFab(
    modifier: Modifier,
    isVisibleBecauseOfScrolling: Boolean,
) {
    val density = LocalDensity.current
    AnimatedVisibility(
        modifier = modifier,
        visible = isVisibleBecauseOfScrolling,
        enter = slideInVertically {
            with(density) { 40.dp.roundToPx() }
        } + fadeIn(),
        exit = fadeOut(
            animationSpec = keyframes {
                this.durationMillis = 120
            }
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp)
                    .align(Alignment.BottomStart)
                    .border(1.dp, Color.White, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                )
            }
        }
    }
}

@Preview
@Composable
fun Test() {
    WebViewLayout()
}
