package com.example.incidentapplication.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.incidentapplication.MainViewModel
import com.example.incidentapplication.R
import com.example.incidentapplication.Request
import com.google.firebase.auth.FirebaseAuth
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Main(navController: NavHostController, auth: FirebaseAuth) {
    val viewModel = MainViewModel()
    val requests = viewModel.request.observeAsState()
    GlobalScope.launch(Dispatchers.IO) {
        viewModel.getDataFromDb()
    }
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color(0x2BC5F8FF)
    ) {
        Box(modifier = Modifier.padding(top = 16.dp)) {
            requests.value?.let { RequestList(requests = it, viewModel) }
        }
        BottomNav(navController)
    }

}

@Composable
fun BottomNav(navController: NavController) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom
    ) {
        Button(
            onClick = {
                navController.navigate("Main")
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0x806992FF)),
            border = BorderStroke(color = Color.Transparent, width = 0.dp),
            elevation = ButtonDefaults.elevation(0.dp)
        ) {
            Image(painter = painterResource(id = R.drawable.ic_home), contentDescription = null)
        }
        Button(
            onClick = { navController.navigate("CreateScreen") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            border = BorderStroke(color = Color.Transparent, width = 0.dp),
            elevation = ButtonDefaults.elevation(0.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0x806992FF))
        ) {
            Image(painter = painterResource(id = R.drawable.ic_create), contentDescription = null)
        }

        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), border = BorderStroke(color = Color.Transparent, width = 0.dp),
            elevation = ButtonDefaults.elevation(0.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0x806992FF))
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_checklist),
                contentDescription = null
            )
        }


    }
}

@Composable
fun RequestList(requests: List<Request>, viewModel: MainViewModel) {
    Box(modifier = Modifier.padding(top = 18.dp))
    LazyColumn {
        items(requests) { request ->
            RequestItem(request = request, viewModel)
        }
    }
}


@OptIn(DelicateCoroutinesApi::class)
@SuppressLint("CoroutineCreationDuringComposition")

@Composable
fun RequestItem(request: Request, viewModel: MainViewModel) {
    var isConvert by remember {
        mutableStateOf(true)
    }

    GlobalScope.launch(Dispatchers.IO) {
        viewModel.getImageUrlFromStorage(request.image)
    }

    Card(
        elevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 18.dp, start = 16.dp, end = 16.dp)
            .clickable {
                isConvert = !isConvert
            },
        backgroundColor = Color(0x66C5E4FF),
        border = BorderStroke(color = Color.Transparent, width = 0.dp)

    ) {
        Column(Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
            ) {
                Text(
                    text = "Topic: ${request.topic}", fontSize = 18.sp,
                    color = Color.DarkGray
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    text = "Description: ${request.description}",
                    color = Color(0xFF828583),
                    fontSize = 12.sp,
                    maxLines = if (isConvert) 1 else 20,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = "Status: Ready",
                    color = Color(0xFF828583),
                    fontSize = 12.sp,
                    maxLines = if (isConvert) 1 else 20,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(horizontalArrangement = Arrangement.Center) {
                if (!isConvert) GlideImage(
                    imageModel = request.image,
                    modifier = Modifier.fillMaxSize(),
                    alignment = Alignment.Center
                )
            }
            Row(
                Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = if (isConvert) R.drawable.ic_arrow_down else R.drawable.ic_arrow_up),
                    contentDescription = null,
                    modifier = Modifier
                        .clickable {
                            isConvert = !isConvert
                        }, alignment = Alignment.BottomCenter
                )
            }
        }

    }
}
