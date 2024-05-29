package com.andka.hanashi.widget

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.andka.hanashi.R

open class BaseEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {
    open val errorMessage: String? = null

    init {
        isSingleLine = true
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        background = ContextCompat.getDrawable(context, R.drawable.rounded_background)
        setTextColor(ContextCompat.getColor(context, R.color.black))
        setHintTextColor(ContextCompat.getColor(context, R.color.primary_200))
        maxLines = 1

        setupListener()
    }

    private fun setupListener() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!checker() && s.isNotEmpty()) {
                    error = errorMessage
                }
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }

    open fun checker(): Boolean {
        return true
    }
}

class EmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : BaseEditText(context, attrs) {
    override val errorMessage: String = context.getString(R.string.error_invalid_email)
    override fun checker(): Boolean {
        val emailPattern = android.util.Patterns.EMAIL_ADDRESS.toString()
        return text?.matches(emailPattern.toRegex()) == true
    }
}

class PasswordEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : BaseEditText(context, attrs) {
    override val errorMessage: String = context.getString(R.string.error_insecure_password)

    init {
        transformationMethod = PasswordTransformationMethod.getInstance()
    }

    override fun checker(): Boolean {
        val passwordPattern = "^.{8,}\$"
        return text?.matches(passwordPattern.toRegex()) == true
    }
}
