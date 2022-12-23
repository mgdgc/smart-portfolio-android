package com.mgchoi.smartportfolio.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.mgchoi.smartportfolio.R
import com.mgchoi.smartportfolio.databinding.RowIndexBinding
import com.mgchoi.smartportfolio.model.Member
import com.mgchoi.smartportfolio.tool.ProfileImageHelper

class IndexViewHolder(val binding: RowIndexBinding) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        const val VIEW_TYPE = R.layout.row_index
    }

    fun bind(member: Member) {
        binding.txtRowIndexName.text = member.name
        binding.txtRowIndexUrl.text = member.url

        val manager = ProfileImageHelper(binding.root.context)
        val profileImage = member.image?.let { manager.read(it) }
            ?: manager.defaultProfileImage(member.name)
        binding.imgRowIndex.setImageBitmap(profileImage)
    }
}