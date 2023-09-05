package com.art.color.picker.demo

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import com.art.color.picker.OnAlphaChangedListener
import com.art.color.picker.demo.databinding.ActivityMainBinding
import com.art.color.picker.hsv.OnHueChangedListener
import com.art.color.picker.hsv.OnSaturationChangedListener
import com.art.color.picker.hsv.OnValueChangedListener
import java.util.Locale

class MainActivity : Activity() {

    private val binding by lazy(LazyThreadSafetyMode.PUBLICATION) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.alphaSlideView.setOnAlphaChangedListener(object : OnAlphaChangedListener {
            override fun onAlphaChanged(alpha: Int) {
                onColorChanged(alpha, Color.HSVToColor(floatArrayOf(binding.hueSlideView.getHue(), binding.satSlideView.getSaturation(), binding.valueSlideView.getValue())))
            }

            override fun onAlphaPicked(alpha: Int) {
            }
        })

        binding.hueSlideView.hueChangedListener = object : OnHueChangedListener {
            override fun onHueChanged(hue: Float) {
                binding.satSlideView.setHue(hue)
                binding.valueSlideView.setHue(hue)
                val color = Color.HSVToColor(floatArrayOf(hue, binding.satSlideView.getSaturation(), binding.valueSlideView.getValue()))
                binding.alphaSlideView.setMaskColor(color)
                onColorChanged(binding.alphaSlideView.getCurrentAlpha(), color)
            }
        }

        binding.satSlideView.saturationChangedListener = object : OnSaturationChangedListener {
            override fun onSaturationChanged(saturation: Float) {
                binding.valueSlideView.setSaturation(saturation)
                val color = Color.HSVToColor(floatArrayOf(binding.hueSlideView.getHue(), saturation, binding.valueSlideView.getValue()))
                binding.alphaSlideView.setMaskColor(color)
                onColorChanged(binding.alphaSlideView.getCurrentAlpha(), color)
            }
        }

        binding.valueSlideView.valueChangedListener = object : OnValueChangedListener {
            override fun onValueChanged(value: Float) {
                binding.satSlideView.setValue(value)
                val color = Color.HSVToColor(floatArrayOf(binding.hueSlideView.getHue(), binding.satSlideView.getSaturation(), value))
                binding.alphaSlideView.setMaskColor(color)
                onColorChanged(binding.alphaSlideView.getCurrentAlpha(), color)
            }
        }

        onColorChanged(binding.alphaSlideView.getCurrentAlpha(), Color.HSVToColor(floatArrayOf(binding.hueSlideView.getHue(), binding.satSlideView.getSaturation(), binding.valueSlideView.getValue())))
    }

    private fun onColorChanged(alpha: Int, color: Int) {
        val mixedColor = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
        binding.colorTV.setBackgroundColor(mixedColor)
        binding.colorTV.text = String.format(Locale.ENGLISH, "%X", mixedColor)
    }
}