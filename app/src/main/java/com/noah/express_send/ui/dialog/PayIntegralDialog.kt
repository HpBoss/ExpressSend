package com.noah.express_send.ui.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import com.noah.express_send.R
import kotlinx.android.synthetic.main.dialog_pay_integral.*

/**
 * @Auther: 何飘
 * @Date: 4/24/21 14:52
 * @Description:
 */
class PayIntegralDialog constructor(
    context: Context,
    themeResId: Int,
    private var maxValue: Int?,
    private val value: Int?
) :
    Dialog(context, themeResId) {

    private var clickCompleteInputIntegral: IClickCompleteInputIntegral? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_pay_integral)
        val window = this.window!!
        window.setGravity(Gravity.BOTTOM)
        window.setWindowAnimations(R.style.main_menu_animStyle)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        initListenSlide()
    }

    private fun initListenSlide() {
        if (maxValue == 0) maxValue = 5
        slider.valueTo = maxValue!!.toFloat()
        slider.value = value!!.toFloat()
        res_payIntegral.text = value.toInt().toString()
        slider.addOnChangeListener { _, value, _ ->
            res_payIntegral.text = value.toInt().toString()
        }

        tv_cancelIntegral.setOnClickListener {
            dismiss()
        }

        tv_completeIntegral.setOnClickListener {
            clickCompleteInputIntegral?.clickComplete(slider.value.toInt())
            dismiss()
        }
    }

    fun setIClickCompleteInputIntegral(iClickCompleteInputIntegral: IClickCompleteInputIntegral) {
        this.clickCompleteInputIntegral = iClickCompleteInputIntegral
    }

    interface IClickCompleteInputIntegral {
        fun clickComplete(integralNum: Int)
    }
}