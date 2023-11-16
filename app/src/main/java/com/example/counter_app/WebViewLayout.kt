package com.example.counter_app

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
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
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width

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

    val context = LocalContext.current
    val sharedPreferences = getSharedPreferences(context)
    var tokenText by remember { mutableStateOf(getTokenFromSharedPreferences(sharedPreferences)) }
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

    var passwordDialogVisible by remember { mutableStateOf(true) }

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
                    url = "$baseUrl/$tokenText",
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
                                    passwordDialogVisible = true
                                }
                            }
                        }
                )
            }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(58.dp, 0.dp, 0.dp, 58.dp),
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
                            modifier = Modifier.size(48.dp),
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                        )
                    }
                }
        }
    }

    if (hitCount >= 5 || tokenText.isEmpty()) {
        PasswordInputDialog(
            onPasswordCorrect = {
                passwordDialogVisible = false
                popUpVisible = true
            },
            onPasswordIncorrect = {
                popUpVisible = false
                passwordDialogVisible = true
            }
        )
    }

    if (popUpVisible && !passwordDialogVisible){
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
                            saveTokenToSharedPreferences(sharedPreferences, newValue)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .imePadding(),
                        label = { Text("Enter Token") }
                    )
                }
            },

            confirmButton = {
                Row() {
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

                    Spacer(modifier = Modifier.width(16.dp))

                    SettingsButton()
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordInputDialog(
    onPasswordCorrect: () -> Unit,
    onPasswordIncorrect: () -> Unit,
) {
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onCloseRequest.
        },
        title = {
            Text(text = "Password", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(text = "Enter your password.")
                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = password,
                    onValueChange = { newValue ->
                        password = newValue
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding(),
                    label = { Text("Enter Password") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (password == "123") {
                        onPasswordCorrect()
                    } else {
                        onPasswordIncorrect()
                    }
                }
            ) {
                Text(text = "SUBMIT")
            }
        }
    
    )
}

@Composable
fun SettingsButton() {
    val context = LocalContext.current
    val intent = Intent(Settings.ACTION_SETTINGS)

    Button(
        onClick = {
            context.startActivity(intent)
        }
    ) {
        Text(text = "SETTINGS")
    }
}

private fun getSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
}

private fun saveTokenToSharedPreferences(sharedPreferences: SharedPreferences, token: String) {
    val editor = sharedPreferences.edit()
    editor.putString("token", token)
    editor.apply()
}

private fun getTokenFromSharedPreferences(sharedPreferences: SharedPreferences): String {
    return sharedPreferences.getString("token", "") ?: ""
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
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
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
                loadUrl(url).also {
                    Log.d("WebView", "Loading URL: $url")
                }
                setInitialScale(110).also {
                    Log.d("setInitialScale", "hehe")
                }

                // Set user agent as desktop
                settings.userAgentString =
                    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                            "Chrome/90.0.4430.212 Safari/537.36"

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

    Log.d("screenResolution", screenWidth.toString())
    Log.d("screenResolution", screenHeight.toString())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .size(screenWidth, screenHeight)
    ) {
        WebViewLayout()
    }
}

@Preview
@Composable
fun Test() {
    OptimizedAppLayout()
}