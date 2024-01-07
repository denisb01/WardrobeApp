package com.example.myapplication.presentation.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.data.firebase.FirebaseClothingItem
import com.example.myapplication.data.firebase.FirebaseOutfitModel
import com.example.myapplication.data.outfits.LongOutfit
import com.example.myapplication.data.outfits.Outfit
import com.example.myapplication.data.outfits.ShortOutfit
import com.example.myapplication.database.FirebaseController
import com.example.myapplication.presentation.screens.fullClothingItemsList
import com.example.myapplication.presentation.screens.fullOutfitsList
import com.example.myapplication.suggestion.ContentBasedFiltering
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class OutfitGenerationActivity: ComponentActivity() {

    private val headItemSizes = Pair(100.dp, 100.dp)
    private val upperBodyItemSizes = Pair(120.dp, 180.dp)
    private val longItemSizes = Pair(160.dp, 300.dp)
    private val lowerBodyItemSizes = Pair(120.dp, 180.dp)
    private val feetItemSizes = Pair(120.dp, 120.dp)

    private val enableGenerateButton = mutableStateOf(false)
    private val outfit = FirebaseOutfitModel()
    private var outfitItems: MutableState<Outfit?> = mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val showDialog = remember { mutableStateOf(true) }

            Background()

            if (showDialog.value) ChooseOutfitFeaturesDialog(showDialog)
            else Screen()
        }
    }

    @Composable
    fun DialogButton(onClickEvent : () -> Unit, text: String, icon: ImageVector, enabled: Boolean = true)
    {
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(getColor(R.color.primary_orange))
            ),
            onClick = onClickEvent,
            modifier = Modifier
                .width(120.dp),
            contentPadding = PaddingValues(0.dp),
            enabled = enabled
        ) {
            Icon(icon, "Icon")
            Spacer(modifier = Modifier.fillMaxWidth(0.08f))
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }

    @Composable
    fun FeaturesDialogHeader()
    {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.15f)
        ) {
            Text(
                text = "Select Outfit Features",
                color = Color(getColor(R.color.primary_orange)),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FeaturesDialogField(fieldValue: MutableState<String>, readOnly: Boolean)
    {
        val interaction = remember { MutableInteractionSource() }
        BasicTextField(
            value = fieldValue.value,
            onValueChange = {
                fieldValue.value = it
            },
            readOnly = readOnly,
            enabled = !readOnly,
            interactionSource = interaction,
            decorationBox = {inner ->
                TextFieldDefaults.TextFieldDecorationBox(
                    value = fieldValue.value,
                    innerTextField = inner,
                    enabled = !readOnly,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interaction,
                    contentPadding = PaddingValues(8.dp, 0.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(getColor(R.color.primary_orange)),
                    )
                )
            },
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .width(150.dp)
                .height(30.dp)
        )
    }

    @Composable
    fun FeaturesDialogInput(fieldValue: MutableState<String>, text: String, readOnly: Boolean = false)
    {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(10.dp, 0.dp)
        ){
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(getColor(R.color.primary_orange))
            )
            FeaturesDialogField(fieldValue = fieldValue, readOnly = readOnly )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FeaturesSelectionBox(fieldValue: MutableState<String>, selectionItems: List<String>) {
        val expanded = remember { mutableStateOf(false) }

        Box {
            ExposedDropdownMenuBox(
                expanded = expanded.value,
                onExpandedChange = {
                    expanded.value = !expanded.value
                }
            ) {
                val interaction = remember { MutableInteractionSource() }
                BasicTextField(
                    value = fieldValue.value,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    interactionSource = interaction,
                    decorationBox = {inner ->
                        TextFieldDefaults.TextFieldDecorationBox(
                            value = fieldValue.value,
                            innerTextField = inner,
                            enabled = false,
                            singleLine = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                            visualTransformation = VisualTransformation.None,
                            interactionSource = interaction,
                            contentPadding = PaddingValues(8.dp, 0.dp),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color(getColor(R.color.primary_orange))
                            )
                        )
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                    ),
                    modifier = Modifier
                        .width(150.dp)
                        .height(30.dp)
                        .menuAnchor(),
                )
                ExposedDropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false },
                    modifier = Modifier
                        .height(100.dp)
                        .verticalScroll(rememberScrollState())
                        .background(Color.White)
                ) {
                    selectionItems.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item, color = Color(getColor(R.color.primary_orange))) },
                            onClick = {
                                fieldValue.value = item
                                expanded.value = false
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun FeaturesDialogSelection(fieldValue: MutableState<String>, text: String, selectionItems: List<String>)
    {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(10.dp, 0.dp)
        ){
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(getColor(R.color.primary_orange))
            )
            FeaturesSelectionBox(fieldValue, selectionItems)
        }
    }

    private fun checkOutfitFeatures(outfit: FirebaseOutfitModel, saveButtonState: MutableState<Boolean>)
    {
        saveButtonState.value = outfit.type.isNotEmpty() &&
                outfit.season.isNotEmpty() &&
                outfit.occasion.isNotEmpty() &&
                outfit.age.isNotEmpty() &&
                outfit.gender.isNotEmpty()
    }

    @Composable
    fun FeaturesDialogContent(generateButtonState: MutableState<Boolean>)
    {
        val outfitHasHatState = remember{ mutableStateOf(if (outfit.headItemID != null) "Yes" else "No" ) }
        val outfitTypeState = remember{ mutableStateOf(outfit.type) }
        val outfitSeasonState = remember{ mutableStateOf(outfit.season) }
        val outfitOccasionState = remember{ mutableStateOf(outfit.occasion) }
        val outfitAgeState = remember{ mutableStateOf(outfit.age) }
        val outfitGenderState = remember{ mutableStateOf(outfit.gender) }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
        ) {
            FeaturesDialogSelection(fieldValue = outfitHasHatState, text = "With hat?", selectionItems = listOf("Yes", "No"))
            FeaturesDialogSelection(fieldValue = outfitTypeState, text = "Outfit Type:", selectionItems = OutfitType.values().map { it.type })
            FeaturesDialogSelection(fieldValue = outfitSeasonState, text = "Outfit Season:", selectionItems = CreateOutfitsActivity.OutfitSeason.values().map { it.season })
            FeaturesDialogSelection(fieldValue = outfitOccasionState, text = "Outfit Occasion:", selectionItems = CreateOutfitsActivity.OutfitOccasions.values().map { it.occasion })
            FeaturesDialogSelection(fieldValue = outfitAgeState, text = "Outfit For:", selectionItems = CreateOutfitsActivity.OutfitAge.values().map { it.age })
            FeaturesDialogSelection(fieldValue = outfitGenderState, text = "Outfit Gender:", selectionItems = CreateOutfitsActivity.OutfitGender.values().map { it.gender })
        }
        outfit.headItemID = if (outfitHasHatState.value == "Yes") "" else null
        outfit.type = outfitTypeState.value
        outfit.season = outfitSeasonState.value
        outfit.occasion = outfitOccasionState.value
        outfit.age = outfitAgeState.value
        outfit.gender = outfitGenderState.value

        checkOutfitFeatures(outfit, generateButtonState)
    }

    private fun generateLongOutfit()
    {
        val generateOutfits = ContentBasedFiltering()
        val longOutfit = generateOutfits.generateLongOutfit(fullClothingItemsList, fullOutfitsList, outfit)

        outfitItems.value = longOutfit

        outfit.headItemID = if (longOutfit.headItem != null) longOutfit.headItem!!.key else null
        outfit.longItemID = longOutfit.longItem.key
        outfit.feetItemID = longOutfit.feetItem.key
    }

    private fun generateShortOutfit()
    {
        val generateOutfits = ContentBasedFiltering()
        val shortOutfit = generateOutfits.generateShortOutfit(fullClothingItemsList, fullOutfitsList, outfit)

        outfitItems.value = shortOutfit

        outfit.headItemID = if (shortOutfit.headItem != null) shortOutfit.headItem!!.key else null
        outfit.upperBodyItemID = shortOutfit.upperBodyItem.key
        outfit.lowerBodyItemID = shortOutfit.lowerBodyItem.key
        outfit.feetItemID = shortOutfit.feetItem.key
    }

    @Composable
    fun FeaturesDialogFooter(displayDialogState: MutableState<Boolean>, generateButtonState: MutableState<Boolean>)
    {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxSize()
        ){
            DialogButton(
                onClickEvent = {
                    displayDialogState.value = false
                },
                text = "Cancel",
                icon = Icons.Filled.Cancel
            )

            DialogButton(
                onClickEvent = {
                    if (outfit.type == OutfitType.LONG.type)
                        generateLongOutfit()
                    else
                        generateShortOutfit()

                    displayDialogState.value = false
                },
                enabled = generateButtonState.value,
                text = "Generate",
                icon = Icons.Filled.CheckCircle,
            )
        }
    }

    @Composable
    fun ChooseOutfitFeaturesDialog(displayDialogState: MutableState<Boolean>)
    {
        Dialog(onDismissRequest = {  }) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                FeaturesDialogHeader()
                FeaturesDialogContent(enableGenerateButton)
                FeaturesDialogFooter(displayDialogState, enableGenerateButton)
            }
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
    fun TopBar()
    {
        val color = Color(getColor(R.color.primary_orange))

        val showSaveDialog = remember { mutableStateOf(false) }

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
                    text = "Generate Outfit",
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                )
            },

            actions = {
                IconButton(
                    onClick = {
                        showSaveDialog.value = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Icon",
                        tint = color
                    )
                }
            },

            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = Color.Transparent
            )
        )

        if (showSaveDialog.value) SaveDialog(showSaveDialog = showSaveDialog)

    }

    @Composable
    fun SaveDialogHeader()
    {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.15f)
        ) {
            Text(
                text = "Save Outfit",
                color = Color(getColor(R.color.primary_orange)),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    @Composable
    fun SaveDialogContent(saveButtonState: MutableState<Boolean>)
    {
        val outfitNameState = remember{ mutableStateOf(outfit.name) }
        val outfitTypeState = remember{ mutableStateOf(outfit.type) }
        val outfitSeasonState = remember{ mutableStateOf(outfit.season) }
        val outfitOccasionState = remember{ mutableStateOf(outfit.occasion) }
        val outfitAgeState = remember{ mutableStateOf(outfit.age) }
        val outfitGenderState = remember{ mutableStateOf(outfit.gender) }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
        ) {
            FeaturesDialogInput(fieldValue = outfitNameState, text = "Outfit Name:")
            FeaturesDialogInput(fieldValue = outfitTypeState, text = "Outfit Type:", readOnly = true)
            FeaturesDialogInput(fieldValue = outfitSeasonState, text = "Outfit Season:", readOnly = true)
            FeaturesDialogInput(fieldValue = outfitOccasionState, text = "Outfit Occasion:", readOnly = true)
            FeaturesDialogInput(fieldValue = outfitAgeState, text = "Outfit For:", readOnly = true)
            FeaturesDialogInput(fieldValue = outfitGenderState, text = "Outfit Gender:", readOnly = true)
        }

        outfit.name = outfitNameState.value

        saveButtonState.value = outfit.name.isNotEmpty()
    }

    @Composable
    fun SaveDialogFooter(displayDialogState: MutableState<Boolean>, saveButtonState: MutableState<Boolean>)
    {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxSize()
        ){
            DialogButton(
                onClickEvent = {
                    displayDialogState.value = false
                },
                text = "Cancel",
                icon = Icons.Filled.Cancel
            )

            DialogButton(
                onClickEvent = {
                    val firebaseController = FirebaseController(baseContext)

                    firebaseController.addOutfitToFirebase(Firebase.auth.currentUser, outfit)

                    finish()
                },
                enabled = saveButtonState.value,
                text = "Save",
                icon = Icons.Filled.CheckCircle,
            )
        }
    }

    @Composable
    fun SaveDialog(showSaveDialog: MutableState<Boolean>)
    {
        val enableSaveButton = remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { showSaveDialog.value = false }) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                SaveDialogHeader()
                SaveDialogContent(enableSaveButton)
                SaveDialogFooter(showSaveDialog, enableSaveButton)
            }
        }
    }

    @Composable
    fun GenerateOutfitButton()
    {
        val displayFeaturesDialog = remember{ mutableStateOf(false) }

        FloatingActionButton(
            containerColor = Color(getColor(R.color.primary_orange)),
            onClick = {
                displayFeaturesDialog.value = true
            }
        ) {
            Icon(
                Icons.Filled.Replay,
                contentDescription = "Icon",
                tint = Color.White
            )
        }

        if (displayFeaturesDialog.value) ChooseOutfitFeaturesDialog(displayDialogState = displayFeaturesDialog)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Screen()
    {
        Scaffold(
            topBar = { TopBar() },
            containerColor = Color.Transparent,
            floatingActionButton = { GenerateOutfitButton() },
            modifier = Modifier
                .fillMaxSize()
        ) { paddingValues ->
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                if (outfitItems.value is LongOutfit)
                    LongOutfitScaffoldBody()
                else if(outfitItems.value is ShortOutfit)
                    ShortOutfitScaffoldBody()
            }
        }
    }

    @Composable
    fun DisplayItem(item: FirebaseClothingItem?, sizes: Pair<Dp, Dp>) {
        AsyncImage(
            model = item?.clothingItemData?.uri,
            contentDescription = item?.clothingItemData?.name,
            modifier = Modifier
                .width(sizes.first)
                .height(sizes.second)
                .clickable {
                    val intent = Intent(baseContext, ClothesInfoActivity::class.java)
                    intent.putExtra("item_info", item)
                    startActivity(intent)
                }
        )
    }

    @Composable
    fun ShortOutfitScaffoldBody()
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .wrapContentSize()
        ) {
            val shortOutfit = outfitItems.value as ShortOutfit

            if(shortOutfit.headItem != null) DisplayItem(shortOutfit.headItem, headItemSizes)

            DisplayItem(shortOutfit.upperBodyItem, upperBodyItemSizes)

            DisplayItem(shortOutfit.lowerBodyItem, lowerBodyItemSizes)

            DisplayItem(shortOutfit.feetItem, feetItemSizes)
        }
    }

    @Composable
    fun LongOutfitScaffoldBody()
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .wrapContentSize()
        ) {
            val longOutfit = outfitItems.value as LongOutfit

            if(longOutfit.headItem != null) DisplayItem(longOutfit.headItem, headItemSizes)

            DisplayItem(longOutfit.longItem, longItemSizes)

            DisplayItem(longOutfit.feetItem, feetItemSizes)
        }
    }

    enum class OutfitType(
        val type: String
    ){
        LONG("Long"),
        SHORT("Short")
    }

}