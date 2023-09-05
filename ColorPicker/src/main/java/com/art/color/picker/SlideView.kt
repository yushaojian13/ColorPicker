package com.art.color.picker

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

abstract class SlideView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    protected var contentRectF = RectF()
    private var viewWidth = 0f
    private var viewHeight = 0f

    private val thumbBitmap = BitmapFactory.decodeResource(resources, R.drawable.slide_thumb)
    private var thumbX = 0f
    private var thumbY = 0f
    private var thumbWidth = thumbBitmap.width
    private var maxThumbSlideDistance = 0f
    private var previousX = 0f

    protected var ratio = 0f
        set(value) {
            field = value
            thumbX = ratio * maxThumbSlideDistance
            onRatioChanged(ratio)
            postInvalidate()
        }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 0.5f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w.toFloat()
        viewHeight = h.toFloat()
        maxThumbSlideDistance = viewWidth - thumbWidth
        thumbX = ratio * maxThumbSlideDistance
        thumbY = h / 2f - thumbBitmap.height / 2f
        val halfThumbWidth = thumbWidth / 2f
        contentRectF.set(halfThumbWidth, h * 0.3f, viewWidth - halfThumbWidth, h * 0.7f)
        onContentRectChanged()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawContents(canvas)
        drawBorder(canvas)
        drawThumb(canvas)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val insideThumb = x in thumbX..thumbX + thumbWidth
                if (insideThumb) {
                    previousX = x
                }
                parent.requestDisallowInterceptTouchEvent(true)
                return insideThumb
            }

            MotionEvent.ACTION_MOVE -> {
                val distance = event.x - previousX
                var result = thumbX + distance
                if (result < 0f) {
                    result = 0f
                } else if (result > maxThumbSlideDistance) {
                    result = maxThumbSlideDistance
                }
                thumbX = result
                previousX = event.x
                postInvalidate()
                ratio = result / maxThumbSlideDistance
                onRatioChanged(ratio)
            }

            MotionEvent.ACTION_UP -> {
                onRatioPicked(ratio)
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return true
    }

    private fun drawThumb(canvas: Canvas) {
        canvas.drawBitmap(thumbBitmap, thumbX, thumbY, paint)
    }

    private fun drawBorder(canvas: Canvas) {
        contentRectF.inset(-1f, -1f)
        canvas.drawRoundRect(contentRectF, 20f, 20f, paint)
        contentRectF.inset(1f, 1f)
    }

    abstract fun onContentRectChanged()

    abstract fun drawContents(canvas: Canvas)

    abstract fun onRatioChanged(ratio: Float)

    abstract fun onRatioPicked(ratio: Float)
}