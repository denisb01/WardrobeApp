package com.example.myapplication.presentation.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.R

@Composable
fun HelpScreen(navController: NavController, context: Context)
{
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ){
        HelpTitle(context = context)
        HelpContentSection(context = context)
    }
}

@Composable
fun HelpTitle(context: Context)
{
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.15f)
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
                text = "Help",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(context.getColor(R.color.primary_orange))
            )
        }
    }
}

@Composable
fun HelpSubtitleContent(context: Context, text: String)
{
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xffedc07e))
    ) {
        Text(
            text = text,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(0.dp, 10.dp)
                .fillMaxWidth(0.9f)
        )
    }
}

@Composable
fun HelpSubtitle(context: Context, startState: Boolean, text: String, SubtitleContent: @Composable() (() -> Unit))
{
    val displaySubtitleContent = remember { mutableStateOf(startState) }

    Box(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(60.dp)
            .clip(
                if(displaySubtitleContent.value) RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp)
                else RoundedCornerShape(20.dp, 20.dp, 20.dp, 20.dp)
            )
            .background(Color(context.getColor(R.color.secondary_orange)))
            .clickable {
                displaySubtitleContent.value = !displaySubtitleContent.value
            }
    ){
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.Center)
        )
        Icon(
            imageVector = if(displaySubtitleContent.value) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
            contentDescription = "Icon",
            tint = Color.White,
            modifier = Modifier
                .padding(10.dp,0.dp)
                .scale(2f)
                .align(Alignment.CenterEnd)
        )
    }

    if (displaySubtitleContent.value){
        SubtitleContent()
    }
}

@Composable
fun HelpContentSection(context: Context)
{
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
    ){
        item {
            HelpSubtitle(context = context, startState = true, text = "Introduction"){
                HelpSubtitleContent(
                    context = context,
                    text = "Welcome to SnapFit, your virtual wardrobe organizer! " +
                            "This user guide will walk you through the steps to make the most out of your experience with SnapFit, " +
                            "the innovative mobile application designed to streamline and enhance your closet management."
                )
            }
        }
        item {
            HelpSubtitle(context = context, startState = false, text = "Getting Started"){
                HelpSubtitleContent(
                    context = context,
                    text = "To start your journey, first you need to add some clothes to your virtual wardrobe by taking pictures.\n" +
                            "1. To effortlessly take pictures of your clothing items you can utilize SnapFit's built-in camera feature located " +
                            "on the center of the bottom navigation bar. For clear and accurate classification by the Object Detection AI, " +
                            "you need to ensure good lighting and neutral background.\n" +
                            "2. After taking a picture of your clothing item, our Object Detection AI will classify your clothing item into " +
                            "8 classes. These classes are: Shoes, Shorts, Pants, Skirts, Long-sleeve Upper Body Item, Short-sleeve upper body " +
                            "item, Dress and Hat\n" +
                            "3. if your item was successfully classified, you can dive into the details by adding specific features for each " +
                            "clothing item, such as size, material, color, gender and age.\n" +
                            "4. When every clothing item feature was added, the Save button will unlock, allowing you to save clothing item " +
                            "to your virtual wardrobe."
                )
            }
        }

        item {
            HelpSubtitle(context = context, startState = false, text = "Browsing Wardrobe"){
                HelpSubtitleContent(
                    context = context,
                    text = "After adding some clothing items, you can view their information and share them.\n" +
                            "1. To view all clothing items added by you, firstly you should navigate to Clothes " +
                            "Screen by pressing on Clothes button located on the bottom navigation bar.\n" +
                            "2. After you successfully navigated to Clothes Screen, you can see the clothing items " +
                            "added by you displayed on Cards.\n" +
                            "3. To find a specific clothing item, you can use the search bar and introduce the name" +
                            "of the item you are looking for, or you can press on the mic button and search using your voice.\n" +
                            "4. To see the information of a specific clothing item, you can click on a clothing item card. " +
                            "and a new screen will appear displaying all of your items information.\n" +
                            "5. To share a clothing item, click on the share icon located in the top right corner on the items " +
                            "information screen."
                )
            }
        }

        item {
            HelpSubtitle(context = context, startState = false, text = "Create Outfits"){
                HelpSubtitleContent(
                    context = context,
                    text = "After adding some clothing items, another thing you can do is create outfits with them.\n" +
                            "1. To add a new outfit, firstly you should navigate to Outfits " +
                            "Screen by pressing on Outfits button located on the bottom navigation bar.\n" +
                            "2. After you successfully navigated to Outfits Screen, you can add new outfits " +
                            "by pressing the '+' floating button located in the bottom right part of the screen.\n" +
                            "3. After pressing the button you will be presented with a choice for outfit type you " +
                            "want to create. You can choose Short Outfit type (feet item, lower body item like pants, upper " +
                            "body item like shirt and optional hat item) or Long Outfit type (feet item, long item like a dress " +
                            "and optional hat item).\n" +
                            "4. After choosing outfit type you will be presented with a template for your outfit. To add items to your " +
                            "outfit click on the '+' on any of the template items.\n" +
                            "5. After clicking on the '+' on any of the template items, you will be presented with a screen to choose " +
                            "only from the clothing items corresponding to the template you clicked on. You can search for a specific " +
                            "clothing item using the search bar (More about searching in Browsing Wardrobe section chapter 3).\n" +
                            "6. After completing your outfit by adding every clothing item on template (Hat is optional), the save button " +
                            "located on the top right corner of the screen will unlock.\n" +
                            "7. Upon clicking on the save button, a dialog will appear to introduce outfit features like gender, season, " +
                            "occasion and age.\n" +
                            "8. After introducing every feature of your outfit, the save button will unlock and you can save your outfit in " +
                            "your virtual wardrobe."
                )
            }
        }

        item {
            HelpSubtitle(context = context, startState = false, text = "Browsing Outfits"){
                HelpSubtitleContent(
                    context = context,
                    text = "After adding some outfits, you can view their information.\n" +
                            "1. To view all outfits added by you, firstly you should navigate to Outfits " +
                            "Screen by pressing on Outfits button located on the bottom navigation bar.\n" +
                            "2. After you successfully navigated to Outfits Screen, you can see the outfits " +
                            "added by you displayed on Cards.\n" +
                            "3. To find a specific outfit, you can use the search bar and introduce the name" +
                            "of the outfit you are looking for, or you can press on the mic button and search using your voice.\n" +
                            "4. To see the information of a specific outfit, you can click on a card. " +
                            "and a new screen will appear displaying all of your outfit information.\n" +
                            "5. To view information about clothing items used in your outfit, press on the image of a " +
                            "clothing item in the outfits information screen."
                )
            }
        }
    }
}