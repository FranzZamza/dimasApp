package com.example.incidentapplication

import kotlinx.coroutines.Job


data class Request(
    var topic: String = "",
    val description: String = "",
    val image: String = "",
    val status: String = "",
    val key: String = ""
)