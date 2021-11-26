package com.art.cp.demo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.art.colorpicker.ColorPickerView
import java.util.*

class MainActivity : AppCompatActivity(), ColorPickerView.OnColorChangedListener {

    private lateinit var colorPickerView: ColorPickerView
    private lateinit var colorTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        colorPickerView = findViewById(R.id.colorPickerView)
        colorTV = findViewById(R.id.colorTV)

        onColorChanged(colorPickerView.color)
        colorPickerView.setOnColorChangedListener(this)
    }

    override fun onColorChanged(newColor: Int) {
        colorTV.setBackgroundColor(newColor)
        colorTV.text = String.format(Locale.ENGLISH, "%X", newColor)
    }
}