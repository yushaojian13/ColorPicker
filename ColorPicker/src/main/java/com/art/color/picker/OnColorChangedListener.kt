package com.art.color.picker

interface OnColorChangedListener {
    fun onColorChanged(color: Int)
    fun onColorPicked(color: Int) = Unit
}