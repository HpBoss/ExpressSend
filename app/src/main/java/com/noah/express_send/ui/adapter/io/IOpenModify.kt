package com.noah.express_send.ui.adapter.io

interface IOpenModify {
    fun startModifyActivity(title: String, type: Int, columnName: String)
    fun pickImage(isOpenCameraOnly: Boolean)
}