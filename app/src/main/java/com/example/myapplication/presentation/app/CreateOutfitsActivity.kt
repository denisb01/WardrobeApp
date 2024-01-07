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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.data.firebase.FirebaseClothingItem
import com.example.myapplication.data.firebase.FirebaseOutfitModel
import com.example.myapplication.database.FirebaseController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class CreateOutfitsActivity: ComponentActivity() {
    companion object{
        val CHOOSE_HEAD_ITEM = 200
        val CHOOSE_UPPER_BODY_ITEM = 201
        val CHOOSE_LONG_ITEM = 202
        val CHOOSE_LOWER_BODY_ITEM = 203
        val CHOOSE_FEET_ITEM = 204
    }

    private val enableSaveButton = mutableStateOf(false)

    private var longOutfitChosen = false

    private val headItem: MutableState<FirebaseClothingItem?> = mutableStateOf(null)
    private val upperBodyItem: MutableState<FirebaseClothingItem?> = mutableStateOf(null)
    private val longItem: MutableState<FirebaseClothingItem?> = mutableStateOf(null)
    private val lowerBodyItem: MutableState<FirebaseClothingItem?> = mutableStateOf(null)
    private val feetItem: MutableState<FirebaseClothingItem?> = mutableStateOf(null)

    private val headItemSizes = Pair(100.dp, 100.dp)
    private val upperBodyItemSizes = Pair(120.dp, 180.dp)
    private val longItemSizes = Pair(160.dp, 300.dp)
    private val lowerBodyItemSizes = Pair(120.dp, 180.dp)
    private val feetItemSizes = Pair(120.dp, 120.dp)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val showDialog = remember {mutableStateOf(true)}

            Background()

            if (showDialog.value) ChooseOutfitTypeDialog(showDialog)
            else Screen()
        }
    }

    @Composable
    fun DialogTitle()
    {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
        ) {
            Text(
                text = "Outfit Type",
                color = Color(getColor(R.color.primary_orange)),
                fontSize = 28.sp,
                fontWeight =  FontWeight.Bold
            )
        }
    }

    @Composable
    fun DialogContent()
    {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
        ) {
            Text(
                text = "For one-piece outfit like a Dress, select Long.\n\n" +
                        "For two-piece outfit like Pants and Top, select Short.",
                color = Color(getColor(R.color.primary_orange)),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
            )
        }
    }

    @Composable
    fun TypeButton(text: String, action: () -> Unit)
    {
        Button(
            onClick = action,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(getColor(R.color.primary_orange))
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
    fun DialogButtons(showDialog: MutableState<Boolean>)
    {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            TypeButton(
                text = "Long",
                action = {
                    showDialog.value = false
                    longOutfitChosen = true
                }
            )

            TypeButton(
                text = "Short",
                action = {
                    showDialog.value = false
                    longOutfitChosen = false
                }
            )
        }
    }

    @Composable
    fun ChooseOutfitTypeDialog(showDialog: MutableState<Boolean>)
    {
        Dialog(
            onDismissRequest = {
                finish()
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
                    .height(320.dp)
            ){
                DialogTitle()
                DialogContent()
                DialogButtons(showDialog)
            }
        }
    }

    private fun checkIfValidOutfit()
    {
        if (longOutfitChosen)
            enableSaveButton.value = feetItem.value != null && longItem.value != null
        else
            enableSaveButton.value = feetItem.value != null && lowerBodyItem.value != null && upperBodyItem.value != null
    }

    private fun handleChosenItem(itemState: MutableState<FirebaseClothingItem?>, data: Intent?) {
        val chosenItem: FirebaseClothingItem?
        val extra = data?.extras?.get("item")

        chosenItem =
            if (extra != null)
                extra as FirebaseClothingItem
            else
                null

        itemState.value = chosenItem

        checkIfValidOutfit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            CHOOSE_HEAD_ITEM -> if (resultCode == RESULT_OK) { handleChosenItem(headItem ,data) }
            CHOOSE_UPPER_BODY_ITEM -> if (resultCode == RESULT_OK) { handleChosenItem(upperBodyItem ,data) }
            CHOOSE_LONG_ITEM -> if (resultCode == RESULT_OK) { handleChosenItem(longItem ,data) }
            CHOOSE_LOWER_BODY_ITEM -> if (resultCode == RESULT_OK) { handleChosenItem(lowerBodyItem ,data) }
            CHOOSE_FEET_ITEM -> if (resultCode == RESULT_OK) { handleChosenItem(feetItem ,data) }
        }
    }

    private fun chooseItem(requestCode: Int)
    {
        val composeIntent = Intent(this, ChooseClothesActivity::class.java)
        composeIntent.putExtra("request_code", requestCode)
        startActivityForResult(composeIntent, requestCode)
    }

    @Composable
    fun DisplayItem(itemState: MutableState<FirebaseClothingItem?>, requestCode: Int, sizes: Pair<Dp, Dp>) {
        AsyncImage(
            model = itemState.value?.clothingItemData?.uri,
            contentDescription = itemState.value?.clothingItemData?.name,
            modifier = Modifier
                .width(sizes.first)
                .height(sizes.second)
                .clickable { chooseItem(requestCode) }
        )
    }

    @Composable
    fun AddItemButton(requestCode: Int, sizes: Pair<Dp, Dp>)
    {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Icon",
            tint = Color(getColor(R.color.primary_orange)),
            modifier = Modifier
                .width(sizes.first)
                .height(sizes.second)
                .background(Color.LightGray)
                .clickable { chooseItem(requestCode) }
        )
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
                text = "Add Outfit Features",
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

                if(fieldValue.value.isEmpty()){
                    enableSaveButton.value = false
                }
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
        saveButtonState.value = outfit.name.isNotEmpty() &&
                                outfit.season.isNotEmpty() &&
                                outfit.occasion.isNotEmpty() &&
                                outfit.age.isNotEmpty() &&
                                outfit.gender.isNotEmpty()
    }

    @Composable
    fun FeaturesDialogContent(outfit: FirebaseOutfitModel, saveButtonState: MutableState<Boolean>)
    {
        val outfitNameState = remember{ mutableStateOf(outfit.name) }
        val outfitTypeState = remember{ mutableStateOf(outfit.type) }
        val outfitSeasonState = remember{ mutableStateOf(outfit.season) }
        val outfitOccasionState = remember{ mutableStateOf(outfit.occasion) }
        val outfitAgeState = remember{ mutableStateOf(outfit.age) }
        val outfitGenderState = remember{ mutableStateOf(outfit.gender) }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
        ) {
            FeaturesDialogInput(fieldValue = outfitNameState, text = "Outfit Name:")
            FeaturesDialogInput(fieldValue = outfitTypeState, text = "Outfit Type:", readOnly = true)
            FeaturesDialogSelection(fieldValue = outfitSeasonState, text = "Outfit Season:",
                selectionItems = OutfitSeason.values().map { it.season })
            FeaturesDialogSelection(fieldValue = outfitOccasionState, text = "Outfit Occasion:",
                selectionItems = OutfitOccasions.values().map { it.occasion })
            FeaturesDialogSelection(fieldValue = outfitAgeState, text = "Outfit For:",
                selectionItems = OutfitAge.values().map { it.age })
            FeaturesDialogSelection(fieldValue = outfitGenderState, text = "Outfit Gender:",
                selectionItems = OutfitGender.values().map { it.gender })
        }

        outfit.name = outfitNameState.value
        outfit.season = outfitSeasonState.value
        outfit.occasion = outfitOccasionState.value
        outfit.age = outfitAgeState.value
        outfit.gender = outfitGenderState.value

        checkOutfitFeatures(outfit, saveButtonState)
    }

    @Composable
    fun FeaturesDialogFooter(displayDialogState: MutableState<Boolean>, outfit: FirebaseOutfitModel, saveButtonState: MutableState<Boolean>)
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

    private fun assembleOutfit(): FirebaseOutfitModel
    {
        val outfit = FirebaseOutfitModel(
            headItemID = if(headItem.value != null) headItem.value?.key else null,
            feetItemID = feetItem.value?.key
        )

        if(longOutfitChosen) {
            outfit.type = "Long"
            outfit.longItemID = longItem.value?.key
        }
        else{
            outfit.type = "Short"
            outfit.upperBodyItemID = upperBodyItem.value?.key
            outfit.lowerBodyItemID = lowerBodyItem.value?.key
        }

        return outfit
    }

    @Composable
    fun FeaturesDialog(displayDialogState: MutableState<Boolean>)
    {
        val outfit = assembleOutfit()
        val saveButtonState = remember{ mutableStateOf(false) }

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
                FeaturesDialogContent(outfit, saveButtonState)
                FeaturesDialogFooter(displayDialogState, outfit, saveButtonState)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopBar()
    {
        val color = Color(getColor(R.color.primary_orange))

        val disabledColor = Color(getColor(R.color.secondary_orange))

        val displayFeaturesDialog = remember{ mutableStateOf(false) }

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
                    text = "Create Outfit",
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                )
            },
            actions = {
                IconButton(
                    onClick = { displayFeaturesDialog.value = true },
                    enabled = enableSaveButton.value
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Icon",
                        tint = if(enableSaveButton.value) color else disabledColor
                    )
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = Color.Transparent
            )
        )

        if (displayFeaturesDialog.value) FeaturesDialog(displayFeaturesDialog)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Screen()
    {
        Scaffold(
            topBar = { TopBar() },
            containerColor = Color.Transparent,
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
                if (longOutfitChosen) LongOutfitScaffoldBody()
                else MultipleItemsOutfitScaffoldBody()
            }
        }
    }

    @Composable
    fun MultipleItemsOutfitScaffoldBody()
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .wrapContentSize()
        ) {
            if(headItem.value == null) AddItemButton(CHOOSE_HEAD_ITEM, headItemSizes)
            else DisplayItem(headItem, CHOOSE_HEAD_ITEM, headItemSizes)

            if(upperBodyItem.value == null) AddItemButton(CHOOSE_UPPER_BODY_ITEM, upperBodyItemSizes)
            else DisplayItem(upperBodyItem, CHOOSE_UPPER_BODY_ITEM, upperBodyItemSizes)

            if(lowerBodyItem.value == null) AddItemButton(CHOOSE_LOWER_BODY_ITEM, lowerBodyItemSizes)
            else DisplayItem(lowerBodyItem, CHOOSE_LOWER_BODY_ITEM, lowerBodyItemSizes)

            if(feetItem.value == null) AddItemButton(CHOOSE_FEET_ITEM, feetItemSizes)
            else DisplayItem(feetItem, CHOOSE_FEET_ITEM, feetItemSizes)
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
            if(headItem.value == null) AddItemButton(CHOOSE_HEAD_ITEM, headItemSizes)
            else DisplayItem(headItem, CHOOSE_HEAD_ITEM, headItemSizes)

            if(longItem.value == null) AddItemButton(CHOOSE_LONG_ITEM, longItemSizes)
            else DisplayItem(longItem, CHOOSE_LONG_ITEM, longItemSizes)

            if(feetItem.value == null) AddItemButton(CHOOSE_FEET_ITEM, feetItemSizes)
            else DisplayItem(feetItem, CHOOSE_FEET_ITEM, feetItemSizes)
        }
    }

    enum class OutfitSeason(
        val season: String
    ){
        SPRING("Spring"),
        SUMMER("Summer"),
        AUTUMN("Autumn"),
        WINTER("Winter")
    }

    enum class OutfitOccasions(
        val occasion: String
    ){
        FORMAL("Formal"),
        CASUAL("Casual"),
        HOUSE("House"),
        PROFESSIONAL("Professional")
    }

    enum class OutfitAge(
        val age: String
    ){
        ADULT("Adult"),
        CHILD("Child")
    }

    enum class OutfitGender(
        val gender: String
    ){
        MALE("Male"),
        FEMALE("Female")
    }
}