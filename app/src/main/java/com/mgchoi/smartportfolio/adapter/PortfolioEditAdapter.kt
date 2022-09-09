package com.mgchoi.smartportfolio.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mgchoi.smartportfolio.databinding.RowFooterBinding
import com.mgchoi.smartportfolio.databinding.RowPortfolioBinding
import com.mgchoi.smartportfolio.databinding.RowProfileBinding
import com.mgchoi.smartportfolio.model.Member
import com.mgchoi.smartportfolio.model.Portfolio
import com.mgchoi.smartportfolio.model.ViewStyle
import com.mgchoi.smartportfolio.viewholder.FooterViewHolder
import com.mgchoi.smartportfolio.viewholder.PortfolioEditViewHolder
import com.mgchoi.smartportfolio.viewholder.ProfileViewHolder

interface ProfileEditRequest {
    fun onImageEditRequest()
    fun onNameEditRequest()
    fun onViewStyleChange(newValue: ViewStyle)
}

interface PortfolioEditRequest {
    fun onEditRequest(index: Int, position: Int, portfolio: Portfolio)
    fun onDeleteRequest(index: Int, position: Int, portfolio: Portfolio)
}

class PortfolioEditAdapter(private val context: Context, private val member: Member) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data: ArrayList<Portfolio> = arrayListOf()

    var profileEditRequest: ProfileEditRequest? = null
    var portfolioEditRequest: PortfolioEditRequest? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ProfileViewHolder.VIEW_TYPE -> ProfileViewHolder(
                context,
                RowProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

            PortfolioEditViewHolder.VIEW_TYPE -> PortfolioEditViewHolder(
                context,
                RowPortfolioBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                member
            )

            else -> FooterViewHolder(
                RowFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProfileViewHolder -> {
                holder.onProfileImageEditRequest = {
                    this.profileEditRequest?.onImageEditRequest()
                }
                holder.onNameEditRequest = {
                    this.profileEditRequest?.onNameEditRequest()
                }
                holder.onViewStyleChange = {
                    this.profileEditRequest?.onViewStyleChange(it)
                }

                holder.bind(member)
            }

            is PortfolioEditViewHolder -> {
                val index = position - 1
                holder.onEditRequest = { delete ->
                    if (delete) this.portfolioEditRequest?.onDeleteRequest(index, position, data[index])
                    else this.portfolioEditRequest?.onEditRequest(index, position, data[index])

                    notifyItemChanged(position)
                }

                holder.bind(this.data[index])
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size + 2
    }

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)
        return when (position) {
            0 -> ProfileViewHolder.VIEW_TYPE
            this.data.size + 1 -> FooterViewHolder.VIEW_TYPE
            else -> PortfolioEditViewHolder.VIEW_TYPE
        }
    }
}