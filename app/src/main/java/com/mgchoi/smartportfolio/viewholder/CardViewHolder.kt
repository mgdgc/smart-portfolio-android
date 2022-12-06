package com.mgchoi.smartportfolio.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mgchoi.smartportfolio.R
import com.mgchoi.smartportfolio.databinding.RowCardBinding
import com.mgchoi.smartportfolio.model.Portfolio
import com.mgchoi.smartportfolio.tool.OGTagImageGetter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CardViewHolder(private val binding: RowCardBinding) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        const val VIEW_TYPE = R.layout.row_card
    }

    var onLinkClick: ((url: String) -> Unit)? = null

    fun bind(portfolio: Portfolio) {
        binding.txtRowCardTitle.text = portfolio.title
        binding.txtRowCardContent.text = portfolio.content

        binding.btnRowCardLink.setOnClickListener {
            this.onLinkClick?.let { it(portfolio.url) }
        }

        // OGTag Image
        CoroutineScope(Dispatchers.IO).launch {
            val manager = OGTagImageGetter()
            val url = manager.getOGImageUrl(portfolio.url)

            if (url != null) {
                val image = manager.getImageFromUrl(url)
                if (image != null) {
                    withContext(Dispatchers.Main) {
                        // 이미지 로딩 프로그레스 바 숨기기
                        binding.progressRowCard.visibility = View.GONE
                        binding.imgRowCardImage.setImageBitmap(image)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        binding.layoutRowCardImg.visibility = View.GONE
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    binding.layoutRowCardImg.visibility = View.GONE
                }
            }
        }
    }
}