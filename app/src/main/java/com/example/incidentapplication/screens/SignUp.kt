package com.example.incidentapplication.screens

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.incidentapplication.R
import com.example.incidentapplication.componentUI.TextInput
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Composable
fun SignUp(navController: NavController, auth: FirebaseAuth) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val repeatPassword = remember { mutableStateOf("") }
    val fullName = remember { mutableStateOf("") }
    val context = LocalContext.current
    Column(
        Modifier
            .fillMaxSize()
            .padding(top = 24.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.sign_up),
            fontSize = 36.sp
        )
        Spacer(modifier = Modifier.height(56.dp))
        TextInput(stringResource(id = R.string.full_name), text = fullName)
        Spacer(modifier = Modifier.height(32.dp))
        TextInput(stringResource(id = R.string.email), text = email)
        Spacer(modifier = Modifier.height(32.dp))
        TextInput(
            stringResource(id = R.string.password_text),
            isSecret = true, text = password
        )
        Spacer(modifier = Modifier.height(32.dp))
        TextInput(
            stringResource(id = R.string.repeat_password_text),
            isSecret = true,
            text = repeatPassword
        )
        Spacer(modifier = Modifier.height(52.dp))

        Button(
            onClick = {
                when {
                    !isEmailValid(email.value) -> {
                        Toast.makeText(
                            context,
                            "Email is not valid",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    password.value.length < 6 -> {
                        Toast.makeText(
                            context,
                            "password less than 6 characters",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    repeatPassword.value.length < 6 -> {
                        Toast.makeText(
                            context,
                            "password less than 6 characters",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    password.value != repeatPassword.value -> {
                        Toast.makeText(
                            context,
                            "Passwords do not match",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    fullName.value.isEmpty() -> {
                        Toast.makeText(
                            context,
                            "Full name is empty",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        createNewAccount(
                            auth,
                            email.value,
                            password.value,
                            navController,
                            fullName.value
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(start = 8.dp, end = 8.dp)
        ) {
            Text(text = stringResource(id = R.string.sign_up))
        }

        Spacer(modifier = Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text = stringResource(id = R.string.go_login_text),
                fontSize = 12.sp,
                modifier = Modifier.clickable {
                    navController.navigate("Login")
                }
            )
        }
    }
}

fun isEmailValid(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun createNewAccount(
    auth: FirebaseAuth,
    email: String,
    password: String,
    navController: NavController,
    fullName: String
) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "createUserWithEmail:success")
                val user = auth.currentUser
                val database = Firebase.database
                val myRef = database.getReference(user?.uid.toString()).child("name")
                myRef.setValue(fullName)
                navController.navigate("Main")
            } else {
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
            }
        }
}
