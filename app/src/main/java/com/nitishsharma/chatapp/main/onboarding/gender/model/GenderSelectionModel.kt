package com.nitishsharma.chatapp.main.onboarding.gender.model

import com.nitishsharma.chatapp.R

data class GenderSelectionModel(
    val gender: String,
    val image: Int
)

object GenderModel {
    fun buildAndGetGenderModel(): ArrayList<GenderSelectionModel> = arrayListOf(
        GenderSelectionModel("boy", R.drawable.ic_boy),
        GenderSelectionModel("girl", R.drawable.ic_girl)
    )
}