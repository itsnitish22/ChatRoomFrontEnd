package com.nitishsharma.chatapp.main.onboarding

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.GoogleAuthProvider
import com.nitishsharma.chatapp.R
import com.nitishsharma.chatapp.base.BaseFragment
import com.nitishsharma.chatapp.base.common.LoadingModel
import com.nitishsharma.chatapp.databinding.FragmentOnboardingBinding
import com.nitishsharma.chatapp.notification.FCMService
import com.nitishsharma.chatapp.utils.setStatusBarColor
import com.nitishsharma.chatapp.utils.setVisibilityBasedOnLoadingModel
import com.nitishsharma.chatapp.utils.toast
import org.koin.core.component.KoinComponent

class OnboardingFragment : BaseFragment<FragmentOnboardingBinding>(), KoinComponent {
    override fun getViewBinding() = FragmentOnboardingBinding.inflate(layoutInflater)
    private lateinit var googleSignInClient: GoogleSignInClient
    private val onboardingVM: OnboardingFragmentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(requireActivity(), R.color.app_bg)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeInstances()
        setupComposeView()
    }

    private fun setupComposeView() {
        binding.joinChatRoomLayout.composeView.setContent {
            GoogleAuthenticationButton()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    fun GoogleAuthenticationButton() {
        var clicked by remember {
            mutableStateOf(false)
        }

        Surface(
            onClick = {
                clicked = !clicked
                authenticateWithGoogle()
            },
            shape = RoundedCornerShape(5.dp),
            border = BorderStroke(width = 1.dp, color = Color.LightGray),
            color = Color.White,
            modifier = Modifier.animateContentSize(
                animationSpec = tween(
                    300,
                    easing = LinearOutSlowInEasing
                )
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(
                    start = 12.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 16.dp
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "",
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Authenticate With Google",
                    color = Color(0xFF000000),
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.sans_med))
                )
            }
        }
    }

    private fun initializeInstances() {
        initializeGoogleAuthentication()
    }

    private fun initializeGoogleAuthentication() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    private fun authenticateWithGoogle() {
        onboardingVM.updateLoadingModel(LoadingModel.LOADING)
        launcher.launch(googleSignInClient.signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                firebaseInstance.fetchSignInMethodsForEmail(task.result.email.toString())
                    .addOnCompleteListener { work ->
                        if (work.isSuccessful) {
                            val result = work.result?.signInMethods
                            if (result.isNullOrEmpty())
                                handleResults(task, true)
                            else
                                handleResults(task, false)
                        } else {
                            onboardingVM.updateLoadingModel(LoadingModel.ERROR)
                            toast("oops!")
                        }
                    }
            } else {
                onboardingVM.updateLoadingModel(LoadingModel.ERROR)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>, saveToDB: Boolean) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            account?.let {
                updateUI(it, saveToDB)
            }
        } else {
            onboardingVM.updateLoadingModel(LoadingModel.ERROR)
            toast("Some error occurred")
        }
    }

    private fun updateUI(account: GoogleSignInAccount, saveToDB: Boolean) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseInstance.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                if (saveToDB) {
                    navigateToGenderSelectionFragment()
                } else {
                    checkIfUserExists()
                }
            } else {
                toast("Some error occurred")
            }
        }
    }

    override fun initObservers() {
        super.initObservers()

        onboardingVM.userExists.observe(viewLifecycleOwner, Observer { userExists ->
            userExists.getContentIfNotHandled()?.let {
                if (it)
                    navigateToHomeFragment()
                else
                    navigateToGenderSelectionFragment()
            }
        })
        onboardingVM.loadingModel.observe(viewLifecycleOwner, Observer {
            binding.loadingModel.progressBar.setVisibilityBasedOnLoadingModel(it)
        })
    }

    private fun checkIfUserExists() {
        onboardingVM.checkIfUserExists(firebaseInstance.currentUser!!)
    }

    private fun navigateToGenderSelectionFragment() {
        findNavController().navigate(OnboardingFragmentDirections.actionOnboardingFragmentToGenderSelectionFragment())
    }

    private fun navigateToHomeFragment() {
        FCMService.subscribeToFirebaseTopic(firebaseInstance.currentUser?.uid)
        onboardingVM.updateLoadingModel(LoadingModel.COMPLETED)
        findNavController().navigate(
            OnboardingFragmentDirections.actionOnboardingFragmentToHomeFragment(
                firebaseUser = firebaseInstance.currentUser
            )
        )
    }
}
