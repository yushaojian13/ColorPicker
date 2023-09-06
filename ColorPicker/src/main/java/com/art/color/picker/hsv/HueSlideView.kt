package com.art.color.picker.hsv

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import com.art.color.picker.SlideView

class HueSlideView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : SlideView(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var bitmapBuffer: Bitmap? = null // 提升绘制性能

    private val clipPath = Path()

    var hueChangedListener: OnHueChangedListener? = null

    fun setHue(hue: Float) {
        ratio = hue.coerceIn(0f, 360f) / 360f
        invalidate()
    }

    fun getHue() = ratio * 360f

    override fun onContentRectChanged() {
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
            // S,V fixed to 1, H varies from [0, 360]
            // i -> [0, colorsCount - 1]
            // H -> [0, 360]
            paint.color = Color.HSVToColor(floatArrayOf(360f / (width - 1) * x, 1f, 1f))
            hueCanvas.drawLine(x, 0f, x, height, paint)
            x += 1
        }

        bitmapBuffer = bitmap

        clipPath.rewind()
        clipPath.addRoundRect(contentRectF, 20f, 20f, Path.Direction.CW)
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