package com.nitishsharma.chatapp.base.common.bindingadapter

import android.view.View
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import com.nitishsharma.chatapp.base.common.model.LoadingModel

@BindingAdapter("commonLoadingModelBinding")
fun setLoadingModelVisibility(progressBar: ProgressBar, loadingModel: LoadingModel) {
    progressBar.visibility = when (loadingModel) {
        LoadingModel.LOADING -> View.VISIBLE
        else -> View.GONE
    }
}