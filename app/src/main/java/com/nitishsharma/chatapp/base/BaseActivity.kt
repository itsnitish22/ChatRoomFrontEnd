package com.nitishsharma.chatapp.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.google.firebase.auth.FirebaseAuth
import com.nitishsharma.chatapp.application.FirstChat
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    var socketIOInstance: Socket? = null
    var firebaseAuth: FirebaseAuth? = null
    lateinit var binding: VB
    lateinit var baseViewModel: BaseViewModel
    val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    abstract fun getViewBinding(): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        socketIOInstance = (application as FirstChat).socketIO

        onViewCreated()
    }

    open fun onViewCreated() {
        initClickListeners()
        initObservers()
        initSocketListeners()
    }

    open fun initSocketListeners() {}

    open fun initObservers() {}

    open fun initClickListeners() {}

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.coroutineContext.cancelChildren()
    }
}