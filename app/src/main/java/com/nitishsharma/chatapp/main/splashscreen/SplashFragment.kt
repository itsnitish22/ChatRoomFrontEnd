package com.nitishsharma.chatapp.main.splashscreen

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseUser
import com.nitishsharma.chatapp.base.BaseFragment
import com.nitishsharma.chatapp.databinding.FragmentSplashBinding
import com.nitishsharma.chatapp.main.onboarding.OnboardingFragmentViewModel
import com.nitishsharma.chatapp.utils.Utility.setStatusBarColor

class SplashFragment : BaseFragment<FragmentSplashBinding>() {
    override fun getViewBinding(): FragmentSplashBinding =
        FragmentSplashBinding.inflate(layoutInflater)

    private val onboardingVM: OnboardingFragmentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(requireActivity(), com.nitishsharma.chatapp.R.color.dark_gray)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userInfo = checkLoggedInStatus()
        if (userInfo != null) {
            onboardingVM.checkIfUserExists(firebaseInstance.currentUser!!)
        } else
            navigateToOnboardingScreen()
    }

    override fun initObservers() {
        super.initObservers()
        onboardingVM.userExists.observe(viewLifecycleOwner, Observer { userExists ->
            userExists.getContentIfNotHandled()?.let {
                if (it)
                    navigateToHomeScreen(firebaseInstance.currentUser!!)
                else
                    navigateToGenderSelectionFragment()
            }
        })
    }

    private fun navigateToGenderSelectionFragment() {
        Handler().postDelayed({
            view?.post {
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToGenderSelectionFragment())
            }
        }, 2000)
    }


    private fun navigateToOnboardingScreen() {
        Handler().postDelayed({
            view?.post {
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToOnboardingFragment())
            }
        }, 2000)
    }

    private fun navigateToHomeScreen(userInfo: FirebaseUser) {
        Handler().postDelayed({
            view?.post {
                findNavController().navigate(
                    SplashFragmentDirections.actionSplashFragmentToHomeFragment(
                        userInfo
                    )
                )
            }
        }, 2000)
    }

    private fun checkLoggedInStatus(): FirebaseUser? {
        firebaseInstance.currentUser?.let {
            return it
        }
        return null
    }
}