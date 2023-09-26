package com.example.counter_app

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

const val homePageUrl = "https://developer.android.com/jetpack/compose/state?hl=en"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewLayout() {
    var popUpVisible by remember { mutableStateOf(false) }
    var hitCount by remember { mutableStateOf(0) }

    var tokenText by remember { mutableStateOf("") } // Create a mutableState for the token text

    // TODO: Implement this on CA-2
    var isHomeVisible by remember { mutableStateOf(true) }

    // Reference to the WebView component
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
                // TODO: adjust the .pointerInput to detect the scroll gesture and hide the home button on CA-2
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(48.dp, 48.dp)
                    .border(8.dp, Color.Red)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            hitCount++
                            if (hitCount >= 5) {
                                popUpVisible = true
                            }
                        }
                    }
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp, 8.dp, 8.dp, 0.dp)
            ) {
                // Pass a reference to the WebView
                WebViewComponent(homePageUrl) { webViewInstance ->
                    webView = webViewInstance
                }
            }

            if (isHomeVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    IconButton(
                        onClick = {
                            hitCount = 0
                            webView?.loadUrl(homePageUrl) // Load the home page URL
                        },
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

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // TODO: Fix this button on CA-2
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
                Column {
                    Text(text = "This is your pop-up content.")
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // TextField for entering the token
                    TextField(
                        value = tokenText,
                        onValueChange = { newValue ->
                            tokenText = newValue
                        },
                        label = { Text("Enter Token") }
                    )
                }
            },

            confirmButton = {
                Button(
                    onClick = {
                        hitCount = 0
                        popUpVisible = false
                        val enteredToken = tokenText // Get the entered token here
                        // TODO: implement Dynamic WebView Content Based on enteredToken
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
            .verticalScroll(scrollState),
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                loadUrl(url)
                settings.javaScriptEnabled = true
                onWebViewReady(this) // Pass the WebView instance
            }
        }
    )
}

@Preview
@Composable
fun Test() {
    WebViewLayout()
}