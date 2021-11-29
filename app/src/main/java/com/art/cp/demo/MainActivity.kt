package com.art.cp.demo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.art.colorpicker.ColorPickView
import com.art.colorpicker.ColorPickerView
import com.art.colorpicker.HSVColorPickerView
import com.art.colorpicker.RectColorPickerView
import java.util.*

class MainActivity : AppCompatActivity(), ColorPickView.OnColorChangedListener, ColorPickerView.OnColorChangedListener {

    private lateinit var colorPickerView: ColorPickerView
    private lateinit var rectColorPickerView: RectColorPickerView
    private lateinit var colorTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        colorTV = findViewById(R.id.colorTV)
        colorPickerView = findViewById(R.id.colorPickerView)
        rectColorPickerView = findViewById(R.id.rectColorPickerView)

        onColorChanged(colorPickerView.color)
        colorPickerView.setOnColorChangedListener(this)
        rectColorPickerView.onColorChangedListener = this
    }

    override fun onColorChanged(newColor: Int) {
        colorTV.setBackgroundColor(newColor)
        colorTV.text = String.format(Locale.ENGLISH, "%X", newColor)
    }
}