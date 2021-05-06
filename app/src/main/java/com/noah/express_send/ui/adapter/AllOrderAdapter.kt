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
import com.noah.express_send.R
import com.noah.express_send.ui.adapter.io.IOrderOperate
import com.noah.internet.response.BestNewOrderEntity
import de.hdodenhof.circleimageview.CircleImageView

/**
 * @Auther: 何飘
 * @Date: 2/28/21 14:36
 * @Description:
 */
class AllOrderAdapter(
    private val orderInfoList: List<BestNewOrderEntity>,
    private val mContext: Context,
    private val iOrderOperate: IOrderOperate
) : RecyclerView.Adapter<AllOrderAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvExpress: TextView = view.findViewById(R.id.tv_express)
        val tvTypeName: TextView = view.findViewById(R.id.tv_typeName)
        val tvWeight: TextView = view.findViewById(R.id.tv_weights)
        val tvDormitory: TextView = view.findViewById(R.id.dormitory)
        val tvDetailAddress: TextView = view.findViewById(R.id.tv_address)
        val tvOrderState: TextView = view.findViewById(R.id.tv_orderState)
        val tvChangeOrderState: TextView = view.findViewById(R.id.btn_changeOrderState)
        val tvFuncOperate: TextView = view.findViewById(R.id.btn_funcOperate)
        val tvNickName: TextView = view.findViewById(R.id.tv_nickNameRight)
        val imgAvatarUrl: CircleImageView = view.findViewById(R.id.avatar)
        val tvPayIntegralNum: TextView = view.findViewById(R.id.tv_payIntegralNum)
        val tvOperateTime: TextView = view.findViewById(R.id.tv_operateTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_order_info_message,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderInfo = orderInfoList[position]
        holder.tvExpress.text = orderInfo.express
        holder.tvTypeName.text = orderInfo.typeName
        holder.tvWeight.text = orderInfo.weight
        holder.tvDormitory.text = orderInfo.dormitory
        holder.tvDetailAddress.text = orderInfo.addressName
        holder.tvOrderState.text = orderInfo.stateName
        holder.tvOperateTime.text = orderInfo.operateTime
        holder.tvPayIntegralNum.text = mContext.getString(R.string.payIntegralNum, orderInfo.payIntegralNum)
        if (orderInfo.nickName == null) {
            holder.tvNickName.text = mContext.resources.getString(R.string.no_nickname)
        } else {
            holder.tvNickName.text = orderInfo.nickName
        }

        holder.tvNickName.setOnClickListener {
            iOrderOperate.browseUserPageInfo(orderInfo.phoneNum, orderInfo.nickName)
        }
        holder.tvChangeOrderState.visibility = View.VISIBLE
        holder.tvFuncOperate.visibility = View.INVISIBLE
        holder.imgAvatarUrl.visibility = View.VISIBLE
        holder.tvNickName.visibility = View.VISIBLE
        holder.tvOrderState.visibility = View.VISIBLE
        when (orderInfo.stateName) {
            "待接单" -> {
                holder.imgAvatarUrl.visibility = View.GONE
                holder.tvNickName.visibility = View.GONE
                holder.tvOrderState.visibility = View.GONE
                holder.tvFuncOperate.visibility = View.VISIBLE
                holder.tvChangeOrderState.text = "取消订单"
                holder.tvChangeOrderState.setOnClickListener {
                    iOrderOperate.cancelOrder(orderInfo.oid.toString(), position, true)
                }
                holder.tvFuncOperate.text = "编辑"
                holder.tvFuncOperate.setOnClickListener {
                    iOrderOperate.modifyOrder(
                        orderInfo.oid.toString(),
                        orderInfo.phoneNum,
                        orderInfo
                    )
                }
            }
            "待派送" -> {
                holder.tvChangeOrderState.visibility = View.GONE
            }
            "待收货" -> {
                holder.tvOrderState.text = "正在派送"
                holder.tvChangeOrderState.text = "确认收货"
                holder.tvChangeOrderState.setOnClickListener {
                    iOrderOperate.confirmOrder(orderInfo.oid.toString(), position)
                }
            }
            "待评价" -> {
                holder.tvChangeOrderState.text = "评价"
                holder.tvChangeOrderState.setOnClickListener {
                    iOrderOperate.commentOrder(orderInfo)
                }
                holder.tvFuncOperate.visibility = View.INVISIBLE
                holder.tvOrderState.text = "待评价"
            }
            "已完成" -> {
                holder.tvChangeOrderState.text = "删除订单"
                holder.tvChangeOrderState.setOnClickListener {
                    iOrderOperate.cancelOrder(orderInfo.oid.toString(), position, false)
                }
            }
        }
        Glide.with(mContext).load(orderInfo.avatarUrl)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
            .preload(30, 30)
        Glide.with(mContext)
            .load(orderInfo.avatarUrl).placeholder(R.drawable.ic_place_holder)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
            .into(holder.imgAvatarUrl)
    }

    override fun getItemCount(): Int {
        return orderInfoList.size
    }
}