package com.art.color.picker.hsv

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import com.art.color.picker.SlideView

class ValueSlideView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : SlideView(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var colorSegments: IntArray? = null

    private var hue = 0f
    private var saturation = 1f

    private val clipPath = Path()

    var valueChangedListener: OnValueChangedListener? = null

    init {
        ratio = 1f
    }

    fun setHue(hue: Float) {
        this.hue = hue.coerceIn(0f, 360f)
        reComputeColorSegments()
        invalidate()
    }

    fun setSaturation(saturation: Float) {
        this.saturation = saturation.coerceIn(0f, 1f)
        reComputeColorSegments()
        invalidate()
    }

    fun setValue(value: Float) {
        ratio = value.coerceIn(0f, 1f) / 1f
    }

    fun getValue() = ratio

    override fun onContentRectChanged() {
        reComputeColorSegments()

        clipPath.rewind()
        clipPath.addRoundRect(contentRectF, 20f, 20f, Path.Direction.CW)
    }

    private fun reComputeColorSegments() {
        if (contentRectF.width() < 360) { // 要求至少360像素宽
            colorSegments = null
            return
        }

        val colorsCount = contentRectF.width().toInt()
        colorSegments = IntArray(colorsCount) { i ->
            // H fixed to 0, V fixed to 1, S varies from [0, 1]
            // i -> [0, colorsCount - 1]
            // S -> [0, 1]
            Color.HSVToColor(floatArrayOf(hue, saturation, 1f / (colorsCount - 1) * i))
        }
    }

    override fun drawContents(canvas: Canvas) {
        val colors = colorSegments ?: return

        canvas.save()
        canvas.clipPath(clipPath)
        for (i in colors.indices) {
            paint.color = colors[i]
            val x = contentRectF.left + i.toFloat()
            canvas.drawLine(x, contentRectF.top, x, contentRectF.bottom, paint)
        }
        canvas.restore()
    }

    override fun onRatioChanged(ratio: Float) {
        // ratio: 0 -> 1, hue: 0 -> 360
        valueChangedListener?.onValueChanged(ratio)
    }

    override fun onRatioPicked(ratio: Float) {
        // ratio: 0 -> 1, hue: 0 -> 360
        valueChangedListener?.onValuePicked(ratio)
    }

}

interface OnValueChangedListener {
    /**
     * @param value [0, 1]
     */
    fun onValueChanged(value: Float)

    /**
     * @param value [0, 1]
     */
    fun onValuePicked(value: Float) = Unit
}