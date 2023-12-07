package com.example.myapplication.presentation.app

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Man
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.Camera
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.data.Prediction
import com.example.myapplication.database.FirebaseController
import com.example.myapplication.ml.Detect
import com.example.myapplication.navigation.Navigation
import com.example.myapplication.navigation.Screen
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
import java.nio.ByteBuffer
import java.nio.ByteOrder

class AppActivity: ComponentActivity() {
    private var clothesButtonState = mutableStateOf(false)
    private var outfitsButtonState = mutableStateOf(true)
    private var settingsButtonState = mutableStateOf(true)
    private var helpButtonState = mutableStateOf(true)

    private val auth: FirebaseAuth = Firebase.auth

    private val REQUEST_IMAGE_CAPTURE = 102
    private lateinit var navController: NavController
    private lateinit var currentContext: Context

    @OptIn(ExperimentalMaterial3Api::class)
    private var drawerState = mutableStateOf(DrawerState(DrawerValue.Closed))

    private val imageSize = 320

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            currentContext = LocalContext.current
            navController = rememberNavController()

            AppScreen()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            val bitmap = data?.extras?.get("data") as Bitmap

            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> if (resultCode == RESULT_OK) {
                    val firebaseController = FirebaseController(currentContext)

                    val detection = objectDetection(bitmap)

                    if (detection.accuracy > 90) {
                        firebaseController.addClothesImageToFirebase(
                            auth.currentUser!!,
                            bitmap,
                            detection
                        )
                        navController.navigate(Screen.ClothesScreen.route)
                    } else {
                        Toast.makeText(
                            currentContext,
                            "Couldn't classify image!",
                            Toast.LENGTH_LONG
                        )
                    }
                }

                else -> super.onActivityResult(requestCode, resultCode, data)
            }
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

        Toast.makeText(currentContext, labels[feature3[0].toInt()] + " : " + (feature0[0]*100).toString(), Toast.LENGTH_SHORT).show()

        val prediction = Prediction(labels[feature3[0].toInt()], feature0[0]*100, feature1)

        Log.i("AIM","Detections")
        for (i in 0..9){
            Log.i("AIM",labels[feature3[i].toInt()] + " : " + (feature0[i]*100).toString())
        }

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

                        },
                        buttonText = "Settings",
                        buttonIcon = Icons.Filled.Settings,
                        settingsButtonState
                    )
                    BottomMenuButton(
                        navigationRoute = {

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
                        text = auth.currentUser?.displayName.toString(),
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

                    }
                )

                DrawerButton(
                    buttonIcon = Icons.Filled.HelpCenter,
                    buttonText = "Account Help",
                    buttonAction = {

                    }
                )

                DrawerButton(
                    buttonIcon = Icons.Filled.Info,
                    buttonText = "App Info",
                    buttonAction = {

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
                    clothesButtonState,outfitsButtonState,settingsButtonState,helpButtonState
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
}
