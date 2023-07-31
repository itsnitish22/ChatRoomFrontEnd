package com.nitishsharma.chatapp.main.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.nitishsharma.chatapp.base.BaseViewModel
import com.nitishsharma.chatapp.utils.Event
import com.nitishsharma.domain.api.interactors.CheckIfUserExistsUseCase
import com.nitishsharma.domain.api.interactors.SaveUserToDbUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class OnboardingFragmentViewModel : BaseViewModel(), KoinComponent {
    private val saveUserToDbUseCase: SaveUserToDbUseCase by inject()
    private val checkIfUserExistsUseCase: CheckIfUserExistsUseCase by inject()

    private val _userSavedSuccessfully: MutableLiveData<Boolean> = MutableLiveData(false)
    val userSavedSuccessfully: LiveData<Boolean>
        get() = _userSavedSuccessfully

    private val _userExists: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val userExists: LiveData<Event<Boolean>>
        get() = _userExists

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

    fun checkIfUserExists(
        currentUser: FirebaseUser
    ) {
        try {
            viewModelScope.launch {
                val response = checkIfUserExistsUseCase.invoke(currentUser)
                if (response.isSuccessful)
                    _userExists.postValue(Event(response.body()?.userExists!!))
            }
        } catch (e: Exception) {
            Timber.tag("Save User Failed").e(e.toString())
        }
    }
}