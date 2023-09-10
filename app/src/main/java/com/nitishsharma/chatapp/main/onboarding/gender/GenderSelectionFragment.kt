package com.nitishsharma.chatapp.main.onboarding.gender

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nitishsharma.chatapp.base.BaseFragment
import com.nitishsharma.chatapp.base.common.model.LoadingModel
import com.nitishsharma.chatapp.databinding.FragmentGenderSelectionBinding
import com.nitishsharma.chatapp.main.onboarding.OnboardingFragmentViewModel
import com.nitishsharma.chatapp.main.onboarding.gender.model.GenderModel
import com.nitishsharma.chatapp.notification.FCMService
import com.nitishsharma.chatapp.utils.setStatusBarColor
import com.nitishsharma.chatapp.utils.setVisibilityBasedOnLoadingModel
import timber.log.Timber

@RequiresApi(Build.VERSION_CODES.M)
class GenderSelectionFragment : BaseFragment<FragmentGenderSelectionBinding>() {
    private val genderModel = GenderModel.buildAndGetGenderModel()
    private var adapter: RecyclerView.Adapter<GenderSelectionAdapter.ViewHolder>? = null
    private var currentSelectedGender: String = "boy"
    private val viewModel: OnboardingFragmentViewModel by viewModels()

    override fun getViewBinding(): FragmentGenderSelectionBinding =
        FragmentGenderSelectionBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBarColor(requireActivity(), com.nitishsharma.chatapp.R.color.dark_gray)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupGenderRecyclerView()
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.userSavedSuccessfully.observe(viewLifecycleOwner, Observer {
            if (it) {
                Timber.i("User Saved To DB Successfully")
                navigateToHomeFragment()
            }
        })
        viewModel.loadingModel.observe(viewLifecycleOwner, Observer {
            binding.loadingModel.progressBar.setVisibilityBasedOnLoadingModel(it)
        })
    }

    private fun navigateToHomeFragment() {
        FCMService.subscribeToFirebaseTopic(firebaseInstance.currentUser?.uid)
        viewModel.updateLoadingModel(LoadingModel.COMPLETED)
        findNavController().navigate(
            GenderSelectionFragmentDirections.actionGenderSelectionFragmentToHomeFragment(
                firebaseUser = firebaseInstance.currentUser
            )
        )
    }

    override fun initClickListeners() {
        super.initClickListeners()
        binding.setGendereButton.setOnClickListener {
            viewModel.updateLoadingModel(LoadingModel.LOADING)
            viewModel.saveUserToDb(firebaseInstance.currentUser!!, currentSelectedGender)
        }
    }

    private fun setupGenderRecyclerView() {
        adapter = GenderSelectionAdapter(
            genderModel,
            object : GenderSelectionAdapter.ItemClickListener {
                override fun onItemClick(clickedGender: String) {
                    currentSelectedGender = clickedGender
                    Timber.i(currentSelectedGender)
                }
            },
            0
        )
        binding.genders.adapter = adapter
        binding.genders.layoutManager = LinearLayoutManager(activity)
    }
}