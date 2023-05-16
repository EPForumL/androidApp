package com.github.ybecker.epforuml.latex

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.webkit.WebView
import androidx.core.content.ContextCompat
import com.github.ybecker.epforuml.R

/**
 * Created by lingaraj on 3/15/17.
 */
class MathView : WebView {
    private val TAG = "KhanAcademyKatexView"
    private var display_text: String? = null
    private var text_color = 0
    private var text_size = 0
    private var clickable = false
    private var enable_zoom_in_controls = false

    constructor(context: Context) : super(context) {
        configurationSettingWebView(enable_zoom_in_controls)
        setDefaultTextColor(context)
        setDefaultTextSize()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        configurationSettingWebView(enable_zoom_in_controls)
        val mTypeArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.MathView,
            0, 0
        )
        try {
            setBackgroundColor(
                mTypeArray.getInteger(
                    R.styleable.MathView_setViewBackgroundColor,
                    ContextCompat.getColor(context, com.firebase.ui.auth.R.color.fui_transparent)
                )
            )
            setTextColor(
                mTypeArray.getColor(
                    R.styleable.MathView_setTextColor,
                    ContextCompat.getColor(context, R.color.black)
                )
            )
            pixelSizeConversion(
                mTypeArray.getDimension(
                    R.styleable.MathView_setTextSize,
                    default_text_size
                )
            )
            setDisplayText(mTypeArray.getString(R.styleable.MathView_setText))
            isClickable = mTypeArray.getBoolean(R.styleable.MathView_setClickable, false)
        } catch (e: Exception) {
            Log.d(TAG, "Exception:$e")
        }
    }

    fun setViewBackgroundColor(color: Int) {
        setBackgroundColor(color)
        this.invalidate()
    }

    private fun pixelSizeConversion(dimension: Float) {
        if (dimension == default_text_size) {
            setTextSize(default_text_size.toInt())
        } else {
            val pixel_dimen_equivalent_size = (dimension.toDouble() / 1.6).toInt()
            setTextSize(pixel_dimen_equivalent_size)
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "NewApi")
    private fun configurationSettingWebView(enable_zoom_in_controls: Boolean) {
        setLayerType(LAYER_TYPE_HARDWARE, null)
        val settings = this.settings
        settings.javaScriptEnabled = true
        settings.allowFileAccess = true
        settings.displayZoomControls = enable_zoom_in_controls
        settings.builtInZoomControls = enable_zoom_in_controls
        settings.setSupportZoom(enable_zoom_in_controls)
        this.isVerticalScrollBarEnabled = enable_zoom_in_controls
        this.isHorizontalScrollBarEnabled = enable_zoom_in_controls
        Log.d(TAG, "Zoom in controls:$enable_zoom_in_controls")
    }

    fun setDisplayText(formula_text: String?) {
        display_text = formula_text
        loadData()
    }

    private val offlineKatexConfig: String
        private get() {
            val offline_config = """<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Auto-render test</title>
        <link rel="stylesheet" type="text/css" href="file:///android_asset/katex/katex.min.css">
        <link rel="stylesheet" type="text/css" href="file:///android_asset/themes/style.css" >
        <script type="text/javascript" src="file:///android_asset/katex/katex.min.js" ></script>
        <script type="text/javascript" src="file:///android_asset/katex/contrib/auto-render.min.js" ></script>
        <script type="text/javascript" src="file:///android_asset/katex/contrib/auto-render.js" ></script>
        <script type="text/javascript" src="file:///android_asset/jquery.min.js" ></script>
        <script type="text/javascript" src="file:///android_asset/latex_parser.js" ></script>
        <meta name="viewport" content="width=device-width"/>
<link rel="stylesheet" href="file:///android_asset/webviewstyle.css"/>
<style type='text/css'>body {margin: 0px;padding: 0px;font-size:${text_size}px;color:${
                getHexColor(
                    text_color
                )
            }; } </style>    </head>
    <p style="word-wrap: break-word;">
        {formula}
    </p>
</html>"""
            val start =
                "<html><head><meta http-equiv='Content-Type' content='text/html' charset='UTF-8' /><style> body {" +
                        " white-space: nowrap;}</style></head><body>"
            val end = "</body></html>"
            return offline_config.replace("{formula}", display_text!!)
        }

    fun setTextSize(size: Int) {
        text_size = size
        loadData()
    }

    fun setTextColor(color: Int) {
        text_color = color
        loadData()
    }

    private fun getHexColor(intColor: Int): String {
        //Android and javascript color format differ javascript support Hex color, so the android color which user sets is converted to hexcolor to replicate the same in javascript.
        val hexColor = String.format("#%06X", 0xFFFFFF and intColor)
        Log.d(TAG, "Hex Color:$hexColor")
        return hexColor
    }

    private fun setDefaultTextColor(context: Context) {
        //sets default text color to black
        text_color = ContextCompat.getColor(context, R.color.black)
    }

    private fun setDefaultTextSize() {
        //sets view default text size to 18
        text_size = default_text_size.toInt()
    }

    private fun loadData() {
        if (display_text != null) {
            loadDataWithBaseURL("null", offlineKatexConfig, "text/html", "UTF-8", "about:blank")
        }
    }

    override fun setClickable(is_clickable: Boolean) {
        this.isEnabled = true
        clickable = is_clickable
        enable_zoom_in_controls = !is_clickable
        configurationSettingWebView(enable_zoom_in_controls)
        this.invalidate()
    }

    @SuppressLint("NewApi")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (clickable && event.action == MotionEvent.ACTION_DOWN) {
            callOnClick()
            false
        } else {
            super.onTouchEvent(event)
        }
    }

    companion object {
        private const val default_text_size = 18f
    }
}