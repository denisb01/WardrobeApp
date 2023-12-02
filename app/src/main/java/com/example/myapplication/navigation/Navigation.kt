package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.presentation.screens.ClothesScreen
import com.example.myapplication.presentation.screens.OutfitsScreen

var globalNavController: NavController? = null

@Composable
fun Navigation(navController: NavController)
{
    globalNavController = navController

    NavHost(navController = navController as NavHostController, startDestination = Screen.ClothesScreen.route) {
        composable(route = Screen.ClothesScreen.route){
            ClothesScreen(navController = navController)
        }
        composable(route = Screen.OutfitsScreen.route){
            OutfitsScreen(navController = navController)
        }
    }
}