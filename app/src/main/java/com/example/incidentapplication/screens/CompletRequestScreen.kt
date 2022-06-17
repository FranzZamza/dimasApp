package com.example.incidentapplication.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.incidentapplication.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun CompleteRequestScreen(navController: NavController) {
    val viewModel = MainViewModel()
    val requests = viewModel.listOfCompleteRequest.observeAsState()

    GlobalScope.launch(Dispatchers.IO) {
        viewModel.getCompleteRequests()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color(0x2BC5F8FF)
    ) {
        Box(modifier = Modifier.padding(top = 16.dp)) {
            requests.value?.let { RequestList(requests = it, viewModel) }
        }
        if (FirebaseAuth.getInstance().currentUser!!.email != "admin@gmail.com")
            BottomNav(navController)
        else AdminBottomNav(navController = navController)
    }
}