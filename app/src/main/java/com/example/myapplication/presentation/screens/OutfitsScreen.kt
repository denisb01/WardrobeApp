package com.example.myapplication.presentation.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.data.FirebaseClothingItem
import com.example.myapplication.presentation.app.AppActivity
import com.example.myapplication.presentation.app.CreateOutfitsActivity

val outfitsList = mutableStateOf(listOf<FirebaseClothingItem>())
var fullOutfitsList = listOf<FirebaseClothingItem>()

val outfitsSearchCriteria = mutableStateOf("")

fun displayOutfitCards(list: List<FirebaseClothingItem>)
{
    outfitsList.value = list
}

fun displaySearchedOutfits()
{
    if (outfitsSearchCriteria.value.isEmpty()){
        displayImageCards(fullOutfitsList)
    }
    else{
        val searchValuesList = mutableListOf<FirebaseClothingItem>()

        for (image in fullOutfitsList){
            if(image.clothingItemData.name.toLowerCase().contains(outfitsSearchCriteria.value.toLowerCase())){
                searchValuesList.add(image)
            }
        }

        displayOutfitCards(searchValuesList)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutfitsScreen(navController: NavController, context: Context)
{
    Scaffold (
        containerColor = Color.Transparent,
        floatingActionButton = { AddOutfitsButton(context = context) }
    ){ paddingValues ->
        Window(paddingValues, context)
    }
}

@Composable
fun AddOutfitsButton(context: Context)
{
    FloatingActionButton(
        containerColor = Color(context.getColor(R.color.primary_orange)),
        onClick = {
            val intent = Intent(context, CreateOutfitsActivity::class.java)
            context.startActivity(intent)
        }
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = "Add",
            tint = Color.White
        )
    }
}

@Composable
fun Window(paddingValues: PaddingValues, context: Context)
{
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(Color.Transparent)
    ){
        SearchBar(context, outfitsSearchCriteria, AppActivity.OUTFITS_SPEECH_REQUEST_CODE, ::displaySearchedOutfits)

        OutfitsCardsDisplay(context = context)
    }
}

@Composable
fun OutfitsCardsDisplay(context: Context)
{
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        content = { items(outfitsList.value){ image ->
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
                            .makeText(
                                context,
                                "",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                    }
            ){
                Column(
                    horizontalAlignment =  Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    AsyncImage(
                        model = image.clothingItemData.uri,
                        contentDescription = image.clothingItemData.name,
                        modifier = Modifier
                            .fillMaxHeight(0.8f)
                    )
                    Text(text = image.clothingItemData.name)
                }
            }
        }
        }
    )
}