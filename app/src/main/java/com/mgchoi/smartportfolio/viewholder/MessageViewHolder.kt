package com.mgchoi.smartportfolio.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mgchoi.smartportfolio.R
import com.mgchoi.smartportfolio.databinding.RowMessageBinding
import com.mgchoi.smartportfolio.model.Portfolio
import com.mgchoi.smartportfolio.tool.CoverImageHelper
import com.mgchoi.smartportfolio.tool.OGTagImageGetter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        // 이미지가 있으면 이미지를, 없으면 OG Tag 보여주기
        if (portfolio.image != null) {
            val coverImageHelper = CoverImageHelper(binding.root.context)
            val image = coverImageHelper.read(portfolio.image!!)
            binding.imgRowMessageImage.setImageBitmap(image)
        } else {
            // OGTag Image
            CoroutineScope(Dispatchers.IO).launch {
                val manager = OGTagImageGetter()
                val url = manager.getOGImageUrl(portfolio.url)

                if (url != null) {
                    val image = manager.getImageFromUrl(url)
                    if (image != null) {
                        withContext(Dispatchers.Main) {
                            // 이미지 로딩 프로그레스 바 숨기기
                            binding.progressRowMessage.visibility = View.GONE
                            binding.imgRowMessageImage.setImageBitmap(image)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            binding.cardRowMessageImage.visibility = View.GONE
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        binding.cardRowMessageImage.visibility = View.GONE
                    }
                }
            }
        }
    }
}