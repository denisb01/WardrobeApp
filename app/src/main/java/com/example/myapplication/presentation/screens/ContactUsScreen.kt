package com.example.myapplication.presentation.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.R

@Composable
fun ContactUsScreen(navController: NavController, context: Context)
{
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ){
        ContactUsTitle(context = context)
        ContactUsImageSection(context = context)
        ContactUsCardsSection(context = context)
    }
}

@Composable
fun ContactUsTitle(context: Context)
{
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.15f)
            .background(Color(context.getColor(R.color.primary_orange)))
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .height(50.dp)
                .width(200.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
        ) {
            Text(
                text = "Contact Us",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(context.getColor(R.color.primary_orange))
            )
        }
    }
}

@Composable
fun ContactUsImageSection(context: Context) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.45f)
            .clip(RoundedCornerShape(0.dp, 0.dp, 40.dp, 40.dp))
            .background(Color(context.getColor(R.color.primary_orange)))
    ) {
        Icon(
            imageVector = Icons.Filled.ContactPhone,
            contentDescription = "Icon",
            tint = Color.White,
            modifier = Modifier
                .width(160.dp)
                .height(160.dp)
        )
        Text(
            text = "Reach out and let's tailor a solution together!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier
                .width(240.dp)
                .padding(0.dp, 10.dp)
        )
    }
}

@Composable
fun ContactCard(context: Context, imageVector: ImageVector, text: String, action: () -> Unit)
{
    val contentColor = Color.White
    val containerColor = Color(context.getColor(R.color.primary_orange))

    Card(
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 15.dp
        ),
        modifier = Modifier
            .width(300.dp)
            .requiredWidth(300.dp)
            .height(70.dp)
            .requiredHeight(70.dp)
            .clickable(onClick = action)
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ){
            Icon(
                imageVector = imageVector,
                contentDescription = "Icon",
                tint = contentColor,
                modifier = Modifier
                    .padding(20.dp, 0.dp)
            )
            Text(
                text = text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Icon(
                imageVector = imageVector,
                contentDescription = "Icon",
                tint = contentColor,
                modifier = Modifier
                    .scale(-1f, 1f)
                    .padding(20.dp, 0.dp)
            )
        }
    }
}

@Composable
fun ContactUsCardsSection(context: Context) {
    val phoneNumber = "+40 737 111222"
    val email = "snapfit@gmail.com"
    val webPage = "https://www.upt.ro"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxSize()
    ){
        ContactCard(
            context = context,
            imageVector = Icons.Filled.Phone,
            text = phoneNumber,
            action = {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$phoneNumber")
                }

                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                }
                else{
                    Toast.makeText(context, "Error!", Toast.LENGTH_LONG).show()
                }
            }
        )

        ContactCard(
            context = context,
            imageVector = Icons.Filled.Mail,
            text = email,
            action = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:$email")
                }
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                }
                else{
                    Toast.makeText(context, "Error!", Toast.LENGTH_LONG).show()
                }
            }
        )

        ContactCard(
            context = context,
            imageVector = Icons.Filled.OpenInBrowser,
            text = webPage,
            action = {
                val webpage: Uri = Uri.parse(webPage)
                val intent = Intent(Intent.ACTION_VIEW, webpage)
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                }
                else{
                    Toast.makeText(context, "Error!", Toast.LENGTH_LONG).show()
                }
            }
        )
    }
}