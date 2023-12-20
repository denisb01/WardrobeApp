package com.example.myapplication.presentation.screens

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.data.FirebaseClothingItemModel
import com.example.myapplication.data.FirebaseOutfit
import com.example.myapplication.data.FirebaseOutfitModel
import com.example.myapplication.navigation.auth
import com.example.myapplication.navigation.firebaseController
import com.example.myapplication.presentation.app.AppActivity
import com.example.myapplication.presentation.app.CreateOutfitsActivity

val outfitsList = mutableStateOf(listOf<FirebaseOutfit>())
var fullOutfitsList = listOf<FirebaseOutfit>()

val outfitsSearchCriteria = mutableStateOf("")

fun displayOutfitCards(list: List<FirebaseOutfit>)
{
    outfitsList.value = list
}

fun displaySearchedOutfits()
{
    if (outfitsSearchCriteria.value.isEmpty()){
        displayOutfitCards(fullOutfitsList)
    }
    else{
        val searchValuesList = mutableListOf<FirebaseOutfit>()

        for (outfit in fullOutfitsList){
            if(outfit.outfitData.name.toLowerCase().contains(outfitsSearchCriteria.value.toLowerCase())){
                searchValuesList.add(outfit)
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

// TODO Fix refreshing

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
        val outfitsListFlow = firebaseController.getOutfits(auth.currentUser!!)
        fullOutfitsList = outfitsListFlow.collectAsState(emptyList()).value

        displaySearchedOutfits()

        SearchBar(context, outfitsSearchCriteria, AppActivity.OUTFITS_SPEECH_REQUEST_CODE, ::displaySearchedOutfits)

        OutfitsCardsDisplay(context = context)
    }
}

@Composable
fun OutfitsCardsDisplay(context: Context) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        content = {
            items(outfitsList.value) { outfit ->
                OutfitCard(context = context, outfit = outfit)
            }
        }
    )
}

@Composable
fun OutfitCard(context: Context, outfit: FirebaseOutfit)
{
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 15.dp
        ),
        modifier = Modifier
            .width(150.dp)
            .requiredWidth(150.dp)
            .height(220.dp)
            .requiredHeight(220.dp)
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
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(0.dp, 5.dp, 0.dp, 0.dp)
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight(0.85f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(context.getColor(R.color.primary_orange)))
            ) {
                if(outfit.outfitData.type == "Long") LongOutfitCardImages(outfit.outfitData, context)
                else ShortOutfitCardImages(outfit.outfitData, context)
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ){
                Text(
                    text = outfit.outfitData.name,
                    color = Color(context.getColor(R.color.primary_orange)),
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

// TODO Add OnClick to start OutfitInfoActivity ( To Be Created)

fun findClothingItemByID(id: String): FirebaseClothingItemModel? {
    for (clothing in fullImagesList){
        if(clothing.key == id) return clothing.clothingItemData
    }

    return null
}

@Composable
fun ShortOutfitCardImages(outfit: FirebaseOutfitModel, context: Context) {
    val headItem = if(outfit.headItemID != null) findClothingItemByID(outfit.headItemID!!) else null
    val upperBodyItem = findClothingItemByID(outfit.upperBodyItemID!!)
    val lowerBodyItem = findClothingItemByID(outfit.lowerBodyItemID!!)
    val feetItem = findClothingItemByID(outfit.feetItemID!!)

    if(headItem != null){
        AsyncImage(
            model = headItem.uri,
            contentDescription = headItem.name,
            modifier = Modifier
                .width(30.dp)
                .height(30.dp)
                .clip(
                    RoundedCornerShape(10.dp)
                )
        )
    }

    AsyncImage(
        model = upperBodyItem?.uri,
        contentDescription = upperBodyItem?.name,
        modifier = Modifier
            .width(30.dp)
            .height(40.dp)
            .clip(
                RoundedCornerShape(10.dp)
            )
    )

    AsyncImage(
        model = lowerBodyItem?.uri,
        contentDescription = lowerBodyItem?.name,
        modifier = Modifier
            .width(30.dp)
            .height(40.dp)
            .clip(
                RoundedCornerShape(10.dp)
            )
    )

    AsyncImage(
        model = feetItem?.uri,
        contentDescription = feetItem?.name,
        modifier = Modifier
            .width(30.dp)
            .height(30.dp)
            .clip(
                RoundedCornerShape(10.dp)
            )
    )
}

@Composable
fun LongOutfitCardImages(outfit: FirebaseOutfitModel, context: Context) {
    val headItem = if(outfit.headItemID != null) findClothingItemByID(outfit.headItemID!!) else null
    val longItem = findClothingItemByID(outfit.longItemID!!)
    val feetItem = findClothingItemByID(outfit.feetItemID!!)

    if(headItem != null){
        AsyncImage(
            model = headItem.uri,
            contentDescription = headItem.name,
            modifier = Modifier
                .width(30.dp)
                .height(30.dp)
                .clip(
                    RoundedCornerShape(10.dp)
                )
        )
    }

    AsyncImage(
        model = longItem?.uri,
        contentDescription = longItem?.name,
        modifier = Modifier
            .width(30.dp)
            .height(50.dp)
            .clip(
                RoundedCornerShape(10.dp)
            )
    )

    AsyncImage(
        model = feetItem?.uri,
        contentDescription = feetItem?.name,
        modifier = Modifier
            .width(30.dp)
            .height(30.dp)
            .clip(
                RoundedCornerShape(10.dp)
            )
    )
}
