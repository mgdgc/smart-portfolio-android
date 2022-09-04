package com.mgchoi.smartportfolio.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.mgchoi.smartportfolio.R
import com.mgchoi.smartportfolio.databinding.RowMessageBinding
import com.mgchoi.smartportfolio.model.Portfolio

class MessageViewHolder(private val binding: RowMessageBinding) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        const val VIEW_TYPE = R.layout.row_message
    }

    var onLinkClick: ((url: String) -> Unit)? = null

    fun bind(portfolio: Portfolio) {
        binding.txtRowMessageTitle.text = portfolio.title
        binding.txtRowMessageContent.text = portfolio.content

        binding.cardRowMessageLink.setOnClickListener {
            this.onLinkClick?.let { it(portfolio.url) }
        }
    }
}