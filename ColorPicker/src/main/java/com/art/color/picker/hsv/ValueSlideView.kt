package com.art.color.picker.hsv

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import com.art.color.picker.SlideView

class ValueSlideView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : SlideView(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var bitmapBuffer: Bitmap? = null // 提升绘制性能

    private var hue = 0f
    private var saturation = 1f

    private val clipPath = Path()

    var valueChangedListener: OnValueChangedListener? = null

    init {
        ratio = 1f
    }

    fun setHue(hue: Float) {
        this.hue = hue.coerceIn(0f, 360f)
        invalidateCache()
    }

    fun setSaturation(saturation: Float) {
        this.saturation = saturation.coerceIn(0f, 1f)
        invalidateCache()
    }

    fun setValue(value: Float) {
        ratio = value.coerceIn(0f, 1f) / 1f
        invalidate()
    }

    fun getValue() = ratio

    override fun onContentRectChanged() {
        invalidateCache()

        clipPath.rewind()
        clipPath.addRoundRect(contentRectF, 20f, 20f, Path.Direction.CW)
    }

    private fun invalidateCache() {
        if (contentRectF.width() < 360 || contentRectF.height() <= 0) { // 要求至少360像素宽
            bitmapBuffer = null
            return
        }

        bitmapBuffer?.recycle()

        val width = contentRectF.width()
        val height = contentRectF.height()
        val bitmap = Bitmap.createBitmap((width + 0.5f).toInt(), (height + 0.5f).toInt(), Bitmap.Config.ARGB_8888)
        val hueCanvas = Canvas(bitmap)

        var x = 0f
        while (x < width) {
            // H fixed to 0, S fixed to 1, V varies from [0, 1]
            // i -> [0, colorsCount - 1]
            // V -> [0, 1]
            paint.color = Color.HSVToColor(floatArrayOf(hue, saturation, 1f / (width - 1) * x))
            hueCanvas.drawLine(x, 0f, x, height, paint)
            x += 1
        }

        bitmapBuffer = bitmap

        invalidate()
    }

    override fun drawContents(canvas: Canvas) {
        val bitmap = bitmapBuffer
        if (bitmap == null || bitmap.isRecycled) return

        canvas.save()
        canvas.clipPath(clipPath)
        canvas.drawBitmap(bitmap, contentRectF.left, contentRectF.top, null)
        canvas.restore()
    }

    override fun onRatioChanged(ratio: Float) {
        // ratio: 0 -> 1, value: 0 -> 1
        valueChangedListener?.onValueChanged(ratio)
    }

    override fun onRatioPicked(ratio: Float) {
        // ratio: 0 -> 1, value: 0 -> 1
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