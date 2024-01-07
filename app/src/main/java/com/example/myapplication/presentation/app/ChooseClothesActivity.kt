package com.example.myapplication.presentation.app

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.data.firebase.FirebaseClothingItem
import com.example.myapplication.presentation.screens.fullClothingItemsList

class ChooseClothesActivity: ComponentActivity() {

    companion object{
        val SPEECH_TO_TEXT = 500
    }

    private val clothingItemType: HashMap<String, String> = hashMapOf(
        "Hat" to "Head",
        "Longsleeve" to "Upper",
        "Shortsleeve" to "Upper",
        "Dress" to "Long",
        "Pants" to "Lower",
        "Shorts" to "Lower",
        "Skirt" to "Lower",
        "Shoes" to "Feet"
    )

    private var requestCode = 0
    private val requestedTypeItems = mutableListOf<FirebaseClothingItem>()

    private val queriedItems = mutableStateOf(listOf<FirebaseClothingItem>())
    private val searchQuery = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            if(intent.hasExtra("request_code")){
                requestCode = intent.getIntExtra("request_code", 0)
            }

            if (requestCode == 0){
                Toast.makeText(this, "Error finding available clothing items!", Toast.LENGTH_LONG).show()
                finish()
            }

            when(requestCode){
                CreateOutfitsActivity.CHOOSE_HEAD_ITEM -> { getAllItemsOfType("Head") }
                CreateOutfitsActivity.CHOOSE_UPPER_BODY_ITEM -> { getAllItemsOfType("Upper") }
                CreateOutfitsActivity.CHOOSE_LONG_ITEM -> { getAllItemsOfType("Long") }
                CreateOutfitsActivity.CHOOSE_LOWER_BODY_ITEM -> { getAllItemsOfType("Lower") }
                CreateOutfitsActivity.CHOOSE_FEET_ITEM -> { getAllItemsOfType("Feet") }
            }

            queriedItems.value = requestedTypeItems

            Screen()
        }
    }

    private fun displayQueriedItems(query: String)
    {
        searchQuery.value = query

        if (searchQuery.value.isEmpty()){
            queriedItems.value = requestedTypeItems
        }
        else{
            val searchValuesList = mutableListOf<FirebaseClothingItem>()

            for (item in requestedTypeItems){
                if(item.clothingItemData.name.toLowerCase().contains(searchQuery.value.toLowerCase())){
                    searchValuesList.add(item)
                }
            }

            queriedItems.value = searchValuesList
        }
    }

    private fun processSpeechData(data: Intent?)
    {
        val spokenText: String? =
            data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let { results ->
                results?.get(0)
            }

        if (spokenText != null) {
            displayQueriedItems(spokenText)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            SPEECH_TO_TEXT -> if (resultCode == RESULT_OK) {
                processSpeechData(data)
            }

            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun getAllItemsOfType(type: String){
        for (item in fullClothingItemsList){
            if (clothingItemType[item.clothingItemData.type] == type){
                requestedTypeItems.add(item)
            }
        }
    }

    private fun returnChosenItem(item: FirebaseClothingItem?)
    {
        val data = Intent()
        data.putExtra("item", item)
        setResult(RESULT_OK, data)
        finish()
    }

    private fun checkItemInfo(item: FirebaseClothingItem)
    {
        val intent = Intent(this, ClothesInfoActivity::class.java)
        intent.putExtra("item_info", item)
        startActivity(intent)
    }

    @Composable
    fun ItemCard(item: FirebaseClothingItem)
    {
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
        ){
            Column(
                horizontalAlignment =  Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .clickable { returnChosenItem(item) }
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Icon",
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable { checkItemInfo(item) }
                )
                AsyncImage(
                    model = item.clothingItemData.uri,
                    contentDescription = item.clothingItemData.name,
                    modifier = Modifier
                        .fillMaxHeight(0.8f)
                        .clip(
                            RoundedCornerShape(10.dp)
                        )
                )
                Text(
                    text = item.clothingItemData.name,
                    color = Color(getColor(R.color.primary_orange)),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                )
            }
        }
    }

    @Composable
    fun CardsDisplay()
    {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            content = { items(queriedItems.value){ image ->
                    ItemCard(image)
                }
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ScaffoldTopBar()
    {
        val color = Color(getColor(R.color.primary_orange))

        CenterAlignedTopAppBar (
            navigationIcon = {
                IconButton(onClick = { finish() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Icon",
                        tint = color
                    )
                }
            },
            title = {
                Text(
                    text = "Choose Clothing Item",
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                )
            },
            actions = {
                IconButton(onClick = {
                    returnChosenItem(null)
                }) {
                    Icon(
                        imageVector = Icons.Filled.Cancel,
                        contentDescription = "Icon",
                        tint = color
                    )
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = Color.Transparent
            )

        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ItemsSearchBar()
    {
        val interaction = remember { MutableInteractionSource() }
        BasicTextField(
            value = searchQuery.value,
            onValueChange = {
                displayQueriedItems(it)
            },
            interactionSource = interaction,
            decorationBox = {inner ->
                TextFieldDefaults.TextFieldDecorationBox(
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Icon",
                            tint = Color.White
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Mic,
                            contentDescription = "Icon",
                            tint = Color.White,
                            modifier = Modifier.clickable {

                                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                }

                                startActivityForResult(intent, SPEECH_TO_TEXT)
                            }
                        )
                    },
                    value = searchQuery.value,
                    innerTextField = inner,
                    enabled = true,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interaction,
                    contentPadding = PaddingValues(2.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(getColor(R.color.primary_orange))
                    )
                )
            },
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            ),
            modifier = Modifier
                .fillMaxHeight(0.06f)
                .fillMaxWidth(0.7f)
                .clip(shape = RoundedCornerShape(25.dp))
        )
    }

    @Composable
    fun ScaffoldBody(paddingValues: PaddingValues){
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.Transparent)
        ){

            ItemsSearchBar()

            CardsDisplay()
        }
    }

    @Composable
    fun Background() {

        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            translate(left = 0f, top = 600f) {
                drawCircle(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(getColor(R.color.primary_orange)),
                            Color(getColor(R.color.secondary_orange)),
                        )
                    ),
                    radius = 360.dp.toPx()
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Screen()
    {
        Background()
        Scaffold(
            topBar = { ScaffoldTopBar() },
            containerColor = Color.Transparent,
            modifier = Modifier
                .fillMaxSize()
        ) { paddingValues ->
            ScaffoldBody(paddingValues = paddingValues)
        }
    }
}