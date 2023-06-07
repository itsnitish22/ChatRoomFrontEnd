package com.nitishsharma.chatapp.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.firebase.auth.FirebaseAuth
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    var socketIOInstance: Socket? = null
    lateinit var firebaseInstance: FirebaseAuth
    val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    lateinit var binding: VB

    abstract fun getViewBinding(): VB

    override fun onDetach() {
        super.onDetach()
        coroutineScope.coroutineContext.cancelChildren()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        firebaseInstance = FirebaseAuth.getInstance()
        socketIOInstance = (activity as BaseActivity<*>).socketIOInstance
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initComposeView()
        initClickListeners()
        initObservers()
        initSocketListeners()
    }

    open fun initSocketListeners() {}

    open fun initObservers() {}

    open fun initClickListeners() {}

    open fun initComposeView() {}
}