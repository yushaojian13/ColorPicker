package com.art.color.picker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.art.util.dp

class AlphaPatternView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private val ratio = 1f
    private var viewWidth = 0f
    private var viewHeight = 0f
    private val alphaPatternPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        alphaPatternPaint.shader = createAlphaPatternShader((18.dp + 0.5f).toInt()) // 灰白相间的色块
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthSpec = widthMeasureSpec
        var heightSpec = heightMeasureSpec
        val widthMode = MeasureSpec.getMode(widthSpec)
        val heightMode = MeasureSpec.getMode(heightSpec)
        var width = MeasureSpec.getSize(widthSpec) - paddingLeft - paddingRight
        var height = MeasureSpec.getSize(heightSpec) - paddingTop - paddingBottom
        if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY && ratio > 0f) {
            height = (width / ratio + 0.5f).toInt()
            heightSpec = MeasureSpec.makeMeasureSpec(height + paddingTop + paddingBottom, MeasureSpec.EXACTLY)
        } else if (widthMode != MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY && ratio > 0f) {
            width = (height * ratio + 0.5f).toInt()
            widthSpec = MeasureSpec.makeMeasureSpec(width + paddingLeft + paddingRight, MeasureSpec.EXACTLY)
        }
        setMeasuredDimension(widthSpec, heightSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        viewWidth = (bottom - top).toFloat()
        viewHeight = (right - left).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, viewWidth, viewHeight, alphaPatternPaint)
    }

}