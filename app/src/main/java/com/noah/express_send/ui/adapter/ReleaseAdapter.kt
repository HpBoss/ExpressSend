package com.noah.express_send.ui.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.view.OptionsPickerView
import com.noah.express_send.R
import com.noah.express_send.bean.ReleaseInfo
import com.noah.express_send.ui.adapter.io.IReleaseInfo

/**
 * @Auther: 何飘
 * @Date: 2/25/21 15:22
 * @Description:
 */
class ReleaseAdapter(
    private val releaseList: List<ReleaseInfo>,
    private val mContext: Context,
    private val iReleaseInfo: IReleaseInfo
)
    : RecyclerView.Adapter<ReleaseAdapter.ViewHolder>() {
    private val options1Items = ArrayList<String>()
    private val optionsExpressName: Array<String> = arrayOf(
        "顺丰", "申通", "中通", "京东", "韵达",
        "天天", "百世", "邮政", "圆通", "其它"
    )
    private val optionsClassify: Array<String> = arrayOf("衣服", "鞋子", "书籍", "数码产品", "生活用品", "其它")

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon : ImageView = view.findViewById(R.id.icon_release)
        val title : TextView = view.findViewById(R.id.tv_item_title)
        val hints : TextView = view.findViewById(R.id.tv_hints)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_recycle_release,
            parent,
            false
        )
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            options1Items.clear()
            val position = viewHolder.adapterPosition
            when(position) {
                0 -> iniOptionsData(optionsExpressName)
                1 -> iniOptionsData(optionsClassify)
                2 -> iReleaseInfo.startKeyBoard()
                3 -> iReleaseInfo.startSetPayIntegralNum()
            }
            if (options1Items.isNotEmpty()) {
                initOptionsPicker(releaseList[position].title, options1Items, position)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val releaseItemInfo = releaseList[position]
        holder.icon.setImageResource(releaseItemInfo.iconImg)
        holder.title.text = releaseItemInfo.title
        holder.hints.text = releaseItemInfo.hints
    }

    override fun getItemCount(): Int {
        return releaseList.size
    }

    private fun iniOptionsData(optionsArrays: Array<String>) {
        for (element in optionsArrays) {
            options1Items.add(element)
        }
    }

    private fun initOptionsPicker(titleName: String, data: ArrayList<String>, position: Int) {
        val pvOptions: OptionsPickerView<String> = OptionsPickerBuilder(mContext) { options1, _, _, _ -> //返回的分别是三个级别的选中位置
            val result = options1Items[options1]
            iReleaseInfo.updateRelease(position, result)
        }
            .setTitleText(titleName)
            .setContentTextSize(20)//设置滚轮文字大小
            .setDividerColor(Color.LTGRAY)//设置分割线的颜色
            .setSelectOptions(0,1)//默认选中项
            .setBgColor(Color.WHITE)
            .setTitleBgColor(ContextCompat.getColor(mContext, R.color.blue_364))
            .setTitleColor(Color.WHITE)
            .setCancelColor(Color.WHITE)
            .setCancelText("取消")
            .setSubmitColor(Color.WHITE)
            .setSubmitText("完成")
            .setTextColorCenter(Color.BLACK)
            .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
            .build<String>()
        pvOptions.setPicker(data)
        pvOptions.show()
    }
}