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
import com.noah.express_send.ui.adapter.io.IOrderInfo
import com.noah.internet.response.ResponseOrder
import java.util.ArrayList

/**
 * @Auther: 何飘
 * @Date: 2/28/21 14:36
 * @Description:
 */
class IndexFragmentAdapter(
    private val mContext: Context,
    private val iOrderInfo: IOrderInfo
) : RecyclerView.Adapter<IndexFragmentAdapter.ViewHolder>(){
    private val orderInfoList = ArrayList<ResponseOrder>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvBrand: TextView = view.findViewById(R.id.tv_brand)
        val tvClassify: TextView = view.findViewById(R.id.tv_classify)
        val tvWeight: TextView = view.findViewById(R.id.tv_weight)
        val tvDetailAddress: TextView = view.findViewById(R.id.tv_address)
        val tvDormitory: TextView = view.findViewById(R.id.tv_dormitorys)
        val imgAvatar: ImageView = view.findViewById(R.id.cardView_avatar)
        val imgMoreOption: ImageView = view.findViewById(R.id.btn_moreOption)
        val tvPayIntegralNum: TextView = view.findViewById(R.id.tv_payIntegralNum)
        val tvNickname: TextView = view.findViewById(R.id.tv_userName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_order_info_indexs,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderInfo = orderInfoList[position]
        holder.tvBrand.text = orderInfo.expressName
        holder.tvClassify.text = orderInfo.typeName
        holder.tvWeight.text = orderInfo.weight
        holder.tvDetailAddress.text = orderInfo.addressName
        holder.tvDormitory.text = orderInfo.dormitory
        holder.tvNickname.text = orderInfo.nickName
        holder.tvPayIntegralNum.text = mContext.getString(R.string.payIntegralNum, orderInfo.payIntegralNum)

        Glide.with(mContext).load(orderInfo.avatarUrl)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
            .preload(50,50)
        Glide.with(mContext)
            .load(orderInfo.avatarUrl).placeholder(R.drawable.ic_place_holder)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
            .into(holder.imgAvatar)

        holder.imgMoreOption.setOnClickListener {
            iOrderInfo.setOnClickItemMore(position, orderInfo.id)
        }
    }

    override fun getItemCount(): Int {
        return orderInfoList.size
    }

    fun setAdapter(orderList: ArrayList<ResponseOrder>) {
        orderInfoList.clear()
        orderInfoList.addAll(orderList)
        notifyDataSetChanged()
    }

    fun clearAdapterList() {
        orderInfoList.clear()
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        orderInfoList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getOrderPhoneNum(position: Int): String{
        return orderInfoList[position].phoneNum
    }
}