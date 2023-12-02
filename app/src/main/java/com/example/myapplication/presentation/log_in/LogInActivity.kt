package com.example.myapplication.presentation.log_in

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
import android.content.IntentSender
import android.os.Bundle
import android.widget.Toast
import androidx.compose.runtime.Composable
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.myapplication.presentation.sign_up.SignUpActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth

class LogInActivity: ComponentActivity() {
    private val email =  mutableStateOf("")
    private val password = mutableStateOf("")
    private var currentContext: Context? = null

    private lateinit var auth: FirebaseAuth

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    private val REQ_ONE_TAP = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            currentContext = LocalContext.current
            auth = Firebase.auth

            oneTapClient = Identity.getSignInClient(this)

            signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build()

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

            FooterSection()
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
                    googleAuthentication()
                }
            ) {
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
                    currentContext?.startActivity(
                        Intent(currentContext,SignUpActivity::class.java).addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT)
                    )
                }
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
                .clip(shape = RoundedCornerShape(0.dp, 0.dp, 0.dp, 100.dp))
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
                text = "Log In",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Right,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(0.dp, 10.dp)
            )
        }
    }

    @Composable
    fun FieldsSection()
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {

            CredentialField(
                fieldIcon = Icons.Rounded.AccountCircle,
                state = email,
                placeholderText = "Enter Email",
                hideText = false,
                keyboardType = KeyboardType.Email
            )
            Spacer(modifier = Modifier.fillMaxHeight(0.025f))
            CredentialField(
                fieldIcon = Icons.Rounded.Lock,
                state = password,
                placeholderText = "Enter Password",
                hideText = true,
                keyboardType = KeyboardType.Password
            )
            Spacer(modifier = Modifier.fillMaxHeight(0.1f))
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(getColor(R.color.primary_orange))
                ),
                modifier = Modifier
                    .width(220.dp)
                    .height(40.dp),
                onClick = {
                    logUserIn()
                },
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
    fun CredentialField(fieldIcon: ImageVector, state: MutableState<String>, placeholderText: String, hideText: Boolean, keyboardType: KeyboardType)
    {
        val visualTransformation = if (hideText) PasswordVisualTransformation() else VisualTransformation.None
        TextField(
            leadingIcon = {
                Icon(fieldIcon, "Icon")
            },
            textStyle = TextStyle.Default.copy(fontSize = 18.sp),
            visualTransformation = visualTransformation,
            placeholder = { Text(text = placeholderText) },
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.LightGray),
            value = state.value,
            onValueChange = {
                state.value = it
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier
                .width(280.dp)
                .clip(shape = RoundedCornerShape(25.dp))
        )
    }

    private fun getUser(idToken: String)
    {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val db = FirebaseController(currentContext)
                    db.addUserData(auth.currentUser)
                    displayToast("Logged In successfully!")
                    sendResult()
                } else {
                    displayToast("Failed to Sign In. Try again later!")
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val googleCredential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = googleCredential.googleIdToken

                    if(idToken != null) {
                        getUser(idToken)
                    }
                    else {
                        displayToast("Selected Google Token not received!")
                    }
                } catch (e: ApiException) {
                    displayToast("Error, try again later!")
                }
            }
        }
    }

    private fun googleAuthentication()
    {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0, null)
                } catch (e: IntentSender.SendIntentException) {
                    displayToast("Couldn't start Google Authentication!")
                }
            }
            .addOnFailureListener(this) { _ ->
                displayToast("No accounts found!")
            }
    }

    private fun logUserIn()
    {
        auth.signInWithEmailAndPassword(email.value, password.value)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    displayToast("Logged In successfully!")
                    sendResult()
                }
                else{
                    displayToast("Log In failed! Invalid email or password!")
                }
            }
    }

    private fun sendResult() {

        val returnIntent = Intent()

        setResult(Activity.RESULT_OK, returnIntent)

        finish()
    }

    private fun displayToast(s: String?) {
        Toast.makeText(applicationContext, s, Toast.LENGTH_SHORT).show()
    }
}