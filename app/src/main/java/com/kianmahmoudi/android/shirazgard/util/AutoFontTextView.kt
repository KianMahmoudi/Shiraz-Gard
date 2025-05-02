package com.kianmahmoudi.android.shirazgard.util

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class AutoFontTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : AppCompatTextView(context, attrs, defStyleAttr) {

    companion object {
        private var faFont: Typeface? = null
        private var enFont: Typeface? = null

        private const val FONT_FA_PATH = "font-fa/font_regular.ttf"
        private const val FONT_EN_PATH = "font/font_regular.ttf"

        private fun isPersian(text: String): Boolean {
            return text.any { it in '\u0600'..'\u06FF' || it in '\u0750'..'\u077F' }
        }
    }

    init {
        try {
            if (faFont == null)
                faFont = Typeface.createFromAsset(context.assets, FONT_FA_PATH)
            if (enFont == null)
                enFont = Typeface.createFromAsset(context.assets, FONT_EN_PATH)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        applyFont(text?.toString() ?: "")
    }


    private fun applyFont(text: String) {
        typeface = if (isPersian(text)) faFont ?: typeface else enFont ?: typeface
    }
}
