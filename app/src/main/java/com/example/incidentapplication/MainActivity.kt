package com.example.incidentapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.incidentapplication.screens.*
import com.example.incidentapplication.ui.theme.IncidentApplicationTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            auth = Firebase.auth
            IncidentApplicationTheme {
                val currentUser = auth.currentUser
                //если юзер существует, то зайдет сразу на главный экран, если нет на логин бросит
                val firstScreen: String = if (currentUser != null) {
                    if (currentUser.email != "admin@gmail.com") "Main"
                    else "MainAdminScreen"
                } else {
                    "Login"
                }

                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = firstScreen
                ) {
                    composable("Login") { Login(navController, auth) }
                    composable("SignUp") { SignUp(navController, auth) }
                    composable("Main") { Main(navController) }
                    composable("CreateScreen") { CreateScreen(navController, auth) }
                    composable("CompleteRequestScreen") { CompleteRequestScreen(navController = navController) }
                    composable("MainAdminScreen") { MainAdminScreen(navController = navController) }
                }
            }
        }
    }
}
