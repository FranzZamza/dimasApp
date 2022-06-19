package com.example.incidentapplication


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class MainViewModel : ViewModel() {

    private val _allRequests = MutableLiveData<List<Request>>()
    val allRequests: LiveData<List<Request>> = _allRequests

    private val _requests = MutableLiveData<List<Request>>()
    val request: LiveData<List<Request>> = _requests

    private val _topic = MutableLiveData<String>()
    val topic: LiveData<String> = _topic

    private val _desc = MutableLiveData<String>()
    val desc: LiveData<String> = _desc

    private val _listOfCompleteRequest = MutableLiveData<List<Request>>()
    val listOfCompleteRequest: LiveData<List<Request>> = _listOfCompleteRequest

    fun setDesc(des: String) {
        _desc.postValue(des)
    }

    fun setTopic(topic: String) {
        _topic.postValue(topic)
    }

    private suspend fun getImageUrlFromStorage(photoId: String): String {
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

    private suspend fun getImageUrlFromStorage(uid: String, photoId: String): String {
        val storageRef = Firebase.storage.reference
        val pathReference = storageRef.child("$uid/$photoId")
        return try {
            val imageUrl = pathReference.downloadUrl.await().normalizeScheme().toString()
            Log.e("imageUrlCopy", imageUrl)
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
                val topic = item.child("topic").value.toString()
                val description = item.child("description").value.toString()
                val imageId = getImageUrlFromStorage(item.child("image").value.toString())
                val status = item.child("status").value.toString()
                listOfRequest.add(Request(topic, description, imageId, status))
                Log.e("RequestList", "$topic $description $imageId")
            }
            _requests.postValue(listOfRequest)
            Log.e("_requests", "${_requests.value}")
        } catch (e: Exception) {
        }
    }

    fun getAllRequests() {
        val listOfRequest = mutableListOf<Request>()
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val data = Firebase.database.reference.get().await()
                val listOfUsers = data.children
                for (user in listOfUsers) {
                    val uid = user.key
                    user.child("request").children.forEach {
                        it.child("status")
                        if (it.child("status").value == "в ожидании") {
                            val key = it.key.toString()
                            val topic = it.child("topic").value.toString()
                            val description = it.child("description").value.toString()
                            val imageId = getImageUrlFromStorage(
                                uid.toString(),
                                it.child("image").value.toString()
                            )
                            val status = it.child("status").value.toString()

                            listOfRequest.add(Request(topic, description, imageId, status, key))
                        }
                    }
                }
                _allRequests.postValue(listOfRequest)
            }
        } catch (e: Exception) {
            Log.e("AllRequestException", "${e.stackTrace}")
        }
    }

    fun getCompleteRequests() {
        val listOfRequest = mutableListOf<Request>()
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val data = Firebase.database.reference.get().await()
                val listOfUsers = data.children
                for (user in listOfUsers) {
                    val uid = user.key
                    user.child("request").children.forEach {
                        if (it.child("status").value == "выполнено") {
                            val topic = it.child("topic").value.toString()
                            val description = it.child("description").value.toString()
                            val imageId = getImageUrlFromStorage(
                                uid.toString(),
                                it.child("image").value.toString()
                            )
                            val status = it.child("status").value.toString()
                            listOfRequest.add(Request(topic, description, imageId, status))
                        }
                    }
                }
                _listOfCompleteRequest.postValue(listOfRequest)
            }
        } catch (e: Exception) {
            Log.e("CompleteRequest", "${e.stackTrace}")
        }
    }

    fun completeRequest(requestKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            var uid=""
            var key=""
            val data = Firebase.database.reference.get().await()
            val listOfUsers = data.children
            for (user in listOfUsers) {
                for (request in user.child("request").children){
                    if (request.key.toString() == requestKey){
                        Log.e("currentUser", user.key.toString())
                        Log.e("requestKey", request.key.toString())
                        Log.e("status", request.child("status").toString())
                        uid= user.key.toString()
                        key= request.key.toString()
                    }
                }
            }
            Firebase.database.getReference(uid).child("request")
                .child(key).child("status").setValue("выполнено").await()
        }
    }

        fun rejectRequest(requestKey: String){
        viewModelScope.launch(Dispatchers.IO) {
            var uid=""
            var key=""
            val data = Firebase.database.reference.get().await()
            val listOfUsers = data.children
            for (user in listOfUsers) {
                for (request in user.child("request").children){
                    if (request.key.toString() == requestKey){
                        Log.e("currentUser", user.key.toString())
                        Log.e("requestKey", request.key.toString())
                        Log.e("status", request.child("status").toString())
                        uid= user.key.toString()
                        key= request.key.toString()
                    }
                }
            }
            Firebase.database.getReference(uid).child("request")
                .child(key).child("status").setValue("отклонено").await()
        }
    }
}

