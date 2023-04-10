package com.nitishsharma.chatapp.onboarding

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.nitishsharma.chatapp.R
import com.nitishsharma.chatapp.databinding.FragmentOnboardingBinding
import com.nitishsharma.chatapp.utils.Utility.toast
import timber.log.Timber

class OnboardingFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentOnboardingBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private val onboardingVM: OnboardingFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentOnboardingBinding.inflate(inflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeInstances() //initialize instances
        initializeObservers()
        setupComposeView()
    }

    private fun initializeObservers() {
        onboardingVM.userSavedSuccessfully.observe(requireActivity(), Observer {
            if (it) {
                Timber.tag("User Saved Successfully").i("User saved to DB")
                navigateToHomeFragment()
            }
        })
    }

    private fun setupComposeView() {
        binding.composeView.setContent {
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
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(width = 1.dp, color = Color.LightGray),
            color = MaterialTheme.colorScheme.surface,
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
                    text = "Authenticate to continue",
                    color = Color(0xFF808080),
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(R.font.inter_bold))
                )
            }
        }
    }

    /**
    this is [google] authentication/ sign in
    no need to touch anything here
     */

    //initializing instances
    private fun initializeInstances() {
        auth = FirebaseAuth.getInstance()
        initializeGoogleAuthentication()
    }

    //initializing google authentication
    private fun initializeGoogleAuthentication() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    //authenticating with google
    private fun authenticateWithGoogle() {
        binding.progressBar.visibility = View.VISIBLE
        launcher.launch(googleSignInClient.signInIntent)
    }

    //launcher dealing w results
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                auth.fetchSignInMethodsForEmail(task.result.email.toString())
                    .addOnCompleteListener { work ->
                        if (work.isSuccessful) {
                            val result = work.result?.signInMethods
                            if (result.isNullOrEmpty())
                                handleResults(task, true)
                            else
                                handleResults(task, false)
                        } else {
                            binding.progressBar.visibility = View.GONE
                            toast("oops!")
                        }
                    }
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

    //handling google authenticate results
    private fun handleResults(task: Task<GoogleSignInAccount>, saveToDB: Boolean) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            account?.let {
                updateUI(it, saveToDB)
            }
        } else {
            binding.progressBar.visibility = View.GONE
            toast("Some error occurred")
        }
    }

    //updating the ui on successful authentication
    private fun updateUI(account: GoogleSignInAccount, saveToDB: Boolean) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                if (saveToDB)
                    saveUserToDb()
                else
                    navigateToHomeFragment()
            } else {
                toast("Some error occurred")
            }
        }
    }

    private fun saveUserToDb() {
        onboardingVM.saveUserToDb(auth.currentUser!!)
    }

    private fun navigateToHomeFragment() {
        binding.progressBar.visibility = View.GONE
        findNavController().navigate(
            OnboardingFragmentDirections.actionOnboardingFragmentToHomeFragment(
                firebaseUser = auth.currentUser
            )
        )
    }
}