package com.example.incidentapplication


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.lang.Exception


class MainViewModel : ViewModel() {
    private val _requests = MutableLiveData<List<Request>>()
    val request: LiveData<List<Request>> = _requests

    private val _photoAddress = MutableLiveData<String>()
    val photoAddress: LiveData<String> = _photoAddress

    suspend fun getImageUrlFromStorage(photoId: String): String {
        val storageRef = Firebase.storage.reference
        val uid = Firebase.auth.uid
        val pathReference = storageRef.child("$uid/$photoId")
        return try {
            val imageUrl = pathReference.downloadUrl.await().normalizeScheme().toString()
            Log.e("imageUrl", imageUrl)
            imageUrl
        } catch (e: Exception) {
            ""
        }
    }

    private val _imageId = MutableLiveData<String>()


    fun imageIdSet(imageId: String) {
        _imageId.postValue(imageId)
    }

    suspend fun getDataFromDb() {
        val auth = Firebase.auth
         val listOfRequest = mutableListOf<Request>()
         try {
            val data = Firebase.database.getReference(auth.uid.toString())
                .child("request").get().await()
            for (item in data.children) {
                val topic= item.child("topic").value.toString()
                val description = item.child("description").value.toString()
                val imageId = getImageUrlFromStorage(item.child("image").value.toString())
                listOfRequest.add(Request(topic,description,imageId))
                Log.e("RequestList","$topic $description $imageId")

            }
            _requests.postValue(listOfRequest)
             Log.e("_requests","${_requests.value}")
        } catch (e: Exception) {
        }
    }

}

