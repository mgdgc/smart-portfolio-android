package com.mgchoi.smartportfolio.viewholder

import android.content.Context
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mgchoi.smartportfolio.R
import com.mgchoi.smartportfolio.databinding.RowPortfolioBinding
import com.mgchoi.smartportfolio.databinding.RowProfileBinding
import com.mgchoi.smartportfolio.model.Member
import com.mgchoi.smartportfolio.model.Portfolio
import com.mgchoi.smartportfolio.tool.CoverImageHelper
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

        // 제목과 내용 작성
        binding.txtRowPortfolioTitle.text = portfolio.title
        binding.txtRowPortfolioContent.text = portfolio.content
        binding.imgRowPortfolioOgtag.visibility = View.GONE

        // 수정 및 삭제 버튼
        binding.txtRowPortfolioEdit.setOnClickListener { onEditRequest?.let { it(false) } }
        binding.txtRowPortfolioDelete.setOnClickListener { onEditRequest?.let { it(true) } }

        Log.d("image", "${portfolio.image}")
        // 커버 이미지가 있으면 설정
        if (portfolio.image != null) {
            val coverImageHelper = CoverImageHelper(context)
            val image = coverImageHelper.read(portfolio.image!!)
            binding.imgRowPortfolioOgtag.setImageBitmap(image)
            binding.imgRowPortfolioOgtag.visibility = View.VISIBLE
        } else {
            // 커버 이미지가 없으면 OG Tag 이미지 불러오기
            CoroutineScope(Dispatchers.IO).launch {
                val getter = OGTagImageGetter()
                val url = getter.getOGImageUrl(portfolio.url) ?: return@launch
                val image = getter.getImageFromUrl(url) ?: return@launch

                CoroutineScope(Dispatchers.Main).launch {
                    binding.imgRowPortfolioOgtag.visibility = View.VISIBLE
                    binding.imgRowPortfolioOgtag.setImageBitmap(image)
                }
            }

        }

    }
}