package com.nitishsharma.chatapp.main.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.nitishsharma.chatapp.base.BaseViewModel
import com.nitishsharma.domain.api.interactors.SaveUserToDbUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class OnboardingFragmentViewModel : BaseViewModel(), KoinComponent {
    private val saveUserToDbUseCase: SaveUserToDbUseCase by inject()

    private val _userSavedSuccessfully: MutableLiveData<Boolean> = MutableLiveData(false)
    val userSavedSuccessfully: LiveData<Boolean>
        get() = _userSavedSuccessfully

    fun saveUserToDb(
        currentUser: FirebaseUser,
        gender: String
    ) {
        try {
            viewModelScope.launch {
                val response = saveUserToDbUseCase.invoke(currentUser, gender)
                if (response.isSuccessful)
                    _userSavedSuccessfully.postValue(true)
            }
        } catch (e: Exception) {
            Timber.tag("Save User Failed").e(e.toString())
        }
    }
}