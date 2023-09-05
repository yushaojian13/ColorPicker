package com.art.color.picker

import android.content.Context
import android.graphics.*
import android.util.AttributeSet

class ColorSlideView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : SlideView(context, attrs, defStyleAttr) {

    private val colors = intArrayOf(0xFF000000.toInt(), 0xFF002BFF.toInt(), 0xFFE900FF.toInt(), 0xFFFF0000.toInt(), 0xFFFF8F00.toInt(), 0xFFF8FF00.toInt(), 0xFF00FF44.toInt(), 0xFF00FFDD.toInt(), 0xFF00D8FF.toInt(), 0xFFFFFFFF.toInt())
    private val positions = floatArrayOf(0f, 0.15f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1f)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var linearGradient: LinearGradient? = null

    private var colorChangedListener: OnColorChangedListener? = null

    fun setOnColorChangedListener(listener: OnColorChangedListener) {
        this.colorChangedListener = listener
    }

    init {
        ratio = 0f
    }

    override fun onContentRectChanged() {
        linearGradient = LinearGradient(contentRectF.left, contentRectF.top, contentRectF.right, contentRectF.top, colors, positions, Shader.TileMode.CLAMP)
    }

    override fun drawContents(canvas: Canvas) {
        paint.shader = linearGradient
        canvas.drawRoundRect(contentRectF, 20f, 20f, paint)
        paint.shader = null
    }

    override fun onRatioChanged(ratio: Float) {
        colorChangedListener?.onColorChanged(getColor(ratio))
    }

    override fun onRatioPicked(ratio: Float) {
        colorChangedListener?.onColorPicked(getColor(ratio))
    }

    val pickedColor: Int get() = getColor(ratio)

    private fun getColor(ratio: Float): Int {
        val size = positions.size
        if (ratio <= 0f) {
            return colors[0]
        }
        if (ratio >= 1f) {
            return colors[size - 1]
        }
        for (i in 0 until size) {
            if (ratio <= positions[i]) {
                val areaRatio = getAreaRadio(ratio, positions[i - 1], positions[i])
                return getColorFrom(colors[i - 1], colors[i], areaRatio)
            }
        }
        return -1
    }

    private fun getAreaRadio(ratio: Float, startPosition: Float, endPosition: Float): Float {
        return (ratio - startPosition) / (endPosition - startPosition)
    }

    private fun getColorFrom(startColor: Int, endColor: Int, radio: Float): Int {
        val redStart = Color.red(startColor)
        val blueStart = Color.blue(startColor)
        val greenStart = Color.green(startColor)
        val redEnd = Color.red(endColor)
        val blueEnd = Color.blue(endColor)
        val greenEnd = Color.green(endColor)

        val red = (redStart + ((redEnd - redStart) * radio + 0.5)).toInt()
        val greed = (greenStart + ((greenEnd - greenStart) * radio + 0.5)).toInt()
        val blue = (blueStart + ((blueEnd - blueStart) * radio + 0.5)).toInt()
        return Color.argb(255, red, greed, blue)
    }

}