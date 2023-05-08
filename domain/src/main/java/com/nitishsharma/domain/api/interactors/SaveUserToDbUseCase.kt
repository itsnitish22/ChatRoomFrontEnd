package com.nitishsharma.domain.api.interactors

import android.os.Bundle
import com.google.firebase.auth.FirebaseUser
import com.nitishsharma.domain.api.repository.ChatRoomAPIRepository
import com.nitishsharma.domain.api.utility.Utils
import org.koin.core.component.KoinComponent

class SaveUserToDbUseCase constructor(private val chatRoomAPIRepository: ChatRoomAPIRepository) :
    KoinComponent {
    suspend operator fun invoke(currentUser: FirebaseUser, gender: String) =
        chatRoomAPIRepository.saveUserToDb(Utils.bundleToJSONMapping(null, Bundle().apply {
            putString("userId", currentUser.uid);
            putString("userName", currentUser.displayName)
            putString("userEmail", currentUser.email)
            putString("userAvatar", currentUser.photoUrl.toString())
            putString("userGender", gender)
            putBoolean("isFree", true)
            putBoolean("isActive", true)
            putInt("ownRooms", 0)
            putInt("otherRooms", 0)
        }))
}