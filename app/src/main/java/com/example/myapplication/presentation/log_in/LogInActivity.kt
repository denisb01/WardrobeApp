package com.example.myapplication.presentation.log_in

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

class LogInActivity: ComponentActivity() {
    private val email =  mutableStateOf("")
    private val password = mutableStateOf("")
    private var currentContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            currentContext = LocalContext.current

            Screen()
        }
    }

    @Composable
    fun Screen()
    {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
        ) {
            FieldsSection()

            FooterSection()
        }
    }

    @Composable
    fun FooterSection()
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                Toast.makeText(currentContext, "Google", Toast.LENGTH_LONG).show()
            }) {
               Row(
                   horizontalArrangement = Arrangement.spacedBy(10.dp)
               ){
                   Icon(Icons.Filled.Email, "Icon")
                   Text(
                       text = "Google Authentication"
                   )
               }
            }

            SignUpText()
        }
    }

    @Composable
    fun SignUpText()
    {
        val textSize: TextUnit = 18.sp

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 20.dp)
        ) {
            Text(
                text = "Don't have an account? ",
                fontSize = textSize
            )
            Text(
                text = "Sign Up",
                fontSize = textSize,
                color = Color.Blue,
                modifier = Modifier.clickable {
                    Toast.makeText(currentContext, "Sign Up", Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    @Composable
    fun FieldsSection()
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.fillMaxHeight(0.25f))
            Text(
                text = getString(R.string.app_name),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.fillMaxHeight(0.075f))
            CredentialFields(
                fieldIcon = Icons.Rounded.AccountCircle,
                state = email,
                placeholderText = "Enter Email",
                VisualTransformation.None
            )
            Spacer(modifier = Modifier.fillMaxHeight(0.025f))
            CredentialFields(
                fieldIcon = Icons.Rounded.Lock,
                state = password,
                placeholderText = "Enter Password",
                PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.fillMaxHeight(0.04f))
            Button(
                modifier = Modifier
                    .width(220.dp)
                    .height(40.dp),
                onClick = {
                    Toast.makeText(currentContext, "Sign In", Toast.LENGTH_LONG).show()
                }
            ) {
                Text(
                    text = "Log In",
                    fontSize = 16.sp
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CredentialFields(fieldIcon: ImageVector, state: MutableState<String>, placeholderText: String, hideText:VisualTransformation)
    {
        TextField(
            leadingIcon = {
                Icon(fieldIcon, "Icon")
            },
            visualTransformation = hideText,
            placeholder = { Text(text = placeholderText) },
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.LightGray),
            value = state.value,
            onValueChange = {
                state.value = it
            },
            modifier = Modifier
                .width(280.dp)
                .clip(shape = RoundedCornerShape(25.dp))
        )
    }
}