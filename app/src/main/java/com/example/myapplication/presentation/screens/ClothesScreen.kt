package com.example.myapplication.presentation.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.data.FirebaseImageModel
import com.example.myapplication.navigation.auth
import com.example.myapplication.navigation.firebaseController
import com.example.myapplication.presentation.app.AppActivity

val imagesList = mutableStateOf(listOf<FirebaseImageModel>())
var fullImagesList = listOf<FirebaseImageModel>()

val clothesSearchCriteria = mutableStateOf("")

fun displayImageCards(list: List<FirebaseImageModel>)
{
    imagesList.value = list
}

fun displaySearchedImages()
{
    if (clothesSearchCriteria.value.isEmpty()){
        displayImageCards(fullImagesList)
    }
    else{
        val searchValuesList = mutableListOf<FirebaseImageModel>()

        for (image in fullImagesList){
            if(image.name.toLowerCase().contains(clothesSearchCriteria.value.toLowerCase())){
                searchValuesList.add(image)
            }
        }

        displayImageCards(searchValuesList)
    }
}

@Composable
fun ClothesScreen(navController: NavController, context: Context)
{
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ){
        val imagesListFlow = firebaseController.getImages(auth.currentUser!!)
        fullImagesList = imagesListFlow.collectAsState(emptyList()).value

        displayImageCards(fullImagesList)

        SearchBar(context, clothesSearchCriteria, AppActivity.CLOTHES_SPEECH_REQUEST_CODE, ::displaySearchedImages)

        ClothesCardsDisplay(context = context)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(context: Context, searchCriteria: MutableState<String>, speechRequestCode: Int, searchDisplay: () -> Unit)
{
    val interaction = remember { MutableInteractionSource() }
    BasicTextField(
        value = searchCriteria.value,
        onValueChange = {
            searchCriteria.value = it

            searchDisplay()
        },
        interactionSource = interaction,
        decorationBox = {inner ->
            TextFieldDefaults.TextFieldDecorationBox(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Icon",
                        tint = Color(context.getColor(R.color.primary_orange))
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Mic,
                        contentDescription = "Icon",
                        tint = Color(context.getColor(R.color.primary_orange)),
                        modifier = Modifier.clickable {
                            val activity = context as Activity

                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            }

                            activity.startActivityForResult(intent, speechRequestCode)
                        }
                    )
                },
                value = searchCriteria.value,
                innerTextField = inner,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = interaction,
                contentPadding = PaddingValues(2.dp),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White
                )
            )
        },
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(context.getColor(R.color.primary_orange))
        ),
        modifier = Modifier
            .fillMaxHeight(0.06f)
            .fillMaxWidth(0.7f)
            .clip(shape = RoundedCornerShape(25.dp))
    )
}

// TODO Modify Cards
@Composable
fun ClothesCardsDisplay(context: Context)
{
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        content = { items(imagesList.value){ image ->
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
                                    image.name + " : " + image.type,
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