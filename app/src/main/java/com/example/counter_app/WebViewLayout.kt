package com.example.counter_app

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.systemuicontroller.rememberSystemUiController

// TODO: ENHANCEMENT! change this URL to display URLs
const val baseUrl = "https://detik.com"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewLayout() {
    val systemUiController = rememberSystemUiController()
    systemUiController.isSystemBarsVisible = false
    systemUiController.navigationBarDarkContentEnabled = true

    var popUpVisible by remember { mutableStateOf(false) }
    var isHomeVisible by remember { mutableStateOf(true) }
    var hitCount by remember { mutableStateOf(0) }

    var tokenText by remember { mutableStateOf("") }
    var homePageUrl by remember { mutableStateOf(baseUrl) }

    var webView: WebView? = null

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

                WebViewComponent(homePageUrl) { webViewInstance ->
                    webView = webViewInstance
                }

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

            if (isHomeVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    IconButton(
                        onClick = {
                            Log.d("token on homePageUrl", homePageUrl)
//                          TODO: ISSUE! after token inputted, the home button is not working properly
                            hitCount = 0
                            popUpVisible = false
                            val enteredToken = tokenText
                            if (enteredToken.isNotEmpty()) {
                                homePageUrl = "$baseUrl/$enteredToken"
                                Log.d("token on HOME", homePageUrl)
                                webView?.loadUrl(homePageUrl)
                            }
                            webView?.loadUrl(baseUrl)

                        },
                        modifier = Modifier
                            .size(48.dp)
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

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
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

    if (popUpVisible) {
        AlertDialog(
            onDismissRequest = {
                popUpVisible = false
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
                        val enteredToken = tokenText 
                        if (enteredToken.isNotEmpty()) {
                            homePageUrl = "$baseUrl/$enteredToken"
                            Log.d("token on SUBMIT", homePageUrl)
                            webView?.loadUrl(homePageUrl)
                        }
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
    onWebViewReady: (WebView) -> Unit
) {
    val scrollState = rememberScrollState()

    AndroidView(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(Color.Transparent),
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                loadUrl(url)
                settings.javaScriptEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                onWebViewReady(this) // Pass the WebView instance
            }
        },
        update = { view ->
            view.loadUrl(url)
        }
    )
}

@Composable
fun ForceResizeContent(screenWidth: Dp, screenHeight: Dp, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(screenWidth, screenHeight)
    ) {
        content()
    }
}

@Composable
fun OptimizedAppLayout() {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        ForceResizeContent(screenWidth, screenHeight) {
            WebViewLayout()
        }
    }
}

@Preview
@Composable
fun Test() {
    OptimizedAppLayout()
}