package com.example.myapplication.presentation.app

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Texture
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.data.FirebaseClothingItem
import com.example.myapplication.data.FirebaseImageModel
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import kotlin.concurrent.thread


class ClothesInfoActivity: ComponentActivity() {

    private lateinit var clothingItem: FirebaseClothingItem
    private lateinit var clothingItemData: FirebaseImageModel
    private val primaryOrangeColor = Color(0xffeb971c)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            if(intent.hasExtra("image_info")){
                clothingItem = intent.getSerializableExtra("image_info") as FirebaseClothingItem
                clothingItemData = clothingItem.clothingItemData
            }
            else{
                Toast.makeText(this, "Error loading clothes info!", Toast.LENGTH_LONG).show()
                finish()
            }

            Screen()
        }
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

    private fun shareImage()
    {
        thread(start = true)
        {
            var url: URL? = null
            try { url = URL(clothingItemData.uri) }
            catch (e: MalformedURLException) { e.printStackTrace() }

            var connection: HttpURLConnection? = null
            try { connection = url!!.openConnection() as HttpURLConnection }
            catch (e: IOException) { e.printStackTrace() }

            connection!!.doInput = true
            try { connection.connect() }
            catch (e: IOException) { e.printStackTrace() }

            var input: InputStream? = null
            try { input = connection.inputStream }
            catch (e: IOException) { e.printStackTrace() }

            val imgBitmap = BitmapFactory.decodeStream(input)
            val imgBitmapPath = MediaStore.Images.Media.insertImage(
                contentResolver, imgBitmap,
                clothingItemData.name, null
            )
            val imgBitmapUri = Uri.parse(imgBitmapPath)

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            shareIntent.putExtra(Intent.EXTRA_STREAM, imgBitmapUri)
            shareIntent.type = "image/png"
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            shareIntent.putExtra(Intent.EXTRA_TEXT, clothingItemData.name)

            startActivity(Intent.createChooser(shareIntent, "Share Clothing Item"))
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
                    text = "Clothing Item Info",
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                )
            },

            actions = {
                IconButton(
                    onClick = { shareImage() }
                ){
                    Icon(
                        imageVector = Icons.Filled.Share,
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

    @Composable
    fun ImageCard()
    {
        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 20.dp
            ),
            modifier = Modifier
                .width(240.dp)
                .requiredWidth(240.dp)
                .height(320.dp)
                .requiredHeight(320.dp)
        ){
            Column(
                horizontalAlignment =  Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                AsyncImage(
                    model = clothingItemData.uri,
                    contentDescription = clothingItemData.name,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxSize()
                )
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
    fun FirstInfoColumn()
    {
        Column{
            IconInfoText(
                imageVector = Icons.Filled.Category,
                titleText = "Clothing Item Type",
                subtitleText = clothingItemData.type
            )

            Divider(
                thickness = 2.dp,
                color = primaryOrangeColor,
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 10.dp)
                    .width(300.dp)
            )

            IconInfoText(
                imageVector = Icons.Filled.ColorLens,
                titleText = "Clothing Item Color",
                subtitleText = clothingItemData.color
            )

            Divider(
                thickness = 2.dp,
                color = primaryOrangeColor,
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 10.dp)
                    .width(300.dp)
            )

            IconInfoText(
                imageVector = Icons.Filled.Texture,
                titleText = "Clothing Material",
                subtitleText = clothingItemData.material
            )
        }
    }

    @Composable
    fun SecondInfoColumn()
    {
        Column{
            IconInfoText(
                imageVector = if (clothingItemData.gender == "Male") Icons.Filled.Male else Icons.Filled.Female,
                titleText = "Clothing Item Gender",
                subtitleText = clothingItemData.gender
            )

            Divider(
                thickness = 2.dp,
                color = primaryOrangeColor,
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 10.dp)
                    .width(300.dp)
            )

            IconInfoText(
                imageVector = Icons.Filled.FormatSize,
                titleText = "Clothing Item Size",
                subtitleText = clothingItemData.size
            )

            Divider(
                thickness = 2.dp,
                color = primaryOrangeColor,
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 10.dp)
                    .width(300.dp)
            )

            IconInfoText(
                imageVector = if(clothingItemData.age == "Adults") Icons.Filled.Face else Icons.Filled.ChildCare,
                titleText = "Clothing Item For",
                subtitleText = clothingItemData.age
            )

        }
    }

    @Composable
    fun InfoCard()
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .clip(
                    RoundedCornerShape(50.dp, 50.dp, 0.dp, 0.dp)
                )
                .background(Color.White)
        ){
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(0.dp, 10.dp)
                    .width(240.dp)
                    .fillMaxHeight(0.125f)
                    .clip(RoundedCornerShape(15.dp))
                    .background(primaryOrangeColor)
            ){
                Text(
                    text = clothingItemData.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(30.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.8f)
                    .horizontalScroll(rememberScrollState())
            ){
                FirstInfoColumn()
                SecondInfoColumn()
            }
        }
    }

    @Composable
    fun MainScaffoldContent(paddingValues: PaddingValues)
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ImageCard()
            InfoCard()
        }
    }
}