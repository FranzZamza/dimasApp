package com.example.incidentapplication.componentUI

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun TextInput(label: String, isSecret: Boolean = false, text:MutableState<String>) {
    OutlinedTextField(
        value = text.value, onValueChange = { text.value = it },
        label = { Text(text = label) }, modifier = Modifier
            .height(60.dp)
            .fillMaxWidth(),
        visualTransformation = if (isSecret) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true
    )
}