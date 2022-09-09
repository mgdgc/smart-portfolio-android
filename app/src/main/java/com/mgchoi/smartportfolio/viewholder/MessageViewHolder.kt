package com.mgchoi.smartportfolio.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mgchoi.smartportfolio.R
import com.mgchoi.smartportfolio.databinding.RowMessageBinding
import com.mgchoi.smartportfolio.model.Portfolio
import com.mgchoi.smartportfolio.tool.OGTagImageGetter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        // OGTag Image
        CoroutineScope(Dispatchers.IO).launch {
            val manager = OGTagImageGetter()
            val url = manager.getOGImageUrl(portfolio.url)
            url?.let { urlString ->
                val image = manager.getImageFromUrl(urlString)
                image?.let { bitmap ->
                    binding.cardRowMessageImage.visibility = View.VISIBLE
                    binding.imgRowMessageImage.setImageBitmap(bitmap)
                }
            }
        }
    }
}