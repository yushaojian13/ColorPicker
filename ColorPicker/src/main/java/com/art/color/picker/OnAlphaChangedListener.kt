package com.art.color.picker

interface OnAlphaChangedListener {
    fun onAlphaChanged(alpha: Int)
    fun onAlphaPicked(alpha: Int) = Unit
}