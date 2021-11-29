package com.art.colorpicker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class RectHueView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var hueColors: IntArray? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        hueColors = IntArray(w) { i ->
            // S,V fixed to 1, H varies from [0, 360)
            Color.HSVToColor(floatArrayOf(360f / w * i, 1f, 1f))
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val colors = hueColors ?: return
        for (i in colors.indices) {
            paint.color = colors[i]
            val x = i.toFloat()
            canvas.drawLine(x, 0f, x, height.toFloat(), paint)
        }
    }
}