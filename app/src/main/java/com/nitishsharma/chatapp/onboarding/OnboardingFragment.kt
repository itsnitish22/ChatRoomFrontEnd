package com.nitishsharma.chatapp.onboarding

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.nitishsharma.chatapp.R
import com.nitishsharma.chatapp.databinding.FragmentOnboardingBinding


class OnboardingFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentOnboardingBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnboardingBinding.inflate(inflater, container, false)

        initializeInstances() //initialize instances

        //on click signInWithGoogle
        binding.signInWithGoogle.setOnClickListener {
            authenticateWithGoogle()
        }

        return binding.root
    }

    //initializing instances
    private fun initializeInstances() {
        auth = FirebaseAuth.getInstance()
        initializeGoogleAuthentication()
        setGooglePlusButtonText(binding.signInWithGoogle, "Authenticate to continue")
    }

    //initializing google authentication
    private fun initializeGoogleAuthentication() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        googleSignInClient.revokeAccess()
    }

    //authenticating with google
    private fun authenticateWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    //launcher dealing w results
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
        }

    //handling google authenticate results
    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            Log.i("OnboardingFrag", "Failed login task")
        }
    }

    //updating the ui on successful authentication
    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(requireContext(), "Success", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //setting up text on google authenticate button
    private fun setGooglePlusButtonText(signInButton: SignInButton, buttonText: String?) {
        for (i in 0 until signInButton.childCount) {
            val v = signInButton.getChildAt(i)
            if (v is TextView) {
                v.text = buttonText
                return
            }
        }
    }
}