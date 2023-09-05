package com.art.color.picker

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet

class AlphaSlideView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    SlideView(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val alphaPatternShader: Shader = createAlphaPatternShader() // 灰白相间的色块
    private var maskColor: Int = 0xFF000000.toInt()
    private var maskShader: Shader? = null // 渐变色条

    private var alphaChangedListener: OnAlphaChangedListener? = null

    init {
        ratio = 0f
    }

    fun setOnAlphaChangedListener(listener: OnAlphaChangedListener) {
        this.alphaChangedListener = listener
    }

    fun setMaskColor(color: Int) {
        maskColor = color
        resetMaskShader()
        postInvalidate()
    }

    fun getCurrentAlpha() = ((1 - ratio) * 255).toInt()

    override fun onContentRectChanged() {
        resetMaskShader()
    }

    private fun resetMaskShader() {
        val startColor = maskColor
        val endColor = maskColor and 0xFFFFFF
        maskShader =
            LinearGradient(contentRectF.left, contentRectF.top, contentRectF.right, contentRectF.top, startColor, endColor, Shader.TileMode.CLAMP)
    }

    override fun drawContents(canvas: Canvas) {
        paint.shader = alphaPatternShader
        canvas.drawRoundRect(contentRectF, 20f, 20f, paint)
        paint.shader = maskShader
        canvas.drawRoundRect(contentRectF, 20f, 20f, paint)
        paint.shader = null
    }

    override fun onRatioChanged(ratio: Float) {
        alphaChangedListener?.onAlphaChanged(getCurrentAlpha())
    }

    override fun onRatioPicked(ratio: Float) {
        alphaChangedListener?.onAlphaPicked(getCurrentAlpha())
    }

}

interface OnAlphaChangedListener {
    fun onAlphaChanged(alpha: Int)
    fun onAlphaPicked(alpha: Int)
}