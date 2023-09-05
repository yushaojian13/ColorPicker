package com.art.color.picker.hsv

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import com.art.color.picker.SlideView

class HueSlideView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : SlideView(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var colorSegments: IntArray? = null

    private val clipPath = Path()

    var hueChangedListener: OnHueChangedListener? = null

    fun setHue(hue: Float) {
        ratio = hue.coerceIn(0f, 360f) / 360f
        invalidate()
    }

    fun getHue() = ratio * 360f

    override fun onContentRectChanged() {
        if (contentRectF.width() < 360) { // 要求至少360像素宽
            colorSegments = null
            return
        }

        val colorsCount = contentRectF.width().toInt()
        colorSegments = IntArray(colorsCount) { i ->
            // S,V fixed to 1, H varies from [0, 360)
            // i -> [0, colorsCount - 1]
            // H -> [0, 360]
            Color.HSVToColor(floatArrayOf(360f / (colorsCount - 1) * i, 1f, 1f))
        }

        clipPath.rewind()
        clipPath.addRoundRect(contentRectF, 20f, 20f, Path.Direction.CW)
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
        hueChangedListener?.onHueChanged(ratio * 360f)
    }

    override fun onRatioPicked(ratio: Float) {
        // ratio: 0 -> 1, hue: 0 -> 360
        hueChangedListener?.onHuePicked(ratio * 360f)
    }

}

interface OnHueChangedListener {
    /**
     * @param hue [0, 360]
     */
    fun onHueChanged(hue: Float)

    /**
     * @param hue [0, 360]
     */
    fun onHuePicked(hue: Float) = Unit
}