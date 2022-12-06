package com.mgchoi.smartportfolio.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mgchoi.smartportfolio.databinding.RowIndexBinding
import com.mgchoi.smartportfolio.model.Member
import com.mgchoi.smartportfolio.viewholder.IndexViewHolder

class IndexAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var data: ArrayList<Member> = arrayListOf()
        set(value) {
            val size = data.size
            this.data.clear()
            notifyItemRangeRemoved(0, size)
            this.data.addAll(value)
            notifyItemRangeInserted(0, value.size)
        }

    var onItemClick: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return IndexViewHolder(
            RowIndexBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as IndexViewHolder

        holder.bind(this.data[position])
        holder.binding.root.setOnClickListener {
            onItemClick?.let { it(position) }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}