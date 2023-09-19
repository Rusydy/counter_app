package com.example.counter_app

import android.util.Log
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
import androidx.compose.ui.unit.sp

enum class ScrollDirection {
    UP,DOWN
}

@Composable
fun WebViewLayout() {
    var popUpVisible by remember { mutableStateOf(false) }
    var hitCount by remember { mutableStateOf(0) }

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
                    detectDragGestures { change, dragAmount ->
                        val swipeDirection = when {
                            dragAmount.y > 0f -> ScrollDirection.UP
                            dragAmount.y < 0f -> ScrollDirection.DOWN
                            else -> null
                        }

                        // Perform the appropriate action based on the swipe direction
                        if (swipeDirection != null) {
                            Log.d("Ajib", "Direction ${swipeDirection.name}")
                        }
                    }
                }
            ,
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(50.dp,50.dp)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            hitCount++
                            if (hitCount >= 5) {
                                popUpVisible = true
                            }
                        }
                    }
            )

            Text(
                text = if (hitCount < 5) "Hit $hitCount times to show pop-up" else "WEBVIEW",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = {
                        hitCount = 0
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .padding(8.dp)
                        .align(Alignment.BottomStart)
                        .border(1.dp, Color.White, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
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

@Preview
@Composable
fun Test(){
    WebViewLayout()
}