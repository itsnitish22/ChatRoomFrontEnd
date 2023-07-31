package com.nitishsharma.domain.api.interactors

import android.os.Bundle
import com.google.firebase.auth.FirebaseUser
import com.nitishsharma.domain.api.repository.ChatRoomAPIRepository
import com.nitishsharma.domain.api.utility.Utils
import org.koin.core.component.KoinComponent

class CheckIfUserExistsUseCase constructor(private val chatRoomAPIRepository: ChatRoomAPIRepository) :
    KoinComponent {

    suspend operator fun invoke(firebaseUser: FirebaseUser) =
        chatRoomAPIRepository.checkIfUserExists(
            Utils.bundleToJSONMapping(
                null,
                Bundle().apply {
                    putString("userId", firebaseUser.uid)
                }
            ))
}