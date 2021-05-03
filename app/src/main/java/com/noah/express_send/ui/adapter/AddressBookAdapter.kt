package com.noah.express_send.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.noah.express_send.R
import com.noah.express_send.ui.adapter.io.IAddressOperate
import com.noah.internet.response.ResponseAddressBook
import java.util.ArrayList

/**
 * @Auther: 何飘
 * @Date: 4/29/21 17:09
 * @Description:
 */
class AddressBookAdapter(
    private val iAddressOperate: IAddressOperate
) : RecyclerView.Adapter<AddressBookAdapter.ViewHolder>() {
    private val addressList = ArrayList<ResponseAddressBook>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSchoolName: TextView = view.findViewById(R.id.tv_schoolName)
        val tvDetailAddress: TextView = view.findViewById(R.id.tv_detailAddressName)
        val btnEditAddress: TextView = view.findViewById(R.id.tv_btnEdit)
        val btnDelete: TextView = view.findViewById(R.id.tv_btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_address,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val addressInfo = addressList[position]
        holder.tvSchoolName.text = addressInfo.schoolName
        holder.tvDetailAddress.text = addressInfo.detailName
        holder.btnDelete.setOnClickListener {
            iAddressOperate.deleteAddress(position, addressInfo.id)
        }
        holder.btnEditAddress.setOnClickListener {
            iAddressOperate.editAddress(position, addressInfo)
        }
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    fun setAdapter(addressList: ArrayList<ResponseAddressBook>) {
        this.addressList.clear()
        this.addressList.addAll(addressList)
        notifyDataSetChanged()
    }

    fun removeAt(position: Int): Int {
        addressList.removeAt(position)
        notifyItemRemoved(position)
        return addressList.size
    }
}