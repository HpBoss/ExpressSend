package com.noah.express_send.ui.adapter

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback
import cn.jpush.im.android.api.content.TextContent
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.UserInfo
import com.noah.express_send.R
import com.noah.express_send.ui.adapter.io.IClickMessageItem
import de.hdodenhof.circleimageview.CircleImageView

/**
 * @Auther: 何飘
 * @Date: 2/28/21 14:36
 * @Description:
 */
class MessageAdapter(
    private val mContext: Context,
    private val iClickMessageItem: IClickMessageItem
) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    private val conversationList: MutableList<Conversation> = mutableListOf()
    private var bitmap: Bitmap? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: CircleImageView = view.findViewById(R.id.message_avatar)
        val nickname: TextView = view.findViewById(R.id.message_nickName)
        val sender: TextView = view.findViewById(R.id.sender)
        val content: TextView = view.findViewById(R.id.content)
        val redHint: TextView = view.findViewById(R.id.redHint)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_message,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val conversation = conversationList[position]
        if (conversation.latestMessage == null) return
        if (conversation.targetInfo is UserInfo) {
            val targetInfo = conversation.targetInfo as UserInfo
            //JMessageClient.deleteSingleConversation(targetInfo.userName)
            if (targetInfo.nickname == null || targetInfo.nickname.isEmpty()) {
                holder.nickname.text = mContext.resources.getString(R.string.no_nickname)
            } else {
                holder.nickname.text = targetInfo.nickname
            }
            if (conversation.latestMessage != null) {
                holder.content.text = (conversation.latestMessage?.content as TextContent).text
            }
            targetInfo.getAvatarBitmap(object : GetAvatarBitmapCallback() {
                override fun gotResult(p0: Int, p1: String?, p2: Bitmap?) {
                    bitmap = if (p0 == 0) {
                        holder.avatar.setImageBitmap(p2)
                        p2
                    } else {
                        holder.avatar.setImageResource(R.drawable.ic_place_holder)
                        null
                    }
                }
            })

            holder.itemView.setOnClickListener {
                conversation.updateConversationExtra("")
                iClickMessageItem.setOnClickMessageItem(
                    position,
                    bitmap,
                    conversation.targetId,
                    targetInfo.nickname
                )
            }
        }
        if (conversation.extra.equals("new_message")) {
            holder.redHint.text = conversation.unReadMsgCnt.toString()
            holder.redHint.visibility = View.VISIBLE
        } else {
            holder.redHint.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return conversationList.size
    }

    fun setAdapter(messageList: MutableList<Conversation>) {
        conversationList.clear()
        conversationList.addAll(messageList)
        notifyDataSetChanged()
    }

    fun clearAdapterList() {
        conversationList.clear()
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        conversationList.removeAt(position)
        notifyDataSetChanged()
    }
}