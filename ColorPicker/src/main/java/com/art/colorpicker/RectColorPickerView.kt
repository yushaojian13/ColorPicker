package com.art.colorpicker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent

class RectColorPickerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : HSVColorPickerView(context, attrs, defStyleAttr) {

    private val satValRect = RectF(0f, 0f, 0f, 0f) // x: sat [0, 1], y: val [0, 1]
    private val hueRect = RectF(0f, 0f, 0f, 0f)    // y: hue [0, 360]
    private val alphaRect = RectF(0f, 0f, 0f, 0f)

    private val satValPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val satValTrackerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val huePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val hueTrackerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val alphaPatternPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val alphaMaskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val alphaTrackerPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var hueOfSatValShader = -1f
    private var hueBitmap: Bitmap? = null

    // TODO: 2021/11/29 配置
    private val alphaRadius = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val satValRatio = 0.875f
        val satValSpaceRatio = 0.9f
        satValRect.set(0f, 0f, w * satValRatio, h * satValRatio)
        hueRect.set(w * satValSpaceRatio, 0f, w.toFloat(), h.toFloat() * satValRatio)
        alphaRect.set(0f, h * satValSpaceRatio, w.toFloat(), h.toFloat())
        alphaPatternPaint.shader = createAlphaPatternShader((alphaRect.height() / 5).toInt() * 2)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val start = SystemClock.uptimeMillis()

        drawSatVal(canvas)
        drawHue(canvas)
        drawAlpha(canvas)

        val elapsed = SystemClock.uptimeMillis() - start
        Log.i("ColorPicker", "RectColorPickerView onDraw: elapsed $elapsed")
    }

    private fun drawSatVal(canvas: Canvas) {
        val width = satValRect.width()
        val height = satValRect.height()
        if (width <= 0f || height <= 0f) return

        if (hueOfSatValShader != hueSelected) {
            hueOfSatValShader = hueSelected

            val left = satValRect.left
            val right = satValRect.right
            val top = satValRect.top
            val bottom = satValRect.bottom

            val valShader = LinearGradient(left, top, left, bottom, Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP)
            val color = Color.HSVToColor(floatArrayOf(hueSelected, 1f, 1f))
            val satShader = LinearGradient(left, top, right, top, Color.WHITE, color, Shader.TileMode.CLAMP)
            satValPaint.shader = ComposeShader(valShader, satShader, PorterDuff.Mode.MULTIPLY)
        }

        canvas.drawRect(satValRect, satValPaint)

        satValTrackerPaint.apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 2.dp.toFloat()
        }

        val cx = width * saturationSelected
        val cy = height * (1 - valueSelected)
        canvas.drawCircle(cx, cy, 5.dp.toFloat(), satValTrackerPaint)
    }

    private fun drawHue(canvas: Canvas) {
        val width = hueRect.width()
        val height = hueRect.height()
        if (width <= 0f || height <= 0f) return

        if (hueBitmap == null) {
            val bitmap = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
            val hueCanvas = Canvas(bitmap)

            var y = 0f
            while (y < height) {
                huePaint.color = Color.HSVToColor(floatArrayOf(360f / height * y, 1f, 1f))
                hueCanvas.drawLine(0f, y, width, y, huePaint)
                y += 1
            }

            hueBitmap = bitmap
        }

        hueBitmap?.let { canvas.drawBitmap(it, null, hueRect, null) }

        val trackerPosition = hueRect.top + height * hueSelected / 360f
        Log.d("ColorPicker", "hueSelected $hueSelected height $height trackerPosition $trackerPosition")
        hueTrackerPaint.apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 4.dp.toFloat()
        }
        canvas.drawLine(hueRect.left, trackerPosition, hueRect.right, trackerPosition, hueTrackerPaint)
    }

    private fun drawAlpha(canvas: Canvas) {
        val bounds = alphaRect
        canvas.translate(bounds.left, bounds.top)
        canvas.drawRoundRect(0f, 0f, bounds.width(), bounds.height(), alphaRadius, alphaRadius, alphaPatternPaint)
        canvas.translate(-bounds.left, -bounds.top)

        val hsv = floatArrayOf(hueSelected, saturationSelected, valueSelected)
        val rgb = Color.HSVToColor(hsv)
        val argb = Color.HSVToColor(0, hsv)
        alphaMaskPaint.shader = LinearGradient(
            alphaRect.left,
            alphaRect.top,
            alphaRect.right,
            alphaRect.bottom,
            rgb,
            argb,
            Shader.TileMode.CLAMP
        )
        canvas.drawRoundRect(alphaRect, alphaRadius, alphaRadius, alphaMaskPaint)

        val trackerPosition = bounds.left + bounds.width() * (255 - alphaSelected).toFloat() / 255
        alphaTrackerPaint.apply {
            color = rgb
            style = Paint.Style.STROKE
            strokeWidth = 4.dp.toFloat()
        }
        canvas.drawLine(trackerPosition, alphaRect.top, trackerPosition, alphaRect.bottom, alphaTrackerPaint)
    }

    private var actionDownX = 0f
    private var actionDownY = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                actionDownX = event.x
                actionDownY = event.y
                return handleEvent(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                return handleEvent(event.x, event.y)
            }
        }
        return super.onTouchEvent(event)
    }

    private fun handleEvent(x: Float, y: Float): Boolean {
        if (satValRect.contains(actionDownX, actionDownY)) {
            val pointF = pointToSatVal(x, y)
            onSatValChanged(pointF.x, pointF.y)
            invalidate()
            return true
        }
        if (hueRect.contains(actionDownX, actionDownY)) {
            onHueChanged(pointToHue(y))
            invalidate()
            return true
        }
        if (alphaRect.contains(actionDownX, actionDownY)) {
            onAlphaChanged(pointToAlpha(x))
            invalidate()
            return true
        }
        return false
    }

    private fun pointToSatVal(x: Float, y: Float): PointF {
        val rect = satValRect
        val relativeX = when {
            x < rect.left -> 0f
            x > rect.right -> rect.width()
            else -> x - rect.left
        }
        val relativeY = when {
            y < rect.top -> 0f
            y > rect.bottom -> rect.height()
            else -> y - rect.top
        }
        return PointF(relativeX / rect.width(), 1 - relativeY / rect.height())
    }

    private fun pointToHue(y: Float): Float {
        val rect = hueRect
        if (y < rect.top) return 0f
        if (y > rect.bottom) return 360f
        return (y - rect.top) / rect.height() * 360
    }

    private fun pointToAlpha(x: Float): Int {
        val rect = alphaRect
        if (x < rect.left) return 255
        if (x > rect.right) return 0
        return (255 - (x - rect.left) / rect.width() * 255).toInt()
    }
}