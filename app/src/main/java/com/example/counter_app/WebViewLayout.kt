package com.example.counter_app

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import android.os.CountDownTimer
import androidx.compose.runtime.DisposableEffect

// TODO: ENHANCEMENT! change this URL to display URLs
const val baseUrl = "https://detik.com"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewLayout() {
    val systemUiController = rememberSystemUiController()
    systemUiController.isSystemBarsVisible = false
    systemUiController.navigationBarDarkContentEnabled = true

    var popUpVisible by remember { mutableStateOf(false) }
    var hitCount by remember { mutableStateOf(0) }

    var tokenText by remember { mutableStateOf("") }
    var homePageUrl by remember { mutableStateOf(baseUrl) }

    val webViewState = remember { mutableStateOf<WebView?>(null) }

    var countDownTimer: CountDownTimer? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        val timer = object : CountDownTimer(15 * 60 * 1000, 1000) { // 15 minutes in milliseconds
            override fun onTick(millisUntilFinished: Long) {
                // Countdown in progress
            }

            override fun onFinish() {
                // Timer has finished, navigate back to homePageUrl
                hitCount = 0
                popUpVisible = false
                homePageUrl = "$baseUrl/$tokenText"
                Log.d("Timer", "Timer has finished, returning to homePageUrl")
                webViewState.value?.loadUrl(homePageUrl)
            }
        }

        timer.start()
        countDownTimer = timer

        onDispose {
            countDownTimer?.cancel()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                WebViewComponent(
                    url = homePageUrl,
                    onWebViewReady = { webViewInstance ->
                        webViewInstance
                    },
                    webViewState = webViewState
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .size(48.dp, 48.dp)
                        .border(8.dp, Color.Transparent)
                        .pointerInput(Unit) {
                            detectTapGestures {
                                hitCount++
                                if (hitCount >= 5) {
                                    popUpVisible = true
                                }
                            }
                        }
                )
            }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp, 0.dp, 0.dp, 50.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    IconButton(
                        onClick = {
                            hitCount = 0
                            popUpVisible = false
                            homePageUrl = "$baseUrl/$tokenText"
                            webViewState.value?.loadUrl(homePageUrl)
                        },
                        modifier = Modifier
                            .size(48.dp)
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

    if (homePageUrl == baseUrl || popUpVisible) {
        AlertDialog(
            onDismissRequest = {
                popUpVisible = true
            },
            title = {
                Text(text = "Pop-up Content", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(text = "This is your pop-up content.")
                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = tokenText,
                        onValueChange = { newValue ->
                            tokenText = newValue
                        },
                        modifier = Modifier.fillMaxWidth().imePadding(),
                        label = { Text("Enter Token") }
                    )
                }
            },

            confirmButton = {
                Button(
                    onClick = {
                        hitCount = 0
                        popUpVisible = false
                        homePageUrl = "$baseUrl/$tokenText"
                        webViewState.value?.loadUrl(homePageUrl)
                    }
                ) {
                    Text(text = "SUBMIT")
                }
            }
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun WebViewComponent(
    url: String,
    onWebViewReady: (WebView) -> Unit,
    webViewState: MutableState<WebView?>
) {
    rememberScrollState(0)
    AndroidView(
        modifier = Modifier.fillMaxSize().background(Color.Transparent),
        factory = { context ->
            WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        // Handle error
                        Log.d("WebViewError", "Error: $error")
                    }

                    override fun onReceivedHttpError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        errorResponse: WebResourceResponse?
                    ) {
                        // Handle HTTP error
                        Log.d("WebViewError", "HTTP Error: $errorResponse")
                    }
                }
                loadUrl(url)
                setInitialScale(60)
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.allowFileAccess = true
                settings.allowContentAccess = true
                settings.allowFileAccessFromFileURLs = true
                settings.allowUniversalAccessFromFileURLs = true
                settings.mediaPlaybackRequiresUserGesture = false
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                onWebViewReady(this) // Pass the WebView instance
                webViewState.value = this // Update the WebView state
            }
        },
        update = { view ->
            view.loadUrl(url)
        }
    )
}

@Composable
fun OptimizedAppLayout() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    Box(
        modifier = Modifier.fillMaxSize().size(screenWidth, screenHeight)
    ) {
        WebViewLayout()
    }
}

@Preview
@Composable
fun Test() {
    OptimizedAppLayout()
}