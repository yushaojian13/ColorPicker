package com.art.colorpicker

import android.content.Context
import android.util.AttributeSet
import android.view.View

abstract class ColorPickView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    abstract val color: Int
    var onColorChangedListener: OnColorChangedListener? = null

    interface OnColorChangedListener {
        fun onColorChanged(newColor: Int)
    }
}