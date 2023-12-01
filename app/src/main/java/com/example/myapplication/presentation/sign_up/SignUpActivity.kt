package com.example.myapplication.presentation.sign_up

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.database.FirebaseController
import com.example.myapplication.presentation.log_in.LogInActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest


class SignUpActivity: ComponentActivity() {
    private var currentContext: Context? = null
    private val firstNameState = mutableStateOf("")
    private val lastNameState = mutableStateOf("")
    private val emailState = mutableStateOf("")
    private val passwordState = mutableStateOf("")

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            currentContext = LocalContext.current
            auth = Firebase.auth


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
            LogoSection()
            FieldsSection()
            Spacer(modifier = Modifier.fillMaxHeight(0.04f))
            FooterSection()
        }
    }

    @Composable
    fun LogInText()
    {
        val textSize: TextUnit = 18.sp

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 20.dp)
        ) {
            Text(
                text = "Already a member? ",
                fontSize = textSize
            )
            Text(
                text = "Log In",
                fontSize = textSize,
                color = Color.Blue,
                modifier = Modifier.clickable {
                    currentContext?.startActivity(
                        Intent(currentContext, LogInActivity::class.java).addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT)
                    )
                }
            )
        }
    }

    @Composable
    fun FooterSection()
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(getColor(R.color.primary_orange))
                ),
                onClick = {
                    signUpUser()
                },
                modifier = Modifier
                    .width(260.dp)
            ) {
                Text(
                    text = "Sign Up",
                    fontSize = 18.sp
                )
            }
            LogInText()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CredentialField(fieldIcon: ImageVector, state: MutableState<String>, placeholderText: String, hideText: Boolean = false, keyboardType: KeyboardType)
    {
        val visualTransformation = if (hideText) PasswordVisualTransformation() else VisualTransformation.None
        TextField(
            leadingIcon = {
                Icon(fieldIcon, "Icon")
            },
            textStyle = TextStyle.Default.copy(fontSize = 16.sp),
            visualTransformation = visualTransformation,
            placeholder = { Text(text = placeholderText) },
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.LightGray),
            value = state.value,
            onValueChange = {
                state.value = it
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier
                .padding(0.dp, 20.dp, 0.dp, 0.dp)
                .width(310.dp)
                .height(50.dp)
                .shadow(
                    elevation = 10.dp,
                    spotColor = Color.Black,
                    shape = RoundedCornerShape(16.dp)
                )
                .clip(shape = RoundedCornerShape(25.dp))
        )
    }

    @Composable
    fun FieldsSection()
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            CredentialField(
                fieldIcon = Icons.Rounded.Person,
                state = firstNameState,
                placeholderText = "First Name",
                keyboardType = KeyboardType.Text
            )

            CredentialField(
                fieldIcon = Icons.Rounded.Person,
                state = lastNameState,
                placeholderText = "Last Number",
                keyboardType = KeyboardType.Text
            )

            CredentialField(
                fieldIcon = Icons.Rounded.Email,
                state = emailState,
                placeholderText = "Email",
                keyboardType = KeyboardType.Email
            )

            CredentialField(
                fieldIcon = Icons.Rounded.Lock,
                state = passwordState,
                placeholderText = "Password",
                hideText = true,
                keyboardType = KeyboardType.Password
            )

        }
    }

    @Composable
    fun LogoSection()
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(0.4f)
                .clip(shape = RoundedCornerShape(0.dp, 0.dp, 100.dp, 0.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(getColor(R.color.secondary_orange)),
                            Color(getColor(R.color.primary_orange))
                        )
                    )
                )
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxHeight(0.825f)
            ){
                Image(
                    painter = painterResource(id = R.mipmap.logo_image_white_transparent_foreground),
                    contentDescription = "Image",
                    modifier = Modifier.size(160.dp)
                )
                Text(
                    text = getString(R.string.app_name),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                    ,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            Text(
                text = "Sign Up",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Left,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(0.dp, 10.dp)
            )
        }
    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
    private fun checkCredentials() : Boolean
    {
        if (!isValidEmail(emailState.value)){
            Toast.makeText(currentContext,"Invalid Email Address!",Toast.LENGTH_SHORT).show()
            return false
        }

        if (firstNameState.value.isEmpty()){
            Toast.makeText(currentContext,"First Name can't be blank!",Toast.LENGTH_SHORT).show()
            return false
        }

        if (lastNameState.value.isEmpty()){
            Toast.makeText(currentContext,"Last Name can't be blank!",Toast.LENGTH_SHORT).show()
            return false
        }

        if (passwordState.value.isEmpty()){
            Toast.makeText(currentContext,"Password can't be blank!",Toast.LENGTH_SHORT).show()
            return false
        }

        if (passwordState.value.length < 6){
            Toast.makeText(currentContext,"Password must be at least 6 characters long!",Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun updateUserDisplayName(user: FirebaseUser?) {
        if (user != null) {
            val profileUpdates = userProfileChangeRequest {
                displayName = firstNameState.value + " " + lastNameState.value
            }

            user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        currentContext,
                        "User added successfully!",
                        Toast.LENGTH_SHORT,
                    ).show()
                } else {
                    Toast.makeText(
                        currentContext,
                        "Error adding user display name!",
                        Toast.LENGTH_SHORT,
                    ).show()

                    finish()
                }
            }
        }
    }

    private fun signUpUser()
    {
        if(checkCredentials()){
            auth.createUserWithEmailAndPassword(emailState.value, passwordState.value)
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful){
                        val db = FirebaseController(currentContext)

                        updateUserDisplayName(auth.currentUser)

                        db.addUserData(auth.currentUser, firstNameState.value + " " + lastNameState.value)

                        finish()
                    }
                    else{
                        Toast.makeText(
                            currentContext,
                            "Error adding user! Check credentials again!",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }
    }
}