package com.andka.penpal.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.andka.penpal.R
import com.google.android.material.textfield.TextInputEditText

class MatchPatternEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {
    private var regexPatterns: String = ""
    private var type: String = ""

    init {
        context.withStyledAttributes(attrs, R.styleable.MatchPatternEditText) {
            val inputType = getInt(R.styleable.MatchPatternEditText_android_inputType, 0)

            // Just check the email since we only check 2 types of input
            when (inputType and InputType.TYPE_MASK_VARIATION) {
                InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS -> {
                    regexPatterns = Patterns.EMAIL_ADDRESS.toString()
                    type = "email"
                }

                else -> {
                    regexPatterns = "^.{8,}\$"
                    type = "password"
                }
            }

            setupListener()
        }

        isSingleLine = true
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun setupListener() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty() && !checkPattern(s, regexPatterns)) {
                    error = if (type == "email") {
                        context.getString(R.string.error_invalid_email)
                    } else {
                        context.getString(R.string.error_insecure_password)
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }

    private fun checkPattern(text: CharSequence, pattern: String): Boolean {
        val matcher = pattern.toRegex().toPattern().matcher(text)
        return matcher.matches()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        context.apply {
            background = ContextCompat.getDrawable(this, R.drawable.rounded_background)
            setTextColor(ContextCompat.getColor(this, R.color.black))
            setHintTextColor(ContextCompat.getColor(this, R.color.primary_200))
        }
        maxLines = 1
    }
}