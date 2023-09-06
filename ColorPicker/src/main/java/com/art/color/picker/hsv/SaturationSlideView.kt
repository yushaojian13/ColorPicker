package com.art.color.picker.hsv

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import com.art.color.picker.SlideView

class SaturationSlideView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : SlideView(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var bitmapBuffer: Bitmap? = null // 提升绘制性能

    private var hue = 0f
    private var value = 1f

    private val clipPath = Path()

    var saturationChangedListener: OnSaturationChangedListener? = null

    init {
        ratio = 1f
    }

    fun setHue(hue: Float) {
        this.hue = hue.coerceIn(0f, 360f)
        invalidateCache()
    }

    fun setValue(value: Float) {
        this.value = value.coerceIn(0f, 1f)
        invalidateCache()
    }

    fun setSaturation(sat: Float) {
        ratio = sat.coerceIn(0f, 1f) / 1f
        invalidate()
    }

    fun getSaturation() = ratio

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
            // H fixed to 0, V fixed to 1, S varies from [0, 1]
            // i -> [0, colorsCount - 1]
            // S -> [0, 1]
            paint.color = Color.HSVToColor(floatArrayOf(hue, 1f / (width - 1) * x, value))
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
        // ratio: 0 -> 1, saturation: 0 -> 1
        saturationChangedListener?.onSaturationChanged(ratio)
    }

    override fun onRatioPicked(ratio: Float) {
        // ratio: 0 -> 1, saturation: 0 -> 1
        saturationChangedListener?.onSaturationPicked(ratio)
    }

}

interface OnSaturationChangedListener {
    /**
     * @param saturation [0, 1]
     */
    fun onSaturationChanged(saturation: Float)

    /**
     * @param saturation [0, 1]
     */
    fun onSaturationPicked(saturation: Float) = Unit
}