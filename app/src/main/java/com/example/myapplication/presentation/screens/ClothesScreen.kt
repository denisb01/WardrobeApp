package com.example.myapplication.presentation.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.navigation.auth
import com.example.myapplication.navigation.firebaseController

@Composable
fun ClothesScreen(navController: NavController, context: Context)
{
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ){
        val imagesListFlow = firebaseController.getImages(auth.currentUser!!)
        val imagesList = imagesListFlow.collectAsState(emptyList()).value

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            content = { items(imagesList){ image ->
                    Card(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 15.dp
                        ),
                        modifier = Modifier
                            .width(150.dp)
                            .requiredWidth(150.dp)
                            .height(200.dp)
                            .requiredHeight(200.dp)
                            .padding(0.dp, 20.dp)
                            .clickable {
                                Toast
                                    .makeText(context, image.label + " : " + image.accuracy.toString(), Toast.LENGTH_SHORT)
                                    .show()
                            }
                    ){
                        Column(
                            horizontalAlignment =  Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            AsyncImage(
                                model = image.uri,
                                contentDescription = image.name,
                                modifier = Modifier
                                    .fillMaxHeight(0.8f)
                            )
                            Text(text = image.name)
                        }
                    }
                }
            }
        )
    }
}