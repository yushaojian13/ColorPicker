package com.art.color.picker

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import com.art.util.dp

const val WHITE_COLOR: Int = -0x1 // 0xFFFFFFFF
const val GRAY_COLOR: Int = -0x343435 // 0xFFCBCBCB
const val COLOR_LUMP_SIZE = 10

fun createAlphaPatternShader(): Shader {
    return createAlphaPatternShader((COLOR_LUMP_SIZE.dp + 0.5f).toInt())!!
}

fun createAlphaPatternShader(size: Int): Shader? {
    val alphaBitmap = createAlphaPatternBitmap(size) ?: return null
    return BitmapShader(alphaBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
}

fun createAlphaPatternBitmap(size: Int): Bitmap? {
    if (size <= 0) return null

    val alphaPatternPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val half = size / 2f
    for (i in 0..1)
        for (j in 0..1) {
            if ((i + j) % 2 == 0)
                alphaPatternPaint.color = GRAY_COLOR
            else
                alphaPatternPaint.color = WHITE_COLOR
            canvas.drawRect((i * half), (j * half), ((i + 1) * half), ((j + 1) * half), alphaPatternPaint)
        }
    return bitmap
}