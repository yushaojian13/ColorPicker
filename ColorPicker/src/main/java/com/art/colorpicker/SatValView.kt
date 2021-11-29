package com.art.colorpicker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class SatValView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in 0 until width) {
            for (j in 0 until height) {
                val x = i.toFloat()
                val y = j.toFloat()
                paint.color = Color.HSVToColor(floatArrayOf(0f, x / width, 1 - y / height))
                canvas.drawPoint(x, y, paint)
            }
        }
    }
}