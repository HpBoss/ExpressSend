package com.noah.express_send.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.noah.express_send.R
import com.noah.express_send.bean.Msg


class MsgAdapter(private val mContext: Context) : RecyclerView.Adapter<MsgViewHolder>() {
    private val msgList = ArrayList<Msg>()
    override fun getItemViewType(position: Int): Int {
        val msg = msgList[position]
        return msg.type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = if (viewType == Msg.TYPE_RECEIVED) {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.msg_left_item, parent, false)
        LeftViewHolder(view)
    } else {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.msg_right_item, parent, false)
        RightViewHolder(view)
    }

    override fun onBindViewHolder(holder: MsgViewHolder, position: Int) {
        val msg = msgList[position]
        when (holder) {
            is LeftViewHolder -> {
                holder.leftMsg.text = msg.content
                if (msg.bitmap != null) {
                    holder.leftAvatar.setImageBitmap(msg.bitmap)
                } else {
                    holder.leftAvatar.setImageResource(R.drawable.ic_place_holder)
                }
            }
            is RightViewHolder -> {
                holder.rightMsg.text = msg.content

                if (msg.bitmap != null) {
                    holder.rightAvatar.setImageBitmap(msg.bitmap)
                } else {
                    holder.rightAvatar.setImageResource(R.drawable.ic_place_holder)
                }
            }
         }

    }

    override fun getItemCount() = msgList.size

    fun addListMsg(list: ArrayList<Msg>) {
        msgList.clear()
        msgList.addAll(list)
        notifyDataSetChanged()
    }

    fun addOneMsg(msg: Msg) {
        msgList.add(msg)
        notifyItemInserted(msgList.size - 1)
    }

}