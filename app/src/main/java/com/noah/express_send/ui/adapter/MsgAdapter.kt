package com.noah.express_send.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import cn.jpush.im.android.api.content.CustomContent
import cn.jpush.im.android.api.content.ImageContent
import cn.jpush.im.android.api.content.TextContent
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.noah.express_send.R
import com.noah.express_send.bean.Msg
import com.noah.express_send.ui.adapter.io.IReceiveOrderOperate
import com.wanglu.photoviewerlibrary.PhotoViewer


class MsgAdapter(
    private val mContext: Context,
    private val activity: AppCompatActivity,
    private val iReceiveOrderOperate: IReceiveOrderOperate
) :
    RecyclerView.Adapter<MsgViewHolder>() {
    private val msgList = ArrayList<Msg>()
    override fun getItemViewType(position: Int): Int {
        val msg = msgList[position]
        return msg.type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        if (viewType == Msg.TEXT_SENT || viewType == Msg.IMG_SENT) {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.item_msg_right,
                parent,
                false
            )
            RightViewHolder(view)
        } else if (viewType == Msg.ORDER_REQUEST_SENT){
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.item_middle_hint,
                parent,
                false
            )
            MiddleViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.item_msg_left,
                parent,
                false
            )
            LeftViewHolder(view)
        }

    override fun onBindViewHolder(holder: MsgViewHolder, position: Int) {
        val msg = msgList[position]
        when (holder) {
            is LeftViewHolder -> {
                if (msg.type == Msg.TEXT_RECEIVED) {
                    holder.leftMsg.text = (msg.message.content as TextContent).text
                    holder.leftImg.visibility = View.GONE
                    holder.leftMsg.visibility = View.VISIBLE
                    holder.leftAvatar.setOnClickListener {
                        iReceiveOrderOperate.clickAvatar()
                    }
                } else if (msg.type == Msg.IMG_RECEIVED){
                    Glide.with(mContext)
                        .load((msg.message.content as ImageContent).localThumbnailPath)
                        .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                        .into(holder.leftImg)
                    holder.leftMsg.visibility = View.GONE
                    holder.leftImg.visibility = View.VISIBLE
                    holder.leftImg.setOnClickListener {
                        PhotoViewer
                            .setClickSingleImg(
                                (msg.message.content as ImageContent).localThumbnailPath,
                                holder.leftImg
                            )   //因为本框架不参与加载图片，所以还是要写回调方法
                            .setShowImageViewInterface(object : PhotoViewer.ShowImageViewInterface {
                                override fun show(iv: ImageView, url: String) {
                                    Glide.with(iv.context).load(url).into(iv)
                                }
                            }).start(activity)
                    }
                    holder.leftAvatar.setOnClickListener {
                        iReceiveOrderOperate.clickAvatar()
                    }
                } else {
                    if (msg.type == Msg.OTHER) return
                    holder.leftReceiveRequest.visibility = View.VISIBLE
                    holder.leftMsg.visibility = View.INVISIBLE
                    val tvRefuse: TextView = holder.leftReceiveRequest.findViewById(R.id.btn_refuse)
                    val tvAgree: TextView = holder.leftReceiveRequest.findViewById(R.id.btn_agree)
                    val customContent = msg.message.content as CustomContent
                    tvRefuse.setOnClickListener {
                        iReceiveOrderOperate.refuseRequest(customContent.getStringValue("oid"))
                    }
                    tvAgree.setOnClickListener {
                        iReceiveOrderOperate.agreeRequest(customContent.getStringValue("oid"))
                    }
                    holder.leftAvatar.setOnClickListener {
                        iReceiveOrderOperate.clickAvatar()
                    }
                }
                if (msg.bitmap != null) {
                    holder.leftAvatar.setImageBitmap(msg.bitmap)
                } else {
                    holder.leftAvatar.setImageResource(R.drawable.ic_place_holder)
                }
            }
            is RightViewHolder -> {
                if (msg.type == Msg.TEXT_SENT) {
                    holder.rightMsg.text = (msg.message.content as TextContent).text
                    holder.rightImg.visibility = View.GONE
                    holder.rightMsg.visibility = View.VISIBLE
                } else if (msg.type == Msg.IMG_SENT){
                    Glide.with(mContext)
                        .load((msg.message.content as ImageContent).localThumbnailPath)
                        .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                        .into(holder.rightImg)
                    holder.rightMsg.visibility = View.GONE
                    holder.rightImg.visibility = View.VISIBLE
                    holder.rightImg.setOnClickListener {
                        PhotoViewer
                            .setClickSingleImg(
                                (msg.message.content as ImageContent).localThumbnailPath,
                                holder.rightImg
                            )   //因为本框架不参与加载图片，所以还是要写回调方法
                            .setShowImageViewInterface(object : PhotoViewer.ShowImageViewInterface {
                                override fun show(iv: ImageView, url: String) {
                                    Glide.with(iv.context).load(url).into(iv)
                                }
                            }).start(activity)
                    }
                }
                if (msg.bitmap != null) {
                    holder.rightAvatar.setImageBitmap(msg.bitmap)
                } else {
                    holder.rightAvatar.setImageResource(R.drawable.ic_place_holder)
                }
            }
            is MiddleViewHolder -> {
                val customContent = msg.message.content as CustomContent
                holder.hintInfo.text = if (customContent.getStringValue("request") != null) {
                    customContent.getStringValue("request")
                } else {
                    customContent.getStringValue("hint")
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