package com.mgchoi.smartportfolio.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.mgchoi.smartportfolio.R
import com.mgchoi.smartportfolio.databinding.RowFooterBinding

class FooterViewHolder(private val binding: RowFooterBinding) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        const val VIEW_TYPE = R.layout.row_footer
    }

}