package com.mgchoi.smartportfolio.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.mgchoi.smartportfolio.R
import com.mgchoi.smartportfolio.databinding.RowPortfolioShareBinding

class PortfolioShareViewHolder(private val binding: RowPortfolioShareBinding) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        const val VIEW_TYPE = R.layout.row_portfolio_share
    }

    var onShareClick: (() -> Unit)? = null

    fun bind() {
        binding.cardRowPortfolioShare.setOnClickListener {
            onShareClick?.let { it() }
        }
    }

}