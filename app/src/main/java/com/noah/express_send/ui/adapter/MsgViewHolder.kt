package com.noah.express_send.ui.adapter

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.noah.express_send.R
import de.hdodenhof.circleimageview.CircleImageView

sealed class MsgViewHolder(view: View) : RecyclerView.ViewHolder(view)

class LeftViewHolder(view: View) : MsgViewHolder(view) {
    val leftMsg: TextView = view.findViewById(R.id.leftMsg)
    val leftAvatar: CircleImageView = view.findViewById(R.id.chatLeftAvatar)
}

class RightViewHolder(view: View) : MsgViewHolder(view) {
    val rightMsg: TextView = view.findViewById(R.id.rightMsg)
    val rightAvatar: CircleImageView = view.findViewById(R.id.chatRightAvatar)
}