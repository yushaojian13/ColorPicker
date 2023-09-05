package com.art.color.picker.demo

import android.app.Activity
import android.os.Bundle
import com.art.color.picker.OnColorChangedListener
import com.art.color.picker.demo.databinding.ActivityMainBinding
import java.util.Locale
import kotlin.random.Random

class MainActivity : Activity() {

    private val binding by lazy(LazyThreadSafetyMode.PUBLICATION) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.hsvSlideView.colorChangedListener = object : OnColorChangedListener {
            override fun onColorChanged(color: Int) {
                binding.colorTV.setBackgroundColor(color)
                binding.colorTV.text = String.format(Locale.ENGLISH, "%X", color)
            }
        }

        binding.hsvSlideView.color = Random.nextInt()
    }

}