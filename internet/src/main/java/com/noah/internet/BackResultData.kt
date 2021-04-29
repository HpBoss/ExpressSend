package com.noah.internet

import android.util.Log

data class BackResultData<T>(val data: T, val msg: String, val resultCode: Int) {
    companion object {
        const val CODE_SUCCESS = 200
    }

    fun getResultData(): T? {
        return if (CODE_SUCCESS == resultCode) {
            data
        } else {
            Log.e("exception", resultCode.toString())
            null
        }
    }
}