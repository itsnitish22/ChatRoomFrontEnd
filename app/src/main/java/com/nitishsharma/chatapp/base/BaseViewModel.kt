package com.nitishsharma.chatapp.base

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.core.component.KoinComponent

open class BaseViewModel : ViewModel(), KoinComponent {
    val firebaseInstance = FirebaseAuth.getInstance()
    override fun onCleared() {
        super.onCleared()
    }
}