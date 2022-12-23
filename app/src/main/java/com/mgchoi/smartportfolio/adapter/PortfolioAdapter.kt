package com.mgchoi.smartportfolio.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.mgchoi.smartportfolio.R
import com.mgchoi.smartportfolio.model.ViewStyle
import com.mgchoi.smartportfolio.WebViewActivity
import com.mgchoi.smartportfolio.databinding.RowCardBinding
import com.mgchoi.smartportfolio.databinding.RowFooterBinding
import com.mgchoi.smartportfolio.databinding.RowMessageBinding
import com.mgchoi.smartportfolio.databinding.RowTimelineBinding
import com.mgchoi.smartportfolio.model.Member
import com.mgchoi.smartportfolio.model.Portfolio
import com.mgchoi.smartportfolio.value.SharedPreferenceKeys
import com.mgchoi.smartportfolio.viewholder.CardViewHolder
import com.mgchoi.smartportfolio.viewholder.FooterViewHolder
import com.mgchoi.smartportfolio.viewholder.MessageViewHolder
import com.mgchoi.smartportfolio.viewholder.TimelineViewHolder

interface DetailViewRequestListener {
    fun cardView(binding: RowCardBinding, portfolioId: Int)
    fun timelineView(binding: RowTimelineBinding, portfolioId: Int)
    fun messageView(binding: RowMessageBinding, portfolioId: Int)
}

class PortfolioAdapter(private val context: Context, private var member: Member) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var detailViewRequestListener: DetailViewRequestListener? = null

    var data: ArrayList<Portfolio> = arrayListOf()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun add(vararg portfolios: Portfolio) {
        val prevCount = this.data.size
        this.data.addAll(portfolios)
        notifyItemRangeInserted(prevCount, portfolios.size)
    }

    fun setMember(member: Member) {
        this.member = member
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TimelineViewHolder.VIEW_TYPE -> TimelineViewHolder(
                context,
                RowTimelineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            MessageViewHolder.VIEW_TYPE -> MessageViewHolder(
                context,
                RowMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            CardViewHolder.VIEW_TYPE -> CardViewHolder(
                context,
                RowCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> FooterViewHolder(
                RowFooterBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TimelineViewHolder -> {
                holder.onLinkClick = { openLink(it) }
                holder.onDetailClick = { b, p ->
                    detailViewRequestListener?.let { it.timelineView(b, p) }
                }
                holder.bind(this.data[position])
            }
            is MessageViewHolder -> {
                holder.onLinkClick = { openLink(it) }
                holder.onDetailClick = { b, p ->
                    detailViewRequestListener?.let { it.messageView(b, p) }
                }
                holder.bind(this.data[position])
            }
            is CardViewHolder -> {
                holder.onLinkClick = { openLink(it) }
                holder.onDetailClick = { b, p ->
                    detailViewRequestListener?.let { it.cardView(b, p) }
                }
                holder.bind(this.data[position])
            }
        }
    }

    private fun openLink(url: String) {
        val pref = context.getSharedPreferences(SharedPreferenceKeys.PREF_APP, 0)
        when (pref.getInt(SharedPreferenceKeys.INT_BROWSER, 0)) {
            0 -> {
                // Checkbox for remember decision
                val checkbox = CheckBox(context).apply {
                    this.setText(R.string.portfolio_browser_remember)
                }

                // Alert dialog for choosing browser
                val alert = AlertDialog.Builder(context)
                alert.setTitle(R.string.portfolio_browser_title)
                    .setMessage(R.string.portfolio_browser_content)
                    .setView(checkbox)
                    .setPositiveButton(R.string.portfolio_browser_chrome) { d, _ ->
                        pref.edit().putInt(
                            SharedPreferenceKeys.INT_BROWSER,
                            if (checkbox.isChecked) 2 else 0
                        ).apply()
                        d.dismiss()
                        openLinkAsChromeCustomTab(url)
                    }
                    .setNegativeButton(R.string.portfolio_browser_internal) { d, _ ->
                        pref.edit().putInt(
                            SharedPreferenceKeys.INT_BROWSER,
                            if (checkbox.isChecked) 1 else 0
                        ).apply()
                        d.dismiss()
                        openLinkAsWebViewActivity(url)
                    }
                    .show()
            }
            1 -> openLinkAsWebViewActivity(url)
            2 -> openLinkAsChromeCustomTab(url)
        }
    }

    private fun openLinkAsWebViewActivity(url: String) {
        val intent = Intent(context, WebViewActivity::class.java)
        intent.putExtra(WebViewActivity.EXTRA_URL, url)
        context.startActivity(intent)
    }

    private fun openLinkAsChromeCustomTab(url: String) {
        WebViewActivity.openAsCustomTab(context, url)
    }

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)
        return if (position < this.data.size) {
            when (member.viewStyle) {
                ViewStyle.TIMELINE -> TimelineViewHolder.VIEW_TYPE
                ViewStyle.MESSAGE -> MessageViewHolder.VIEW_TYPE
                ViewStyle.CARD -> CardViewHolder.VIEW_TYPE
            }
        } else {
            FooterViewHolder.VIEW_TYPE
        }
    }

    override fun getItemCount(): Int {
        return this.data.size + 1
    }
}