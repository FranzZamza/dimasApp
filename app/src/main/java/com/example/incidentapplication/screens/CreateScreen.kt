package com.example.incidentapplication.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.incidentapplication.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

@Composable
fun CreateScreen(navController: NavController, auth: FirebaseAuth) {

    val viewModel = MainViewModel()
    val context = LocalContext.current
    val imageData = remember { mutableStateOf(null as Uri?) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        imageData.value = it
    }
    val imageIncident = remember {
        mutableStateOf(com.example.incidentapplication.R.drawable.ic_baseline_image_search_24)
    }
    val textOfTopic = viewModel.topic.observeAsState(initial = "")
    val textOfDescription = viewModel.desc.observeAsState(initial = "")

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Регистрация инцидента",
            fontSize = 36.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
        OutlinedTextField(
            value = textOfTopic.value,
            onValueChange = { viewModel.setTopic(it) },
            label = { Text(text = "Тема") },
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(26.dp))

        OutlinedTextField(
            value = textOfDescription.value,
            onValueChange = { viewModel.setDesc(it) },
            label = { Text(text = "Описание") },
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(26.dp))
        Card(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .clickable {
                    launcher.launch("image/*")
                }
        ) {
            Box(
                modifier = Modifier.size(80.dp), Alignment.Center
            ) {
                Image(
                    painter = painterResource(imageIncident.value),
                    contentDescription = "",
                    alignment = Alignment.Center,
                )
            }
            UploadedImageView(imageData = imageData, context = context)
        }

        Spacer(modifier = Modifier.height(26.dp))
        Button(
            onClick = {
                val imageKey = Firebase.database.reference.push().key
                imageKey?.let {
                    addNewRequest(
                        auth,
                        textOfTopic.value,
                        textOfDescription.value,
                        imageData,
                        imageKey
                    )
                    viewModel.imageIdSet(imageKey)
                }
                navController.navigate("Main")
            }, modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(text = "Отправить")
        }
    }
}

@Composable
private fun UploadedImageView(
    imageData: MutableState<Uri?>,
    context: Context,
) {
    val bitmap = remember {
        mutableStateOf(null as Bitmap?)
    }
    val uri = imageData.value
    if (uri != null) {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        bitmap.value = ImageDecoder.decodeBitmap(source)
    }
    bitmap.value?.let { it1 ->
        Image(
            bitmap = it1.asImageBitmap(),
            contentDescription = ""
        )
    }
}

private fun saveToBd(auth: FirebaseAuth, imageData: MutableState<Uri?>, key: String) {
    val uri = imageData.value
    val storage = Firebase.storage
    val storageRef = storage.reference
    uri?.let {
        val ref = storageRef.child("${auth.uid}/$key")
        ref.putFile(uri).addOnSuccessListener {
            Log.d("Pic", "Success")
        }.addOnFailureListener {
            Log.d("Pic", "unSuccess")
        }
    }
}

fun addNewRequest(
    auth: FirebaseAuth,
    textOfTopic: String,
    textOfDescription: String,
    imageData: MutableState<Uri?>,
    imageKey: String
) {
    val user = auth.currentUser
    val database = Firebase.database
    val key = database.reference.push().key
    if (key != null) {
        database.getReference(user?.uid.toString()).child("request").child(key)
            .child("topic").setValue(textOfTopic)

        database.getReference(user?.uid.toString()).child("request").child(key)
            .child("description").setValue(textOfDescription)

        database.getReference(user?.uid.toString()).child("request").child(key)
            .child("image").setValue(imageKey)

        database.getReference(user?.uid.toString()).child("request").child(key)
            .child("status").setValue("на рассмотрении")

        saveToBd(auth, imageData = imageData, imageKey)
    }
}
