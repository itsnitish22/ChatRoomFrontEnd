package com.nitishsharma.chatapp.main.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.nitishsharma.chatapp.base.BaseFragment
import com.nitishsharma.chatapp.databinding.FragmentHomeDrawerBinding
import com.nitishsharma.chatapp.utils.Utility.toast

class HomeFragmentDrawer : BaseFragment<FragmentHomeDrawerBinding>() {
    override fun getViewBinding() = FragmentHomeDrawerBinding.inflate(layoutInflater)
    private val homeFragmentDrawerVM: HomeFragmentViewModel by activityViewModels()

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