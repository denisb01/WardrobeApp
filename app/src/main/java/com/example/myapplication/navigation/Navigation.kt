package com.example.myapplication.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.database.FirebaseController
import com.example.myapplication.presentation.screens.ClothesScreen
import com.example.myapplication.presentation.screens.ContactUsScreen
import com.example.myapplication.presentation.screens.HelpScreen
import com.example.myapplication.presentation.screens.OutfitsScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

val auth: FirebaseAuth = Firebase.auth
lateinit var firebaseController: FirebaseController
private fun navigateToClothes(statesList: List<MutableState<Boolean>>){
    statesList[0].value = false
    statesList[1].value = true
    statesList[2].value = true
    statesList[3].value = true

}

private fun navigateToOutfits(statesList: List<MutableState<Boolean>>){
    statesList[0].value = true
    statesList[1].value = false
    statesList[2].value = true
    statesList[3].value = true
}

private fun navigateToContactUs(statesList: List<MutableState<Boolean>>){
    statesList[0].value = true
    statesList[1].value = true
    statesList[2].value = false
    statesList[3].value = true
}

private fun navigateToHelp(statesList: List<MutableState<Boolean>>){
    statesList[0].value = true
    statesList[1].value = true
    statesList[2].value = true
    statesList[3].value = false
}

@Composable
fun Navigation(navController: NavController, context: Context, statesList: List<MutableState<Boolean>>)
{
    firebaseController = FirebaseController(context)
    NavHost(navController = navController as NavHostController, startDestination = Screen.ClothesScreen.route) {
        composable(route = Screen.ClothesScreen.route){
            ClothesScreen(navController = navController, context = context)
            navigateToClothes(statesList)
        }
        composable(route = Screen.OutfitsScreen.route){
            OutfitsScreen(navController = navController, context = context)
            navigateToOutfits(statesList)
        }
        composable(route = Screen.ContactUsScreen.route){
            ContactUsScreen(navController = navController, context = context)
            navigateToContactUs(statesList)
        }
        composable(route = Screen.HelpScreen.route){
            HelpScreen(navController = navController, context = context)
            navigateToHelp(statesList)
        }
    }
}