package com.art.color.picker.hsv

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.Space
import com.art.color.picker.AlphaSlideView
import com.art.color.picker.OnAlphaChangedListener
import com.art.color.picker.OnColorChangedListener
import com.art.color.picker.R
import com.art.util.dp

/**
 * 至少应有120dp高，如果高于120dp，H、S、V、A各30dp，其余空间均匀分布
 */
class HSVSlideView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    private val hueSlideView = HueSlideView(context)
    private val saturationSlideView = SaturationSlideView(context)
    private val valueSlideView = ValueSlideView(context)
    private var alphaSlideView: AlphaSlideView? = null

    var colorChangedListener: OnColorChangedListener? = null

    var color: Int
        get() {
            val alpha = currentAlpha
            val color = Color.HSVToColor(floatArrayOf(hueSlideView.getHue(), saturationSlideView.getSaturation(), valueSlideView.getValue()))
            return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
        }
        set(value) {
            val hsv = FloatArray(3)
            Color.colorToHSV(value, hsv)
            hueSlideView.setHue(hsv[0])
            saturationSlideView.setSaturation(hsv[1])
            valueSlideView.setValue(hsv[2])
            alphaSlideView?.setCurrentAlpha(Color.alpha(value))
        }

    init {
        orientation = VERTICAL
        
        val a = context.obtainStyledAttributes(attrs, R.styleable.HSVSlideView)
        val showAlphaSlider = a.getBoolean(R.styleable.HSVSlideView_hsv_show_alpha, true)
        a.recycle()

        val sliderLP = LayoutParams(LayoutParams.MATCH_PARENT, (30.dp + 0.5f).toInt())
        val spaceLP = LayoutParams(LayoutParams.MATCH_PARENT, 0)
        spaceLP.weight = 1f

        addView(Space(context), spaceLP)
        addView(hueSlideView, sliderLP)
        addView(Space(context), spaceLP)
        addView(saturationSlideView, sliderLP)
        addView(Space(context), spaceLP)
        addView(valueSlideView, sliderLP)
        addView(Space(context), spaceLP)
        
        if (showAlphaSlider) {
            alphaSlideView = AlphaSlideView(context)
            addView(alphaSlideView, sliderLP)
            addView(Space(context), spaceLP)
        }


        alphaSlideView?.setOnAlphaChangedListener(object : OnAlphaChangedListener {
            override fun onAlphaChanged(alpha: Int) {
                val color = Color.HSVToColor(floatArrayOf(hueSlideView.getHue(), saturationSlideView.getSaturation(), valueSlideView.getValue()))
                onColorChanged(alpha, color)
            }

            override fun onAlphaPicked(alpha: Int) {
                val color = Color.HSVToColor(floatArrayOf(hueSlideView.getHue(), saturationSlideView.getSaturation(), valueSlideView.getValue()))
                onColorPicked(alpha, color)
            }
        })

        hueSlideView.hueChangedListener = object : OnHueChangedListener {
            override fun onHueChanged(hue: Float) {
                saturationSlideView.setHue(hue)
                valueSlideView.setHue(hue)
                val color = Color.HSVToColor(floatArrayOf(hue, saturationSlideView.getSaturation(), valueSlideView.getValue()))
                alphaSlideView?.setMaskColor(color)
                onColorChanged(currentAlpha, color)
            }

            override fun onHuePicked(hue: Float) {
                val color = Color.HSVToColor(floatArrayOf(hue, saturationSlideView.getSaturation(), valueSlideView.getValue()))
                onColorPicked(currentAlpha, color)
            }
        }

        saturationSlideView.saturationChangedListener = object : OnSaturationChangedListener {
            override fun onSaturationChanged(saturation: Float) {
                valueSlideView.setSaturation(saturation)
                val color = Color.HSVToColor(floatArrayOf(hueSlideView.getHue(), saturation, valueSlideView.getValue()))
                alphaSlideView?.setMaskColor(color)
                onColorChanged(currentAlpha, color)
            }

            override fun onSaturationPicked(saturation: Float) {
                val color = Color.HSVToColor(floatArrayOf(hueSlideView.getHue(), saturation, valueSlideView.getValue()))
                onColorPicked(currentAlpha, color)
            }
        }

        valueSlideView.valueChangedListener = object : OnValueChangedListener {
            override fun onValueChanged(value: Float) {
                saturationSlideView.setValue(value)
                val color = Color.HSVToColor(floatArrayOf(hueSlideView.getHue(), saturationSlideView.getSaturation(), value))
                alphaSlideView?.setMaskColor(color)
                onColorChanged(currentAlpha, color)
            }

            override fun onValuePicked(value: Float) {
                val color = Color.HSVToColor(floatArrayOf(hueSlideView.getHue(), saturationSlideView.getSaturation(), value))
                onColorPicked(currentAlpha, color)
            }
        }

    }

    private val currentAlpha = alphaSlideView?.getCurrentAlpha() ?: 255

    private fun onColorChanged(alpha: Int, color: Int) {
        val mixedColor = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
        colorChangedListener?.onColorChanged(mixedColor)
    }

    private fun onColorPicked(alpha: Int, color: Int) {
        val mixedColor = Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color))
        colorChangedListener?.onColorPicked(mixedColor)
    }

}