package com.noah.express_send.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.noah.express_send.R
import com.noah.internet.response.CommentAllInfo
import de.hdodenhof.circleimageview.CircleImageView
import java.util.ArrayList

/**
 * @Auther: 何飘
 * @Date: 2/28/21 14:36
 * @Description:
 */
class HistoryCommentAdapter(
    private val mContext: Context,
) : RecyclerView.Adapter<HistoryCommentAdapter.ViewHolder>() {
    private val commentInfoList = ArrayList<CommentAllInfo>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: CircleImageView = view.findViewById(R.id.iv_avatarComment)
        val nickname: TextView = view.findViewById(R.id.tv_nickname)
        val symbolHigh: TextView = view.findViewById(R.id.symbol_high)
        val content: TextView = view.findViewById(R.id.tv_commentContent)
        val chipGroup: ChipGroup = view.findViewById(R.id.chipGroup)
        val createTime: TextView = view.findViewById(R.id.tv_createTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_single_comment_info,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val commentInfo = commentInfoList[position]
        holder.nickname.text = commentInfo.nickname
        holder.content.text = commentInfo.content
        holder.createTime.text = commentInfo.createTime

        if (commentInfo.type == 0) {
            holder.symbolHigh.visibility = View.INVISIBLE
        } else {
            holder.symbolHigh.visibility = View.VISIBLE
        }

        if (commentInfo.chipList.size > 0) {
            holder.chipGroup.removeAllViews()
            for (chipEntity in commentInfo.chipList) {
                val chip = LayoutInflater.from(holder.itemView.context)
                    .inflate(R.layout.item_chip_small, holder.chipGroup, false) as Chip
                chip.text = chipEntity.chipName
                holder.chipGroup.addView(chip)
            }
            holder.chipGroup.visibility = View.VISIBLE
        } else {
            holder.chipGroup.visibility = View.GONE
        }

        Glide.with(mContext).load(commentInfo.avatarUrl)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
            .preload(40, 40)
        Glide.with(mContext)
            .load(commentInfo.avatarUrl).placeholder(R.drawable.ic_place_holder)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
            .into(holder.avatar)
    }

    override fun getItemCount(): Int {
        return commentInfoList.size
    }

    fun setAdapter(commentInfoList: ArrayList<CommentAllInfo>) {
        this.commentInfoList.clear()
        this.commentInfoList.addAll(commentInfoList)
        notifyDataSetChanged()
    }
}