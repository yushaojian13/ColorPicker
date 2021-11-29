package com.art.colorpicker

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet

abstract class HSVColorPickerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ColorPickView(context, attrs, defStyleAttr) {

    // selected HSV color
    protected var hueSelected = 360f      // [0, 360]
    protected var saturationSelected = 0f // [0, 1]
    protected var valueSelected = 0f      // [0, 1]
    protected var alphaSelected = 255     // [0, 255]

    override val color
        get() = Color.HSVToColor(
            alphaSelected,
            floatArrayOf(hueSelected, saturationSelected, valueSelected)
        )

    protected fun onHueChanged(hue: Float) {
        hueSelected = hue
        onColorChangedListener?.onColorChanged(color)
    }

    protected fun onSatValChanged(sat: Float, value: Float) {
        saturationSelected = sat
        valueSelected = value
        onColorChangedListener?.onColorChanged(color)
    }

    protected fun onAlphaChanged(alpha: Int) {
        alphaSelected = alpha
        onColorChangedListener?.onColorChanged(color)
    }
}