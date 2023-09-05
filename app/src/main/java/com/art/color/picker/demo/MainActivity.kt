package com.art.color.picker.demo

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import com.art.color.picker.OnAlphaChangedListener
import com.art.color.picker.OnColorChangedListener
import com.art.color.picker.demo.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : Activity() {

    private val binding by lazy(LazyThreadSafetyMode.PUBLICATION) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.colorSlideView.setOnColorChangedListener(object : OnColorChangedListener {
            override fun onColorChanged(color: Int) {
                binding.alphaSlideView.setMaskColor(color)

                onColorChanged(binding.alphaSlideView.getCurrentAlpha(), color)
            }

            override fun onColorPicked(color: Int) {
            }
        })

        binding.alphaSlideView.setOnAlphaChangedListener(object : OnAlphaChangedListener {
            override fun onAlphaChanged(alpha: Int) {
                onColorChanged(alpha, binding.colorSlideView.pickedColor)
            }

            override fun onAlphaPicked(alpha: Int) {
            }
        })

        onColorChanged(binding.alphaSlideView.getCurrentAlpha(), binding.colorSlideView.pickedColor)
    }

    private fun onColorChanged(alpha: Int, color: Int) {
        val mixedColor = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
        binding.colorTV.setBackgroundColor(mixedColor)
        binding.colorTV.text = String.format(Locale.ENGLISH, "%X", mixedColor)
    }
}