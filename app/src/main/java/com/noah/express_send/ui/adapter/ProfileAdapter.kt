package com.noah.express_send.ui.adapter

import android.app.Activity
import android.graphics.Color
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.noah.express_send.R
import com.noah.express_send.bean.ProfileInfo
import com.noah.express_send.ui.adapter.io.IOpenModify
import de.hdodenhof.circleimageview.CircleImageView
import me.leefeng.promptlibrary.PromptButton
import me.leefeng.promptlibrary.PromptDialog

/**
 * @Auther: 何飘
 * @Date: 3/27/21 20:24
 * @Description:
 */
class ProfileAdapter(
    private val ProfileInfoList: List<ProfileInfo>,
    private val activity: Activity,
    private val iOpenModify: IOpenModify,
    private val phoneNum: String?
) : RecyclerView.Adapter<ProfileAdapter.ViewHolder>() {
    private lateinit var promptDialog: PromptDialog

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvTitleName)
        val word: TextView = view.findViewById(R.id.tvWord)
        val avatar: CircleImageView = view.findViewById(R.id.profileAvatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_profile_view,
            parent,
            false
        )
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            when (viewHolder.adapterPosition) {
                0 -> popBottomDialogPhoto()
                1 -> iOpenModify.startModifyActivity(
                    "修改" + viewHolder.title.text.toString(),
                    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, "email"
                )
                2 -> iOpenModify.startModifyActivity(
                    "修改" + viewHolder.title.text.toString(),
                    InputType.TYPE_CLASS_TEXT, "nick_name"
                )
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val profileInfo = ProfileInfoList[position]
        holder.title.text = profileInfo.title
        holder.word.text = profileInfo.word
        if (position == 0) {
            val img = profileInfo.imgResource
            if (img == null) {
                // 本地和后台都没有图片
                holder.avatar.setImageResource(R.drawable.ic_place_holder)
            } else {
                // 使用Glide加载网络图片
                Glide.with(activity).load(img)
                    .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .preload()
                Glide.with(activity).load(img)
                    .placeholder(R.drawable.ic_place_holder)
                    .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .into(holder.avatar)
            }
        }
    }

    override fun getItemCount(): Int {
        return ProfileInfoList.size
    }

    private fun popBottomDialogPhoto() {
        val cancel = PromptButton("取消", null)
        cancel.textColor = Color.parseColor("#0076ff")
        promptDialog = PromptDialog(activity)
        promptDialog.showAlertSheet(
            "", true, cancel,
            PromptButton(
                "相册选取"
            ) {
                iOpenModify.pickImage(false)
            },
            PromptButton("拍摄") {
                iOpenModify.pickImage(true)
            },
        )
    }

}