package com.example.incidentapplication.screens
import android.content.ContentValues.TAG
import android.content.Context
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
import androidx.navigation.NavHostController
import com.example.incidentapplication.R
import com.example.incidentapplication.componentUI.TextInput
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Login(navController: NavHostController, auth: FirebaseAuth) {
    val context = LocalContext.current
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(text = stringResource(id = R.string.login_text), fontSize = 36.sp)
        Spacer(modifier = Modifier.height(82.dp))
        TextInput(stringResource(id = R.string.email), text = email)
        Spacer(modifier = Modifier.height(56.dp))
        TextInput(
            label = stringResource(id = R.string.password_text),
            isSecret = true,
            text = password
        )
        Spacer(modifier = Modifier.height(142.dp))
        Button(
            onClick = {
                when {
                    !isEmailValid(email.value) -> {
                        Toast.makeText(
                            context,
                            "Email is not valid",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    password.value.length < 6 -> {
                        Toast.makeText(context, "Password is not correct", Toast.LENGTH_SHORT)
                            .show()
                    }
                    else -> {
                        loginWithEmailAndPassword(
                            auth,
                            email.value,
                            password.value,
                            navController,
                            context
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(start = 8.dp, end = 8.dp)
        ) {
            Text(text = stringResource(id = R.string.login_text))
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text = stringResource(id = R.string.go_sign_up_text),
                fontSize = 12.sp,
                modifier = Modifier.clickable {
                    navController.navigate("SignUp")
                }
            )
        }
    }
}

private fun loginWithEmailAndPassword(
    auth: FirebaseAuth,
    email: String,
    password: String,
    navController: NavHostController,
    context: Context
) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener() { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInWithEmail:success")
                navController.navigate("Main")
            } else {
                Toast.makeText(context, "Password or email is not correct", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "signInWithEmail:failure", task.exception)
            }
        }
}

