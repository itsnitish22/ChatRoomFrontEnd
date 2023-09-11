package com.nitishsharma.chatapp.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.nitishsharma.chatapp.base.common.model.LoadingModel
import org.koin.core.component.KoinComponent

open class BaseViewModel : ViewModel(), KoinComponent {
    val firebaseInstance = FirebaseAuth.getInstance()
    val firebaseDb = FirebaseDatabase.getInstance()

    private val _loadingModel: MutableLiveData<LoadingModel> = MutableLiveData()
    val loadingModel: LiveData<LoadingModel>
        get() = _loadingModel


    fun updateLoadingModel(loadingModel: LoadingModel = LoadingModel.COMPLETED) {
        _loadingModel.postValue(loadingModel)
    }

    override fun onCleared() {
        super.onCleared()
    }
}