package com.nitishsharma.chatapp.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import com.nitishsharma.chatapp.databinding.FragmentHomeDrawerBinding


class HomeFragmentDrawer : Fragment() {
    private val firebaseInstance = FirebaseAuth.getInstance()
    private lateinit var binding: FragmentHomeDrawerBinding
    private val homeFragmentDrawerVM: HomeFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentHomeDrawerBinding.inflate(layoutInflater).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            signOut.setOnClickListener {
                signOutUser()
            }
        }
    }

    private fun signOutUser() {
        homeFragmentDrawerVM.signOutUser()
    }
}