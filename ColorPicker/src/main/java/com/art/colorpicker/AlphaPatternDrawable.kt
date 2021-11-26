package com.art.colorpicker

import android.graphics.*
import android.graphics.drawable.Drawable

/**
 * This drawable will draw a simple white and gray chessboard pattern.
 * It's the pattern you will often see as a background behind a partly transparent image in many applications.
 */
class AlphaPatternDrawable(rectangleSize: Int) : Drawable() {

    private val alphaPatternPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        shader = createAlphaPatternShader(rectangleSize * 2)
    }

    override fun draw(canvas: Canvas) {
        val bounds = RectF(bounds)
        canvas.translate(bounds.left, bounds.top)
        canvas.drawRect(0f, 0f, bounds.width(), bounds.height(), alphaPatternPaint)
        canvas.translate(-bounds.left, -bounds.top)
    }

    override fun setAlpha(alpha: Int) {
        throw UnsupportedOperationException("Alpha is not supported by this drawable.")
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        throw UnsupportedOperationException("ColorFilter is not supported by this drawable.")
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }
}