package com.example.myapplication.presentation.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.data.firebase.FirebaseClothingItem
import com.example.myapplication.data.firebase.FirebaseClothingItemModel
import com.example.myapplication.data.firebase.FirebaseOutfit
import com.example.myapplication.data.firebase.FirebaseOutfitModel
import com.example.myapplication.data.outfits.LongOutfit
import com.example.myapplication.data.outfits.ShortOutfit
import com.example.myapplication.navigation.auth
import com.example.myapplication.navigation.firebaseController
import com.example.myapplication.presentation.app.AppActivity
import com.example.myapplication.presentation.app.CreateOutfitsActivity
import com.example.myapplication.presentation.app.OutfitGenerationActivity
import com.example.myapplication.presentation.app.OutfitInfoActivity

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
fun CreationMethodDialogTitle(context: Context)
{
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.15f)
    ) {
        Text(
            text = "Outfit Creation Method",
            color = Color(context.getColor(R.color.primary_orange)),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CreationMethodDialogContent(context: Context)
{
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.4f)
    ) {
        Text(
            text = "Choose how you want to create your outfit.\n\n" +
                    "You can create your outfits manually or generate them by your preferences.",
            color = Color(context.getColor(R.color.primary_orange)),
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth(0.9f)
        )
    }
}

@Composable
fun CreationMethodButton(text: String, action: () -> Unit, context: Context, enabled: Boolean = true)
{
    Button(
        onClick = action,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(context.getColor(R.color.primary_orange))
        ),
        modifier = Modifier
            .fillMaxWidth(0.7f)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            color = Color.White
        )
    }
}

@Composable
fun CreationMethodDialogButtons(context: Context, showDialog: MutableState<Boolean>)
{
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        CreationMethodButton(
            text = "Manual",
            action = {
                val intent = Intent(context, CreateOutfitsActivity::class.java)
                context.startActivity(intent)
            },
            context = context
        )

        CreationMethodButton(
            text = "Generate",
            enabled = fullOutfitsList.isNotEmpty(),
            action = {
                val intent = Intent(context, OutfitGenerationActivity::class.java)
                context.startActivity(intent)
            },
            context = context
        )

        CreationMethodButton(
            text = "Cancel",
            action = {
                showDialog.value = !showDialog.value
            },
            context = context
        )
    }
}

@Composable
fun CreationMethodDialog(showDialog: MutableState<Boolean>, context: Context)
{
    Dialog(
        onDismissRequest = {
            showDialog.value = false
        },
        properties = DialogProperties(
            dismissOnClickOutside = false
        )
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            modifier = Modifier
                .width(350.dp)
                .height(400.dp)
        ){
            CreationMethodDialogTitle(context)
            CreationMethodDialogContent(context)
            CreationMethodDialogButtons(context, showDialog)
        }
    }
}

@Composable
fun AddOutfitsButton(context: Context)
{
    val showDialog = remember { mutableStateOf(false) }

    FloatingActionButton(
        containerColor = Color(context.getColor(R.color.primary_orange)),
        onClick = {
            showDialog.value = true
        }
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = "Add",
            tint = Color.White
        )
    }

    if (showDialog.value){
        CreationMethodDialog(
            showDialog = showDialog,
            context = context
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
                val intent = Intent(context, OutfitInfoActivity::class.java)
                intent.putExtra("outfit_info", outfit)
                context.startActivity(intent)
            }
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(context.getColor(R.color.secondary_orange)),
                            Color.White
                        )
                    )
                )
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(0.dp, 5.dp, 0.dp, 5.dp)
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight(0.85f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
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

fun findClothingItemByID(id: String): FirebaseClothingItem {
    for (clothing in fullClothingItemsList){
        if(clothing.key == id) return clothing
    }

    return FirebaseClothingItem("None", FirebaseClothingItemModel())
}

@Composable
fun OutfitScreenItemImage(item: FirebaseClothingItem, sizes: Pair<Dp,Dp>)
{
    AsyncImage(
        model = item.clothingItemData.uri,
        contentDescription = item.clothingItemData.name,
        modifier = Modifier
            .width(sizes.first)
            .height(sizes.second)
            .clip(
                RoundedCornerShape(10.dp)
            )
    )
}

fun retrieveShortClothingItems(outfit: FirebaseOutfitModel): ShortOutfit
{
    return ShortOutfit(
        headItem = if(outfit.headItemID != null) findClothingItemByID(outfit.headItemID!!) else null,
        upperBodyItem = findClothingItemByID(outfit.upperBodyItemID!!),
        lowerBodyItem = findClothingItemByID(outfit.lowerBodyItemID!!),
        feetItem = findClothingItemByID(outfit.feetItemID!!)
    )
}

@Composable
fun ShortOutfitCardImages(outfit: FirebaseOutfitModel, context: Context) {
    val shortOutfitItems = retrieveShortClothingItems(outfit)

    if(shortOutfitItems.headItem != null)
        OutfitScreenItemImage(item = shortOutfitItems.headItem!!, sizes = Pair(30.dp,30.dp))

    OutfitScreenItemImage(item = shortOutfitItems.upperBodyItem, sizes = Pair(30.dp,40.dp))
    OutfitScreenItemImage(item = shortOutfitItems.lowerBodyItem, sizes = Pair(30.dp,40.dp))
    OutfitScreenItemImage(item = shortOutfitItems.feetItem, sizes = Pair(30.dp,30.dp))
}

fun retrieveLongClothingItems(outfit: FirebaseOutfitModel): LongOutfit
{
    return LongOutfit(
        headItem = if(outfit.headItemID != null) findClothingItemByID(outfit.headItemID!!) else null,
        longItem = findClothingItemByID(outfit.longItemID!!),
        feetItem = findClothingItemByID(outfit.feetItemID!!)
    )
}

@Composable
fun LongOutfitCardImages(outfit: FirebaseOutfitModel, context: Context) {
    val longOutfitItems = retrieveLongClothingItems(outfit)

    if(longOutfitItems.headItem != null)
        OutfitScreenItemImage(item = longOutfitItems.headItem!!, sizes = Pair(30.dp,30.dp))

    OutfitScreenItemImage(item = longOutfitItems.longItem, sizes = Pair(30.dp,50.dp))
    OutfitScreenItemImage(item = longOutfitItems.feetItem, sizes = Pair(30.dp,30.dp))
}
