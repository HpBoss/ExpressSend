package com.noah.express_send.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.jhworks.library.core.vo.MediaUiConfigVo
import com.jhworks.library.engine.IEngine

class GlideEngine : IEngine {
    override fun loadDetailImage(imageView: ImageView, uiConfig: MediaUiConfigVo) {

    }

    override fun loadImage(imageView: ImageView, uiConfig: MediaUiConfigVo) {
        Glide.with(imageView)
                .load(uiConfig.path)
                .placeholder(uiConfig.placeholderResId)
                .error(uiConfig.errorResId)
                .override(uiConfig.width, uiConfig.height)
                .centerCrop()
                .into(imageView)
    }
}