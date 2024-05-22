package com.andka.penpal.ui.customview

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.view.View
import androidx.core.content.withStyledAttributes
import com.andka.penpal.R
import com.google.android.material.textfield.TextInputEditText

class MatchPatternEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextInputEditText(context, attrs) {
    private var regexPatterns: String = ""
    private var type: String = ""

    init {
        context.withStyledAttributes(attrs, R.styleable.MatchPatternEditText) {
            val inputType = getInt(R.styleable.MatchPatternEditText_android_inputType, 0)

            val patternsAndTypes = mapOf(
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS to Pair(
                    Patterns.EMAIL_ADDRESS.toString(), "email"
                ),
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD to Pair(
                    "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$", "password"
                )
            )

            patternsAndTypes[inputType]?.let {
                regexPatterns = it.first
                type = it.second
            }

            setupListener()
        }

        isSingleLine = true
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun setupListener() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                error = if (s.isNotEmpty()) {
                    if (!checkPattern(s, regexPatterns)) {
                        if (type == "email") context.getString(R.string.error_invalid_email)
                        else context.getString(R.string.error_insecure_password)
                    } else null
                } else null
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
}