package com.nitishsharma.chatapp.onboarding

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.nitishsharma.chatapp.api.retrofit.RetrofitInstance
import com.nitishsharma.chatapp.utils.Utility
import kotlinx.coroutines.launch
import timber.log.Timber

class OnboardingFragmentViewModel : ViewModel() {
    private val _userSavedSuccessfully: MutableLiveData<Boolean> = MutableLiveData(false)
    val userSavedSuccessfully: LiveData<Boolean>
        get() = _userSavedSuccessfully

    fun saveUserToDb(
        currentUser: FirebaseUser
    ) {
        try {
            viewModelScope.launch {
                RetrofitInstance.api.saveUserToDb(Utility.bundleToJSONMapping(null, Bundle().apply {
                    putString("userId", currentUser.uid);
                    putString("userName", currentUser.displayName)
                    putString("userAvatar", currentUser.photoUrl.toString())
                }))
            }
            _userSavedSuccessfully.postValue(true)
        } catch (e: Exception) {
            Timber.tag("Save User Failed").e(e.toString())
        }
    }
}