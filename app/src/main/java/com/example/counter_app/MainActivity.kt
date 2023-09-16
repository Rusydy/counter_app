package com.example.counter_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalDensity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebViewLayout()
        }
    }
}

@Composable
fun WebViewLayout() {
    var popUpVisible by remember { mutableStateOf(false) }
    var hitCount by remember { mutableStateOf(0) }


    // Calculate the threshold value based on density
    val density = LocalDensity.current.density
    val topLeftCornerThreshold = (48.dp * density)// Adjust this threshold as needed


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.Gray)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        // Check if the click is in the top-left corner
                        val x = offset.x
                        val y = offset.y
                        val isClickInTopLeftCorner = (x <= topLeftCornerThreshold.value) && (y <= topLeftCornerThreshold.value)

                        if (isClickInTopLeftCorner) {
                            hitCount++
                            if (hitCount >= 5) {
                                popUpVisible = true
                            }
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (hitCount < 5) "Hit $hitCount times to show pop-up" else "WEBVIEW",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            ) {
                Button(
                    onClick = {
                        // Implement action for the HOME button here
                        hitCount = 0 // Reset hitCount to 0
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "HOME Button")
                }
            }

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
