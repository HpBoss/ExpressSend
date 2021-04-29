package com.noah.pricekeyboard

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener
import android.os.Build
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import java.lang.reflect.Method
import java.util.*

/**
 * @Auther: 何飘
 * @Date: 2/26/21 21:34
 * @Description:
 */
class KeyboardUtil {
    private var mActivity: Activity? = null
    private var mIfRandom = false
    private var mDialog: Dialog

    private var mKeyboardView: PriceKeyBoardView? = null
    private var mKeyboardNumber //数字键盘
            : Keyboard? = null
    private var mEditText: EditText? = null

    constructor(activity: Activity, dialog: Dialog): this(activity, dialog, false)
    constructor(activity: Activity, dialog: Dialog, ifRandom: Boolean) {
        mActivity = activity
        mIfRandom = ifRandom
        mDialog = dialog
        mKeyboardNumber = Keyboard(mActivity, R.xml.keyboardnumber)
        mKeyboardView = dialog.findViewById<View>(R.id.keyboard_view) as PriceKeyBoardView
    }

    /**
     * edittext绑定自定义键盘
     *
     * @param editText 需要绑定自定义键盘的edittext
     */
    fun attachTo(editText: EditText?) {
        mEditText = editText
        mEditText?.requestFocus()
        hideSystemSoftKeyboard(mActivity!!.applicationContext, mEditText)
        showSoftKeyboard()
    }

    private fun showSoftKeyboard() {
        if (mKeyboardNumber == null) {
            mKeyboardNumber = Keyboard(mActivity, R.xml.keyboardnumber)
        }
        if (mKeyboardView == null) {
            mKeyboardView = mDialog.findViewById<View>(R.id.keyboard_view) as PriceKeyBoardView
        }
        if (mIfRandom) {
            randomKeyboardNumber()
        } else {
            mKeyboardView?.setKeyboard(mKeyboardNumber)
        }
        mKeyboardView?.isEnabled = true
        mKeyboardView?.setPreviewEnabled(false)
        mKeyboardView?.visibility = View.VISIBLE
        mKeyboardView?.setOnKeyboardActionListener(mOnKeyboardActionListener)
    }

    private val mOnKeyboardActionListener: OnKeyboardActionListener =
        object : OnKeyboardActionListener {
            override fun onPress(primaryCode: Int) {}
            override fun onRelease(primaryCode: Int) {}
            override fun onKey(primaryCode: Int, keyCodes: IntArray) {
                val editable = mEditText!!.text
                val start = mEditText!!.selectionStart
                if (primaryCode == Keyboard.KEYCODE_DELETE) { // 回退
                    if (editable != null && editable.isNotEmpty()) {
                        if (start > 0) {
                            editable.delete(start - 1, start)
                        }
                    }
                } else if (primaryCode == Keyboard.KEYCODE_CANCEL) { // 隐藏键盘
                    hideKeyboard()
                    if (mIonCancelClick != null) {
                        mIonCancelClick!!.onCancelClick()
                    }
                } else if (primaryCode == Keyboard.KEYCODE_DONE) { // 隐藏键盘
                    if (mOnOkClick != null) {
                        mOnOkClick!!.onOkClick()
                    }
                } else {
                    editable!!.insert(start, primaryCode.toChar().toString())
                }
            }

            override fun onText(text: CharSequence) {}
            override fun swipeLeft() {}
            override fun swipeRight() {}
            override fun swipeDown() {}
            override fun swipeUp() {}
        }


    /**
     * 隐藏系统键盘
     *
     * @param editText
     */
    private fun hideSystemSoftKeyboard(context: Context, editText: EditText?) {
        val sdkInt = Build.VERSION.SDK_INT
        if (sdkInt >= 11) {
            try {
                val cls = EditText::class.java
                val setShowSoftInputOnFocus: Method =
                    cls.getMethod("setShowSoftInputOnFocus", Boolean::class.javaPrimitiveType)
                setShowSoftInputOnFocus.isAccessible = true
                setShowSoftInputOnFocus.invoke(editText, false)
            } catch (e: SecurityException) {
                e.printStackTrace()
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            editText!!.inputType = InputType.TYPE_NULL
        }
        // 如果软键盘已经显示，则隐藏
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText!!.windowToken, 0)
    }

    interface OnOkClick {
        fun onOkClick()
    }

    interface IonCancelClick {
        fun onCancelClick()
    }

    var mOnOkClick: OnOkClick? = null
    var mIonCancelClick: IonCancelClick? = null

    fun setOnOkClick(onOkClick: OnOkClick?) {
        mOnOkClick = onOkClick
    }

    fun setOnCancelClick(IonCancelClick: IonCancelClick?) {
        mIonCancelClick = IonCancelClick
    }


    private fun isNumber(str: String): Boolean {
        val wordstr = "0123456789"
        return wordstr.contains(str)
    }

    private fun randomKeyboardNumber() {
        val keyList = mKeyboardNumber!!.keys
        // 查找出0-9的数字键
        val newkeyList: MutableList<Keyboard.Key> = ArrayList()
        for (i in keyList.indices) {
            if (keyList[i].label != null
                && isNumber(keyList[i].label.toString())
            ) {
                newkeyList.add(keyList[i])
            }
        }
        // 数组长度
        val count = newkeyList.size
        // 结果集
        val resultList: MutableList<KeyModel> = ArrayList<KeyModel>()
        // 用一个LinkedList作为中介
        val temp: LinkedList<KeyModel> = LinkedList<KeyModel>()
        // 初始化temp
        for (i in 0 until count) {
            temp.add(KeyModel(48 + i, i.toString() + ""))
        }
        // 取数
        val rand = Random()
        for (i in 0 until count) {
            val num = rand.nextInt(count - i)
            resultList.add(
                KeyModel(
                    temp[num].code,
                    temp[num].label
                )
            )
            temp.removeAt(num)
        }
        for (i in newkeyList.indices) {
            newkeyList[i].label = resultList[i].label
            newkeyList[i].codes[0] = resultList[i].code
        }
        mKeyboardView?.setKeyboard(mKeyboardNumber)
    }

    fun showKeyboard() {
        val visibility: Int? = mKeyboardView?.visibility
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            mKeyboardView?.visibility = View.VISIBLE
        }
    }

    fun hideKeyboard() {
        val visibility: Int? = mKeyboardView?.visibility
        if (visibility == View.VISIBLE) {
            mKeyboardView?.visibility = View.GONE
        }
    }
}