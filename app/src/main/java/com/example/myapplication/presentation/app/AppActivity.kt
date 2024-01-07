package com.example.myapplication.presentation.app

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Man
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.Camera
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.data.firebase.FirebaseClothingItemModel
import com.example.myapplication.data.other.Prediction
import com.example.myapplication.database.FirebaseController
import com.example.myapplication.ml.Detect
import com.example.myapplication.navigation.Navigation
import com.example.myapplication.navigation.Screen
import com.example.myapplication.presentation.screens.clothesSearchCriteria
import com.example.myapplication.presentation.screens.displaySearchedImages
import com.example.myapplication.presentation.screens.displaySearchedOutfits
import com.example.myapplication.presentation.screens.outfitsSearchCriteria
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp

class AppActivity: ComponentActivity() {
    companion object{
        val REQUEST_IMAGE_CAPTURE = 102
        val CLOTHES_SPEECH_REQUEST_CODE = 103
        val OUTFITS_SPEECH_REQUEST_CODE = 104
    }

    private var clothesButtonState = mutableStateOf(false)
    private var outfitsButtonState = mutableStateOf(true)
    private var contactUsButtonState = mutableStateOf(true)
    private var helpButtonState = mutableStateOf(true)

    private val auth: FirebaseAuth = Firebase.auth

    private lateinit var navController: NavController
    private lateinit var currentContext: Context

    @OptIn(ExperimentalMaterial3Api::class)
    private var drawerState = mutableStateOf(DrawerState(DrawerValue.Closed))

    private val imageSize = 320

    private var displayDialog = mutableStateOf(false)
    private var enableSaveButton = mutableStateOf(false)

    private var displayData: Prediction? = null

    // Item features states
    private val nameState = mutableStateOf("")
    private val typeState = mutableStateOf("")
    private val colorState =  mutableStateOf("")
    private val ageState = mutableStateOf("")
    private val sizeState = mutableStateOf("")
    private val materialState = mutableStateOf("")
    private val genderState = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            if (Firebase.auth.currentUser != null) {

                if (displayDialog.value) {
                    ImageInfoEditDialog()
                }

                currentContext = LocalContext.current
                navController = rememberNavController()

                AppScreen()
            }
            else{
                finish()
            }
        }
    }

    @Composable
    fun DialogHeader()
    {
        Row (
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.1f)
                .background(Color(getColor(R.color.primary_orange)))
        ){
            Text(
                text = "Clothing Item Features",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DialogField(fieldValue: MutableState<String>, readOnly: Boolean)
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
    fun DialogInput(fieldValue: MutableState<String>, text: String, readOnly: Boolean)
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
            DialogField(fieldValue = fieldValue, readOnly = readOnly )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SelectionBox(fieldValue: MutableState<String>, selectionItems: List<String>) {
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
    fun DialogSelection(fieldValue: MutableState<String>, text: String, selectionItems: List<String>)
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
            SelectionBox(fieldValue, selectionItems)
        }
    }

    @Composable
    fun DialogFieldsSection()
    {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .verticalScroll(rememberScrollState())
        ) {
            typeState.value = displayData?.type.toString()

            Image(
                displayData?.bitmap?.asImageBitmap()!!, "Image",
                modifier = Modifier
                    .fillMaxSize(0.4f)
                    .padding(0.dp, 5.dp)
            )

            DialogInput(fieldValue = nameState, text = "Item Name:", readOnly = false)
            DialogInput(fieldValue = typeState, text = "Item Type:", readOnly = true)
            DialogSelection(fieldValue = colorState, text = "Item Color:", selectionItems = ClothingItemColor.values().map { it.color })
            DialogSelection(fieldValue = materialState, text = "Item Material:", selectionItems = ClothingItemMaterial.values().map { it.material })

            when(typeState.value){
                "Shoes" -> DialogSelection(fieldValue = sizeState, text = "Item Size:", listOf("36","37","38","39","40","41","42","43","44","45","46"))
                else -> DialogSelection(fieldValue = sizeState, text = "Item Size:", listOf("XXS","XS","S","M","L","XL","XXL"))
            }

            DialogSelection(fieldValue = ageState, text = "Item For:", listOf("Adults","Children"))
            DialogSelection(fieldValue = genderState, text = "Item Gender:", listOf("Male","Female","Unisex"))

            if (checkStates(listOf(nameState,typeState,colorState,ageState,sizeState,materialState,genderState))){
                enableSaveButton.value = true
            }
        }
    }

    private fun checkStates(statesList: List<MutableState<String>>) : Boolean
    {
        for (item in statesList){
            if (item.value.isEmpty()){
                return false
            }
        }
        return true
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
    fun DialogFooter()
    {
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxSize()
        ){
            DialogButton(
                onClickEvent = {
                    displayDialog.value = false
                    displayData = null
                },
                text = "Cancel",
                icon = Icons.Filled.Cancel
            )

            DialogButton(
                onClickEvent = {
                    val firebaseController = FirebaseController(currentContext)
                    firebaseController.addClothesImageToFirebase(
                        auth.currentUser!!,
                        FirebaseClothingItemModel(
                            name = nameState.value,
                            type = typeState.value,
                            color = colorState.value,
                            age = ageState.value,
                            size = sizeState.value,
                            material = materialState.value,
                            gender = genderState.value
                        ),
                        displayData?.bitmap!!
                    )

                    nameState.value = ""
                    typeState.value = ""
                    colorState.value = ""
                    ageState.value = ""
                    sizeState.value = ""
                    materialState.value = ""
                    genderState.value = ""

                    displayDialog.value = false
                    displayData = null

                    enableSaveButton.value = false

                    navController.navigate(Screen.ClothesScreen.route)
                },
                text = "Save",
                icon = Icons.Filled.CheckCircle,
                enabled = enableSaveButton.value
            )
        }
    }

    @Composable
    fun ImageInfoEditDialog()
    {
        if (displayData != null) {
            Dialog(
                onDismissRequest = {
                    
                }
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(600.dp)
                        .padding(8.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    DialogHeader()
                    DialogFieldsSection()
                    DialogFooter()
                }
            }
        }
    }

    private fun processImage(data: Intent?)
    {
        val bitmap = data?.extras?.get("data") as Bitmap
        val detection = objectDetection(bitmap)

        if (detection.accuracy > 90) {
            displayDialog.value = true
            displayData = detection
        } else {
            Toast.makeText(
                currentContext,
                "Couldn't classify image!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun processClothesSpeechText(data: Intent?)
    {
        val spokenText: String? =
            data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let { results ->
                results?.get(0)
            }

        if (spokenText != null) {
            clothesSearchCriteria.value = spokenText
            displaySearchedImages()
        }
    }

    private fun processOutfitsSpeechText(data: Intent?)
    {
        val spokenText: String? =
            data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let { results ->
                results?.get(0)
            }

        if (spokenText != null) {
            outfitsSearchCriteria.value = spokenText
            displaySearchedOutfits()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> if (resultCode == RESULT_OK) {
                processImage(data)
            }
            CLOTHES_SPEECH_REQUEST_CODE -> if (resultCode == RESULT_OK){
                processClothesSpeechText(data)
            }
            OUTFITS_SPEECH_REQUEST_CODE -> if (resultCode == RESULT_OK){
                processOutfitsSpeechText(data)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun objectDetection(image: Bitmap): Prediction
    {
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(imageSize, imageSize, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(NormalizeOp(floatArrayOf(0f), floatArrayOf(255f)))
            .add(CastOp(DataType.FLOAT32))
            .build()

        val model = Detect.newInstance(currentContext)

        var tfImageBuffer = TensorImage(DataType.FLOAT32)
        tfImageBuffer.load(image)
        tfImageBuffer = imageProcessor.process(tfImageBuffer)

        // Runs model inference and gets result.
        val outputs = model.process(tfImageBuffer.tensorBuffer)
        // Predictions
        val feature0 = outputs.outputFeature0AsTensorBuffer.floatArray
        // Positions
        val feature1 = outputs.outputFeature1AsTensorBuffer.floatArray
        // Number of predictions
        val feature2 = outputs.outputFeature2AsTensorBuffer.floatArray
        // Labels
        val feature3 = outputs.outputFeature3AsTensorBuffer.floatArray

        val labels = FileUtil.loadLabels(currentContext, "labels.txt")

        val prediction = Prediction(labels[feature3[0].toInt()], feature0[0]*100, image)

        model.close()

        return prediction
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppScreen() {
        Scaffold(
            topBar = { TransparentTopBar() },
            bottomBar = { WhiteBottomBar() },
        ) { paddingValues ->
            Background(paddingValues = paddingValues)
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                NavigationAndDrawer()
            }
        }
    }

    @Composable
    fun Background(paddingValues: PaddingValues) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            translate(left = -100f, top = -700f) {
                drawCircle(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(currentContext.getColor(R.color.secondary_orange)),
                            Color(currentContext.getColor(R.color.primary_orange)),
                        )
                    ),
                    radius = 360.dp.toPx()
                )
            }
        }
    }

    @Composable
    fun WhiteBottomBar() {
        BottomAppBar(
            containerColor = Color.White,
            actions = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomMenuButton(
                        navigationRoute = {
                            navController.navigate(Screen.ClothesScreen.route)
                        },
                        buttonText = "Clothes",
                        buttonIcon = Icons.Filled.PhotoLibrary,
                        clothesButtonState
                    )
                    BottomMenuButton(
                        navigationRoute = {
                            navController.navigate(Screen.OutfitsScreen.route)
                        },
                        buttonText = "Outfits",
                        buttonIcon = Icons.Filled.Man,
                        outfitsButtonState
                    )
                    CameraButton()
                    BottomMenuButton(
                        navigationRoute = {
                            navController.navigate(Screen.ContactUsScreen.route)
                        },
                        buttonText = "Contact",
                        buttonIcon = Icons.Filled.Phone,
                        contactUsButtonState
                    )
                    BottomMenuButton(
                        navigationRoute = {
                            navController.navigate(Screen.HelpScreen.route)
                        },
                        buttonText = "Help",
                        buttonIcon = Icons.Filled.Help,
                        helpButtonState
                    )
                }
            }
        )
    }

    @Composable
    fun BottomMenuButton(navigationRoute: () -> Unit, buttonText: String, buttonIcon: ImageVector, buttonState: MutableState<Boolean>)
    {
        IconButton(
            enabled = buttonState.value,
            onClick = navigationRoute,
            modifier = Modifier
                .size(75.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (!buttonState.value)
                            Brush.verticalGradient(
                                listOf(
                                    Color(getColor(R.color.secondary_orange)),
                                    Color.White
                                )
                            )
                        else
                            Brush.linearGradient(listOf(Color.White, Color.White))
                    )
            ){
                Icon(
                    imageVector = buttonIcon,
                    contentDescription = "Icon",
                    modifier = Modifier.size(30.dp),
                    tint = Color(getColor(R.color.primary_orange))
                )
                Spacer(modifier = Modifier.fillMaxHeight(0.1f))
                Text(
                    text = buttonText,
                    color = Color(getColor(R.color.primary_orange)),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    @Composable
    fun CameraButton()
    {
        FloatingActionButton(
            shape = CircleShape,
            onClick = {
                startCameraIntent()
            },
            containerColor = Color(getColor(R.color.primary_orange)),
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(
                5.dp
            )
        ) {
            Icon(
                Icons.Rounded.Camera,
                "Localized description",
                modifier = Modifier.size(70.dp),
                tint = Color.White
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TransparentTopBar() {
        TopAppBar(
            actions = {
                IconButton(
                    onClick = {
                        if (drawerState.value.isOpen)
                            drawerState.value = DrawerState(DrawerValue.Closed)
                        else
                            drawerState.value = DrawerState(DrawerValue.Open)
                    }
                ) {
                    Icon(
                        modifier = Modifier.size(40.dp),
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Localized description",
                        tint = Color.White
                    )
                }
            },
            title = {
                Text(
                    text = getString(R.string.app_name),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp
                )
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier.shadow(70.dp)
        )

    }

    @Composable
    fun DrawerHeaderSection()
    {
        val color = getColor(R.color.primary_orange)
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.20f)
                .background(Color.White)
        ){
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .background(Color(color))
            ){
                Text(
                    text = "Account",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.95f)
                    .background(Color(0x1feb971c))
            ){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = Firebase.auth.currentUser?.displayName.toString(),
                        color = Color(color),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = auth.currentUser?.email.toString(),
                        color = Color(color),
                        fontSize = 12.sp,
                    )
                }
                Icon(
                    Icons.Rounded.AccountBox,
                    "Icon",
                    tint = Color(color),
                    modifier = Modifier
                        .size(50.dp)
                    )
            }
            Divider(
                thickness = 2.dp,
                color = Color(color),
                modifier = Modifier.fillMaxWidth(0.9f)
            )
        }
    }

    @Composable
    fun DrawerButton(buttonIcon: ImageVector, buttonText: String, buttonAction: () -> Unit)
    {
        IconButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color.White),
            onClick = buttonAction
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ){
                Spacer(modifier = Modifier.fillMaxWidth(0.02f))
                Icon(
                    buttonIcon,
                    "Icon",
                    modifier = Modifier.size(35.dp),
                    tint = Color(getColor(R.color.primary_orange))
                )
                Text(
                    text = buttonText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(getColor(R.color.primary_orange)),
                    modifier = Modifier.fillMaxWidth(0.8f),
                )
            }
        }
    }

    @Composable
    fun DrawerButtonSection(paddingValues: PaddingValues) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                Spacer(modifier = Modifier.fillMaxHeight(0.025f))
                DrawerButton(
                    buttonIcon = Icons.Filled.ManageAccounts,
                    buttonText = "Account Settings",
                    buttonAction = {
                        val intent = Intent(baseContext, AccountSettingsActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }

    @Composable
    fun DrawerFooterSection()
    {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxHeight(0.15f)
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                Divider(
                    thickness = 2.dp,
                    color = Color(getColor(R.color.primary_orange)),
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
                DrawerButton(
                    buttonIcon = Icons.Filled.ExitToApp,
                    buttonText = "Log Out",
                    buttonAction = {
                        if (auth.currentUser != null) {
                            auth.signOut()


                            currentContext.startActivity(
                                Intent(
                                    currentContext,
                                    MainActivity::class.java
                                ).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                            )

                            finish()
                        }
                    }
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun NavigationAndDrawer() {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ModalNavigationDrawer(
                drawerState = drawerState.value,
                drawerContent = {
                    ModalDrawerSheet(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .fillMaxHeight()
                    ) {
                        Scaffold(
                            topBar = {
                                DrawerHeaderSection()
                            },
                            bottomBar = {
                                DrawerFooterSection()
                            }
                        ) { paddingValues ->
                            DrawerButtonSection(paddingValues)
                        }
                    }
                },
                gesturesEnabled = true
            ) {
                val statesList = listOf(
                    clothesButtonState,outfitsButtonState,contactUsButtonState,helpButtonState
                )

                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Navigation(navController, currentContext, statesList)
                }
            }
        }
    }

    private fun startCameraIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(currentContext, "Couldn't start the camera!", Toast.LENGTH_LONG).show()
        }
    }

    enum class ClothingItemColor(
        val color: String, val index: Int
    ){
        BLACK("Black", 0),
        WHITE("White", 1),
        GRAY("Gray", 2),
        BEIGE("Beige", 3),
        RED("Red", 4),
        GREEN("Green", 5),
        BLUE("Blue", 6),
        YELLOW("Yellow", 7),
        ORANGE("Orange", 8),
        PINK("Pink", 9),
        PURPLE("Purple", 10),
        BROWN("Brown", 11),
        TEAL("Teal", 12),
        MAROON("Maroon", 13)
    }

    enum class ClothingItemMaterial(
        val material: String, val index: Int
    ){
        COTTON("Cotton", 0),
        POLYESTER("Polyester", 1),
        WOOL("Wool", 2),
        NYLON("Nylon", 3),
        SPANDEX("Spandex", 4),
        RAYON("Rayon", 5),
        LEATHER("Leather",6),
        SYN_LEATHER("Synthetic Leather", 7),
        DENIM("Denim", 8),
        SILK("Silk", 9),
        ACRYLIC("Acrylic", 10),
        CANVAS("Canvas", 11),
        RUBBER("Rubber", 12),
        SUEDE("Suede", 13)
    }
}
