package com.example.myapplication.presentation.log_in

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

class LogInActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Screen()
    {
        val email = remember { mutableStateOf("") }
        val password = remember{ mutableStateOf("") }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "WardrobeApp",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.padding(0.dp,160.dp,0.dp,40.dp)
            )

            TextField(
                value = email.value,
                onValueChange = {
                    email.value = it
                },
                modifier = Modifier.padding(0.dp,0.dp,0.dp,20.dp)
            )

            TextField(
                value = password.value,
                onValueChange = {
                    password.value = it
                },
                modifier = Modifier.padding(0.dp,0.dp,0.dp,20.dp)
            )

            Button(onClick = { /*TODO*/ }) {
                Text(text = "Log In")
            }
        }
    }
}