package com.nitishsharma.chatapp.splashscreen

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.nitishsharma.chatapp.R
import com.nitishsharma.chatapp.databinding.FragmentSplashBinding

class SplashFragment : Fragment(R.layout.fragment_splash) {
    private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(inflater, container, false)

        if (checkLoggedInStatus())
            navigateToHomeScreen()
        else //change to home
            navigateToOnboardingScreen()

        return binding.root

    }

    private fun navigateToOnboardingScreen() {
        Handler().postDelayed({
            view?.post {
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToOnboardingFragment())
            }
        }, 3000)
    }

    private fun navigateToHomeScreen() {
        Handler().postDelayed({
            view?.post {
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToOnboardingFragment())
            }
        }, 3000)
    }

    private fun checkLoggedInStatus(): Boolean {
        if (FirebaseAuth.getInstance().currentUser != null)
            return true
        return false
    }
}