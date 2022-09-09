package com.mgchoi.smartportfolio.viewholder

import android.content.Context
import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.mgchoi.smartportfolio.R
import com.mgchoi.smartportfolio.model.ViewStyle
import com.mgchoi.smartportfolio.databinding.RowProfileBinding
import com.mgchoi.smartportfolio.model.Member
import com.mgchoi.smartportfolio.tool.ProfileImageManager

class ProfileViewHolder(val context: Context, val binding: RowProfileBinding) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        const val VIEW_TYPE = R.layout.row_profile
    }

    var onNameEditRequest: (() -> Unit)? = null
    var onProfileImageEditRequest: (() -> Unit)? = null
    var onViewStyleChange: ((ViewStyle) -> Unit)? = null

    fun bind(member: Member) {
        binding.txtRowProfileName.text = member.name

        binding.spinnerRowProfileViewStyle.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p: AdapterView<*>?, v: View?, position: Int, i: Long) {
                    member.viewStyle = ViewStyle.of(position)
                    onViewStyleChange?.let { it(ViewStyle.of(position)) }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }

        val manager = ProfileImageManager(context)
        val profileImage = member.image?.let { manager.read(it) }
            ?: manager.defaultProfileImage(member.name)
        binding.imgProfile.setImageBitmap(profileImage)

        binding.frameProfilePic.setOnClickListener {
            onProfileImageEditRequest?.let { it() }
        }

        binding.layoutRowProfileEditName.setOnClickListener {
            onNameEditRequest?.let { it() }
        }


    }
}