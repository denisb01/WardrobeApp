package com.example.myapplication.presentation.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.database.FirebaseController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest

class AccountSettingsActivity: ComponentActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            if(Firebase.auth.currentUser != null) Screen()
            else {
                Toast.makeText(baseContext, "Error!", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ScaffoldTopBar()
    {
        val color = Color.White

        CenterAlignedTopAppBar (
            navigationIcon = {
                IconButton(onClick = { finish() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Icon",
                        tint = color
                    )
                }
            },
            title = {
                Text(
                    text = "Account Settings",
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                )
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = Color(getColor(R.color.primary_orange))
            )

        )
    }

    @Composable
    fun UserImage()
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .clip(RoundedCornerShape(0.dp, 0.dp, 100.dp, 100.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(getColor(R.color.primary_orange)),
                            Color(getColor(R.color.secondary_orange)),
                        )
                    )
                )
        ) {
            Icon(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = "Icon",
                tint = Color.White,
                modifier = Modifier
                    .width(250.dp)
                    .height(250.dp)
            )

            Text(
                text = Firebase.auth.currentUser!!.displayName.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = Firebase.auth.currentUser!!.email.toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SettingsField(fieldValue: MutableState<String>, hideText: Boolean)
    {
        val visualTransformation = if (hideText) PasswordVisualTransformation() else VisualTransformation.None

        val interaction = remember { MutableInteractionSource() }
        BasicTextField(
            visualTransformation = visualTransformation,
            value = fieldValue.value,
            onValueChange = {
                fieldValue.value = it
            },
            readOnly = false,
            enabled = true,
            interactionSource = interaction,
            decorationBox = {inner ->
                TextFieldDefaults.TextFieldDecorationBox(
                    value = fieldValue.value,
                    innerTextField = inner,
                    enabled = true,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interaction,
                    contentPadding = PaddingValues(10.dp, 0.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(getColor(R.color.secondary_orange)),
                    )
                )
            },
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(45.dp)
        )
    }

    @Composable
    fun TextSettingsField(text: String, fieldValue: MutableState<String>, hideText: Boolean = false)
    {
        Column{
            Text(
                text = text,
                fontSize = 20.sp,
                color = Color(getColor(R.color.primary_orange)),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(0.dp,6.dp)
            )
            SettingsField(fieldValue = fieldValue, hideText = hideText)
        }
    }

    @Composable
    fun SettingsButton(enableButton: MutableState<Boolean>, text: String, action: () -> Unit)
    {
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(getColor(R.color.primary_orange))
            ),
            enabled = enableButton.value,
            onClick = action,
            modifier = Modifier
                .width(250.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ){
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    private fun checkIfValidUserName(userNameValue: MutableState<String>): Boolean
    {
        return userNameValue.value.isNotEmpty() &&
                userNameValue.value != Firebase.auth.currentUser!!.displayName.toString()
    }

    private fun checkIfValidPassword(passwordValue: MutableState<String>, confirmPasswordValue: MutableState<String>): Boolean{
        val passwordChange = passwordValue.value.isNotEmpty() &&
                passwordValue.value == confirmPasswordValue.value

        return passwordValue.value.length >= 6 && passwordChange
    }

    private fun checkButtonEnable(userNameValue: MutableState<String>, passwordValue: MutableState<String>, confirmPasswordValue: MutableState<String>): Boolean
    {

        var usernameChange = checkIfValidUserName(userNameValue)

        var passwordChange = checkIfValidPassword(passwordValue, confirmPasswordValue)

        return usernameChange || passwordChange
    }

    private fun updateUserName(userName: String) {
        val db = FirebaseController(baseContext)

        val profileUpdates = userProfileChangeRequest {
            displayName = userName
        }

        Firebase.auth.currentUser!!.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    baseContext,
                    "User display name added successfully!",
                    Toast.LENGTH_SHORT,
                ).show()
            } else {
                Toast.makeText(
                    baseContext,
                    "Error saving user display name!",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }

        db.addUserData(Firebase.auth.currentUser, userName)
    }

    private fun updatePassword(password: String)
    {
        val user = Firebase.auth.currentUser

        user!!.updatePassword(password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        baseContext,
                        "Password saved successfully!",
                        Toast.LENGTH_SHORT,
                    ).show()
                } else {
                    Toast.makeText(
                        baseContext,
                        "Error saving new password!",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    @Composable
    fun FieldsSection()
    {
        val userNameValue = remember { mutableStateOf(Firebase.auth.currentUser!!.displayName.toString()) }
        val passwordValue = remember { mutableStateOf("") }
        val confirmPasswordValue = remember { mutableStateOf("") }

        val enableButton = remember { mutableStateOf(false) }

        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {
            TextSettingsField("New Username", fieldValue = userNameValue)

            TextSettingsField("New Password", fieldValue = passwordValue, hideText = true)
            TextSettingsField("Confirm New Password", fieldValue = confirmPasswordValue, hideText = true)

            enableButton.value = checkButtonEnable(userNameValue, passwordValue, confirmPasswordValue)

            SettingsButton(
                enableButton = enableButton,
                text = "Save",
                action = {
                    if (checkIfValidUserName(userNameValue)) {
                        if (userNameValue.value.contains(" "))
                            updateUserName(userNameValue.value)
                        else
                            Toast.makeText(
                                baseContext,
                                "Username should have First Name and Last Name!",
                                Toast.LENGTH_SHORT
                            ).show()

                        Toast.makeText(
                            baseContext,
                            "Restart App to see changed username!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    if (checkIfValidPassword(passwordValue, confirmPasswordValue))
                        updatePassword(passwordValue.value)

                    finish()
                }
            )
        }
    }

    @Composable
    fun ScaffoldBody(paddingValues: PaddingValues){
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.Transparent)
        ){
            UserImage()
            FieldsSection()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Screen()
    {
        Scaffold(
            topBar = { ScaffoldTopBar() },
            containerColor = Color.Transparent,
            modifier = Modifier
                .fillMaxSize()
        ) { paddingValues ->
            ScaffoldBody(paddingValues = paddingValues)
        }
    }

}