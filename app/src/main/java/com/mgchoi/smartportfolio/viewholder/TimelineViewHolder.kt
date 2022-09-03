package com.mgchoi.smartportfolio.viewholder

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mgchoi.smartportfolio.R
import com.mgchoi.smartportfolio.databinding.RowTimelineBinding
import com.mgchoi.smartportfolio.model.Portfolio

class TimelineViewHolder(private val binding: RowTimelineBinding) :
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

    fun bind(portfolio: Portfolio) {
        // Set texts
        binding.txtRowTimelineTitle.text = portfolio.title
        binding.txtRowTimelineContent.text = portfolio.content

        // Set dot color
        val colorIndex = portfolio.title.hashCode() % COLORS_DOT.size
        binding.imgRowTimelineDot.imageTintList =
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    binding.root.context,
                    COLORS_DOT[colorIndex]
                )
            )

        // Set listeners
        binding.cardRowTimeline.setOnClickListener {
            this.onLinkClick?.let { it(portfolio.url) }
        }
    }
}