package com.example.myapplication.presentation.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Man
import androidx.compose.material.icons.filled.SevereCold
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.data.firebase.FirebaseClothingItem
import com.example.myapplication.data.firebase.FirebaseOutfit
import com.example.myapplication.data.firebase.FirebaseOutfitModel
import com.example.myapplication.data.outfits.LongOutfit
import com.example.myapplication.data.outfits.Outfit
import com.example.myapplication.data.outfits.ShortOutfit
import com.example.myapplication.presentation.screens.findClothingItemByID

class OutfitInfoActivity: ComponentActivity() {

    private lateinit var outfit: FirebaseOutfit
    private lateinit var outfitData: FirebaseOutfitModel

    private val primaryOrangeColor = Color(0xffeb971c)

    private lateinit var outfitItems: Outfit

    private var isLongOutfit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            if(intent.hasExtra("outfit_info")){
                outfit = intent.getSerializableExtra("outfit_info") as FirebaseOutfit
                outfitData = outfit.outfitData

                isLongOutfit = outfit.outfitData.type == "Long"

                outfitItems = retrieveClothingItems()
            }
            else{
                Toast.makeText(this, "Error loading clothes info!", Toast.LENGTH_LONG).show()
                finish()
            }

            Screen()
        }
    }

    private fun retrieveClothingItems(): Outfit {
        return if (isLongOutfit)
            LongOutfit(
                headItem = if (outfitData.headItemID != null) findClothingItemByID(outfitData.headItemID!!) else null,
                longItem = findClothingItemByID(outfitData.longItemID!!),
                feetItem = findClothingItemByID(outfitData.feetItemID!!)
            )
        else
            ShortOutfit(
                headItem = if (outfitData.headItemID != null) findClothingItemByID(outfitData.headItemID!!) else null,
                upperBodyItem = findClothingItemByID(outfitData.upperBodyItemID!!),
                lowerBodyItem = findClothingItemByID(outfitData.lowerBodyItemID!!),
                feetItem = findClothingItemByID(outfitData.feetItemID!!)
            )
    }

    @Composable
    fun Background() {

        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            translate(left = -100f, top = 100f) {
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
    fun TopBar()
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
                    text = "Outfit Info",
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                )
            },

            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = Color.Transparent
            )

        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Screen()
    {
        Background()
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = { TopBar() },
                modifier = Modifier.fillMaxSize()
            ) { paddingValues ->
                MainScaffoldContent(paddingValues = paddingValues)
            }
        }
    }

    @Composable
    fun ClothingItemImage(clothingItem: FirebaseClothingItem,sizes: Pair<Dp, Dp>)
    {
        AsyncImage(
            model = clothingItem.clothingItemData.uri,
            contentDescription = clothingItem.clothingItemData.name,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .width(sizes.first)
                .height(sizes.second)
                .clip(
                    RoundedCornerShape(10.dp)
                )
                .border(
                    2.dp,
                    Color(getColor(R.color.primary_orange)),
                    RoundedCornerShape(10.dp)
                )
                .clickable {
                    val intent = Intent(baseContext, ClothesInfoActivity::class.java)
                    intent.putExtra("item_info", clothingItem)
                    startActivity(intent)
                }
        )
    }

    @Composable
    fun DisplayLongOutfit(longOutfitItems: LongOutfit)
    {
        if(longOutfitItems.headItem != null)
            ClothingItemImage(clothingItem = longOutfitItems.headItem!!, sizes = Pair(60.dp,80.dp))

        ClothingItemImage(clothingItem = longOutfitItems.longItem, sizes = Pair(120.dp,160.dp))
        ClothingItemImage(clothingItem = longOutfitItems.feetItem, sizes = Pair(60.dp,80.dp))
    }

    @Composable
    fun DisplayShortOutfit(shortOutfitItems: ShortOutfit)
    {
        if(shortOutfitItems.headItem != null)
            ClothingItemImage(clothingItem = shortOutfitItems.headItem!!, sizes = Pair(60.dp,80.dp))

        ClothingItemImage(clothingItem = shortOutfitItems.upperBodyItem, sizes = Pair(90.dp,120.dp))
        ClothingItemImage(clothingItem = shortOutfitItems.lowerBodyItem, sizes = Pair(90.dp,120.dp))
        ClothingItemImage(clothingItem = shortOutfitItems.feetItem, sizes = Pair(60.dp,80.dp))
    }

    @Composable
    fun OutfitCard()
    {

        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 20.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            modifier = Modifier
                .width(240.dp)
                .requiredWidth(240.dp)
                .height(500.dp)
                .requiredHeight(500.dp)
        ){
            Column(
                horizontalAlignment =  Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if(isLongOutfit) DisplayLongOutfit(longOutfitItems = outfitItems as LongOutfit)
                    else DisplayShortOutfit(shortOutfitItems = outfitItems as ShortOutfit)
                }
            }
        }
    }

    @Composable
    fun IconInfoText(imageVector: ImageVector, titleText: String, subtitleText: String)
    {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(0.dp, 10.dp)
                .fillMaxWidth(0.8f)
                .height(80.dp)
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = "Icon",
                tint = primaryOrangeColor,
                modifier = Modifier
                    .padding(10.dp, 0.dp)
                    .height(40.dp)
                    .width(40.dp)
            )

            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(10.dp, 10.dp)
                    .fillMaxHeight()
                    .fillMaxWidth()
            ) {
                Text(
                    text = titleText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = primaryOrangeColor
                )

                Text(
                    text = subtitleText,
                    fontSize = 22.sp,
                    color = primaryOrangeColor
                )
            }
        }
    }

    @Composable
    fun InfoColumns()
    {
        Column{
            IconInfoText(
                imageVector = Icons.Filled.Category,
                titleText = "Clothing Item Type",
                subtitleText = outfit.outfitData.type
            )

            Divider(
                thickness = 2.dp,
                color = primaryOrangeColor,
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 10.dp)
                    .width(300.dp)
            )

            IconInfoText(
                imageVector = if (outfit.outfitData.gender == "Male") Icons.Filled.Male else Icons.Filled.Female,
                titleText = "Outfit Gender",
                subtitleText = outfit.outfitData.gender
            )

            Divider(
                thickness = 2.dp,
                color = primaryOrangeColor,
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 10.dp)
                    .width(300.dp)
            )

            IconInfoText(
                imageVector = if(outfit.outfitData.season == CreateOutfitsActivity.OutfitSeason.SPRING.season ||
                                 outfit.outfitData.season == CreateOutfitsActivity.OutfitSeason.SUMMER.season)
                             Icons.Filled.Whatshot
                        else Icons.Filled.SevereCold,
                titleText = "Outfit For",
                subtitleText = outfit.outfitData.season
            )

            Divider(
                thickness = 2.dp,
                color = primaryOrangeColor,
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 10.dp)
                    .width(300.dp)
            )

            IconInfoText(
                imageVector = Icons.Filled.Man,
                titleText = "Outfit Occasion",
                subtitleText = outfit.outfitData.occasion
            )

            Divider(
                thickness = 2.dp,
                color = primaryOrangeColor,
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 10.dp)
                    .width(300.dp)
            )

            IconInfoText(
                imageVector = if(outfit.outfitData.age == CreateOutfitsActivity.OutfitAge.ADULT.age) Icons.Filled.Face else Icons.Filled.ChildCare,
                titleText = "Outfit For",
                subtitleText = outfit.outfitData.age
            )

        }
    }

    @Composable
    fun InfoCard()
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .height(630f.dp)
                .clip(
                    RoundedCornerShape(50.dp, 50.dp, 0.dp, 0.dp)
                )
                .border(
                    2.dp,
                    Color.White,
                    RoundedCornerShape(50.dp, 50.dp, 0.dp, 0.dp)
                )
                .background(Color.White)
        ){
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.125f)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(getColor(R.color.secondary_orange)),
                                Color.White,
                            )
                        )
                    )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(0.dp, 10.dp)
                        .width(240.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(15.dp))
                        .background(primaryOrangeColor)
                ) {
                    Text(
                        text = outfit.outfitData.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp
                    )
                }
            }

            InfoColumns()
        }
    }

    @Composable
    fun MainScaffoldContent(paddingValues: PaddingValues)
    {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){
            item {
                OutfitCard()
            }
            item {
                InfoCard()
            }
        }
    }
    
}