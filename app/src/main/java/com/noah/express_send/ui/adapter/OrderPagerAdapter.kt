package com.noah.express_send.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.noah.express_send.R
import com.noah.express_send.ui.adapter.io.IOrderDetails
import com.noah.internet.response.BestNewOrderEntity
import com.noah.internet.response.ResponseOrderEntity

/**
 * @Auther: 何飘
 * @Date: 5/4/21 21:23
 * @Description:
 */
class OrderPagerAdapter(
    private val mContext: Context,
    private val iOrderDetails: IOrderDetails,
    private val layoutView: Int
) :
    RecyclerView.Adapter<OrderPagerAdapter.ViewHolder>() {
    private val orderList = ArrayList<BestNewOrderEntity>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.avatar)
        val username: TextView = view.findViewById(R.id.tv_nickNameRight)
        val orderState: TextView = view.findViewById(R.id.tv_orderState)
        val express: TextView = view.findViewById(R.id.tv_express)
        val weight: TextView = view.findViewById(R.id.tv_weights)
        val classify: TextView = view.findViewById(R.id.tv_typeName)
        val integralNum: TextView = view.findViewById(R.id.tv_payIntegralNum)
        val dormitory: TextView = view.findViewById(R.id.dormitory)
        val location: TextView = view.findViewById(R.id.tv_address)
        val operateTime: TextView = view.findViewById(R.id.tv_operateTime)

        val btnChangeOrderState: TextView = view.findViewById(R.id.btn_changeOrderState)
        val btnFuncOperate: TextView = view.findViewById(R.id.btn_funcOperate)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderPagerAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            layoutView, parent, false
        )
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            iOrderDetails.entryOrderDetails(orderList[viewHolder.layoutPosition])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: OrderPagerAdapter.ViewHolder, position: Int) {
        val orderInfo = orderList[position]
        holder.btnChangeOrderState.visibility = View.INVISIBLE
        holder.btnFuncOperate.visibility = View.INVISIBLE

        holder.orderState.text = orderInfo.stateName
        holder.express.text = orderInfo.express
        holder.location.text = orderInfo.addressName
        holder.username.text = orderInfo.nickName
        holder.weight.text = orderInfo.weight
        holder.classify.text = orderInfo.typeName
        holder.integralNum.text =
            mContext.getString(R.string.payIntegralNum, orderInfo.payIntegralNum)
        holder.dormitory.text = orderInfo.dormitory
        holder.operateTime.text = orderInfo.operateTime

        holder.username.setOnClickListener {
            iOrderDetails.browseUserPageInfo(orderInfo.phoneNum, orderInfo.nickName)
        }
        Glide.with(mContext).load(orderInfo.avatarUrl)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
            .preload()
        Glide.with(mContext).load(orderInfo.avatarUrl)
            .placeholder(R.drawable.ic_place_holder)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
            .into(holder.avatar)
        when (orderInfo.stateName) {
            mContext.getString(R.string.Order_toBeSend) -> {
                holder.orderState.text = mContext.getString(R.string.Order_received)
                holder.btnChangeOrderState.text = mContext.getString(R.string.start_send_order)
                holder.btnChangeOrderState.visibility = View.VISIBLE
                holder.btnFuncOperate.visibility = View.INVISIBLE
                holder.btnChangeOrderState.setOnClickListener {
                    iOrderDetails.startDeliverOrder(orderInfo)
                }
            }
            "待收货" -> {
                holder.orderState.text = "已派单"
            }
            "待评价" -> {
                holder.orderState.text = "已收货"
                displayDeleteButton(holder, orderInfo, position)
            }
            "已完成" -> {
                holder.orderState.text = "已完成"
                displayDeleteButton(holder, orderInfo, position)
            }
        }
    }

    private fun displayDeleteButton(
        holder: OrderPagerAdapter.ViewHolder,
        orderInfo: BestNewOrderEntity,
        position: Int
    ) {
        holder.btnChangeOrderState.visibility = View.VISIBLE
        holder.btnChangeOrderState.text = "删除订单"
        holder.btnChangeOrderState.setOnClickListener {
            iOrderDetails.deleteOrder(orderInfo.oid.toString(), position)
        }
    }

    fun setAdapter(orderList: ArrayList<BestNewOrderEntity>) {
        this.orderList.clear()
        this.orderList.addAll(orderList)
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        orderList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

}