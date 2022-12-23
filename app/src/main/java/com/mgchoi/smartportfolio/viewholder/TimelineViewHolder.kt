package com.mgchoi.smartportfolio.viewholder

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.provider.Contacts.Intents.UI
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mgchoi.smartportfolio.PortfolioDetailActivity
import com.mgchoi.smartportfolio.R
import com.mgchoi.smartportfolio.databinding.RowTimelineBinding
import com.mgchoi.smartportfolio.model.Portfolio
import com.mgchoi.smartportfolio.tool.CoverImageHelper
import com.mgchoi.smartportfolio.tool.OGTagImageGetter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

class TimelineViewHolder(private val context: Context, private val binding: RowTimelineBinding) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        const val VIEW_TYPE = R.layout.row_timeline
        private val COLORS_DOT = arrayOf(
            R.color.red,
            R.color.blue,
            R.color.sky,
            R.color.indigo,
            R.color.green,
            R.color.teal,
            R.color.pink,
            R.color.yellow,
            R.color.orange,
            R.color.purple
        )
    }

    var onLinkClick: ((String) -> Unit)? = null
    var onDetailClick: ((binding: RowTimelineBinding, portfolioId: Int) -> Unit)? = null

    fun bind(portfolio: Portfolio) {
        // Set texts
        binding.txtRowTimelineTitle.text = portfolio.title
        binding.txtRowTimelineContent.text = portfolio.content

        // Set dot color
        val colorIndex = abs(portfolio.title.hashCode() % COLORS_DOT.size)
        binding.imgRowTimelineDot.imageTintList =
            ColorStateList.valueOf(ContextCompat.getColor(context, COLORS_DOT[colorIndex]))

        // Set listeners
        binding.cardRowTimeline.setOnClickListener {
            onDetailClick?.let { it(binding, portfolio.id) }
        }
        binding.layoutRowTimelineLink.setOnClickListener {
            this.onLinkClick?.let { it(portfolio.url) }
        }

        // 이미지가 있으면 이미지를, 없으면 OG Tag 보여주기
        if (portfolio.image != null) {
            val coverImageHelper = CoverImageHelper(binding.root.context)
            val image = coverImageHelper.read(portfolio.image!!)
            binding.imgRowTimelineImage.setImageBitmap(image)
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
                            binding.progressRowTimeline.visibility = View.GONE
                            binding.imgRowTimelineImage.setImageBitmap(image)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            binding.cardRowTimelineImage.visibility = View.GONE
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        binding.cardRowTimelineImage.visibility = View.GONE
                    }
                }
            }
        }
    }
}