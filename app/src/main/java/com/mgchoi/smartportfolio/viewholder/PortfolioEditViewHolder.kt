package com.mgchoi.smartportfolio.viewholder

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mgchoi.smartportfolio.R
import com.mgchoi.smartportfolio.databinding.RowPortfolioBinding
import com.mgchoi.smartportfolio.databinding.RowProfileBinding
import com.mgchoi.smartportfolio.model.Member
import com.mgchoi.smartportfolio.model.Portfolio
import com.mgchoi.smartportfolio.tool.OGTagImageGetter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PortfolioEditViewHolder(
    val context: Context,
    val binding: RowPortfolioBinding,
    val member: Member
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        const val VIEW_TYPE = R.layout.row_portfolio
    }

    var onEditRequest: ((delete: Boolean) -> Unit)? = null

    fun bind(portfolio: Portfolio) {

        binding.txtRowPortfolioTitle.text = portfolio.title
        binding.txtRowPortfolioContent.text = portfolio.content
        binding.imgRowPortfolioOgtag.visibility = View.GONE

        binding.txtRowPortfolioEdit.setOnClickListener { onEditRequest?.let { it(false) } }
        binding.txtRowPortfolioDelete.setOnClickListener { onEditRequest?.let { it(true) } }

        CoroutineScope(Dispatchers.IO).launch {
            val getter = OGTagImageGetter()
            val url = getter.getOGImageUrl(portfolio.url) ?: return@launch
            val image = getter.getImageFromUrl(url) ?: return@launch

            binding.imgRowPortfolioOgtag.visibility = View.VISIBLE
            binding.imgRowPortfolioOgtag.setImageBitmap(image)
        }

    }
}