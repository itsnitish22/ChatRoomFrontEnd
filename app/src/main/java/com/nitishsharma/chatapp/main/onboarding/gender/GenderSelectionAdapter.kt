package com.nitishsharma.chatapp.main.onboarding.gender

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.nitishsharma.chatapp.R
import com.nitishsharma.chatapp.databinding.ItemGenderBinding
import com.nitishsharma.chatapp.main.onboarding.gender.model.GenderSelectionModel

@RequiresApi(Build.VERSION_CODES.M)
class GenderSelectionAdapter(
    private val genderModel: ArrayList<GenderSelectionModel>,
    private val itemClickListener: ItemClickListener,
    private val selectedGenderBeforeCreation: Int,
) : RecyclerView.Adapter<GenderSelectionAdapter.ViewHolder>() {

    interface ItemClickListener {
        fun onItemClick(clickedGender: String)
    }

    private var currentSelectedItem = -1
    private var previousSelectedItem = -1
    private var currentViewHolder: GenderSelectionAdapter.ViewHolder? = null
    private var previousViewHolder: GenderSelectionAdapter.ViewHolder? = null

    inner class ViewHolder(val binding: ItemGenderBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemGenderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val currentGender = genderModel[position]
        with(holder) {
            binding.apply {
                genderIv.setImageResource(currentGender.image)
                genderName.text = currentGender.gender.capitalize()
            }
            if (selectedGenderBeforeCreation == position) {
                changeCurrentItem(position, holder)
                selectTheCurrentItem(currentViewHolder, itemView)
                changePreviousItem(currentSelectedItem, holder)
            }
            binding.genderCard.setOnClickListener {
                changeCurrentItem(position, holder)
                if (currentSelectedItem != previousSelectedItem) {
                    selectTheCurrentItem(currentViewHolder, itemView)
                    deselectThePreviousItem(previousViewHolder, itemView)
                    changePreviousItem(currentSelectedItem, holder)
                }
                itemClickListener.onItemClick(currentGender.gender)
            }
        }
    }

    private fun changePreviousItem(
        currentSelectedItem: Int,
        holder: GenderSelectionAdapter.ViewHolder
    ) {
        previousSelectedItem = currentSelectedItem
        previousViewHolder = holder
    }

    private fun changeCurrentItem(position: Int, holder: GenderSelectionAdapter.ViewHolder) {
        currentSelectedItem = position
        currentViewHolder = holder
    }

    private fun deselectThePreviousItem(previousViewHolder: ViewHolder?, itemView: View) {
        previousViewHolder?.let {
            with(it.binding) {
                genderCard.strokeColor = itemView.context.getColor(R.color.whitish)
                selected.visibility = View.GONE
            }
        }
    }

    private fun selectTheCurrentItem(currentViewHolder: ViewHolder?, itemView: View) {
        currentViewHolder?.let {
            with(it.binding) {
                genderCard.strokeColor = itemView.context.getColor(R.color.chatroom_light_blue)
                selected.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount() = genderModel.size
}