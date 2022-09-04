package com.mgchoi.smartportfolio.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.mgchoi.smartportfolio.R
import com.mgchoi.smartportfolio.databinding.RowCardBinding
import com.mgchoi.smartportfolio.model.Portfolio

class CardViewHolder(private val binding: RowCardBinding) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        const val VIEW_TYPE = R.layout.row_card
    }

    var onLinkClick: ((url: String) -> Unit)? = null

    fun bind(portfolio: Portfolio) {
        binding.txtRowCardTitle.text = portfolio.title
        binding.txtRowCardContent.text = portfolio.content

        binding.txtRowCardTitle.setOnClickListener {
            this.onLinkClick?.let { it(portfolio.url) }
        }
    }
}