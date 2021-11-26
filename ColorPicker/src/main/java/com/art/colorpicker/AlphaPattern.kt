package com.art.colorpicker

import android.graphics.*

const val WHITE_COLOR: Int = -0x1 // 0xFFFFFFFF
const val GRAY_COLOR: Int = -0x343435 // 0xFFCBCBCB

fun createAlphaPatternShader(size: Int): Shader {
    val alphaBitmap = createAlphaPatternBitmap(size)
    return BitmapShader(alphaBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
}

fun createAlphaPatternBitmap(size: Int): Bitmap {
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