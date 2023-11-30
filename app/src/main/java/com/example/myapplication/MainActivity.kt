package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.presentation.log_in.LogInActivity
import com.example.myapplication.presentation.sign_up.SignUpActivity

class MainActivity : ComponentActivity() {
    private var currentContext: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            currentContext = LocalContext.current

            Screen()
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
                .fillMaxHeight(0.55f)
                .clip(shape = RoundedCornerShape(0.dp, 0.dp, 100.dp, 100.dp))
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
        }
    }

    @Composable
    fun SelectionSection()
    {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ){
            Text(
                text = "Fashion at your fingertips, one snap at a time!",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color(getColor(R.color.primary_orange)),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(0.dp, 40.dp)
            )

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(getColor(R.color.primary_orange))
                ),
                shape = RoundedCornerShape(20),
                onClick = {
                    startLogInActivity()
                },
                modifier = Modifier
                    .width(280.dp)
                    .shadow(elevation = 20.dp, spotColor = Color.Black, ambientColor = Color.Transparent)
            ) {
                Text(
                    text = "Log In",
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.fillMaxHeight(0.1f))

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(20),
                onClick = {
                    startSignUpActivity()
                },
                modifier = Modifier
                    .width(280.dp)
                    .shadow(elevation = 20.dp, spotColor = Color.Black, ambientColor = Color.Transparent)
            ) {
                Text(
                    text = "Sign Up",
                    fontSize = 22.sp,
                    color = Color(getColor(R.color.primary_orange))
                )
            }
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

            SelectionSection()
        }
    }

    private fun startLogInActivity(){
        currentContext?.startActivity(
            Intent(currentContext, LogInActivity::class.java).addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT)
        )
    }

    private fun startSignUpActivity(){
        currentContext?.startActivity(
            Intent(currentContext, SignUpActivity::class.java).addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT)
        )
    }

}