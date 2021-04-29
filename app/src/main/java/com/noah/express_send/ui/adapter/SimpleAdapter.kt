package com.noah.express_send.ui.adapter

import com.noah.express_send.R
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder


/**
 * @Auther: 何飘
 * @Date: 2/23/21 14:50
 * @Description:
 */
class SimpleAdapter() : BaseBannerAdapter<Int>() {
    override fun bindData(
        holder: BaseViewHolder<Int>?,
        data: Int,
        position: Int,
        pageSize: Int
    ) {
        holder?.setImageResource(R.id.banner_image, data)
//        ImageView imageView = holder.findViewById(R.id.banner_image)
//        Glide.with(imageView).load(data.getImagePath()).placeholder(R.drawable.placeholder).into(imageView)
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_banner
    }

}