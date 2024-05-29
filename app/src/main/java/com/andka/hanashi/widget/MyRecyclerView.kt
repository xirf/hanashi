package com.andka.hanashi.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.andka.hanashi.R
import com.andka.hanashi.databinding.MyRecyclerViewLayoutBinding
import com.andka.hanashi.databinding.RecyclerEmptyLayoutBinding
import com.andka.hanashi.databinding.RecyclerErrorLayoutBinding
import com.andka.hanashi.databinding.RecyclerLoadingLayoutBinding

class MyRecyclerView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    private val binding: MyRecyclerViewLayoutBinding = MyRecyclerViewLayoutBinding.inflate(LayoutInflater.from(context), this)
    private val errorBinding: RecyclerErrorLayoutBinding = binding.customErrorView
    private val emptyBinding: RecyclerEmptyLayoutBinding = binding.customEmptyView
    private val loadingBinding: RecyclerLoadingLayoutBinding = binding.customOverlayView

    val recyclerView: RecyclerView get() = binding.customRecyclerView

    private var errorText: String = ""
        set(value) {
            field = value
            errorBinding.errorMsgText.text = value
        }

    private var emptyText: String = ""
        set(value) {
            field = value
            emptyBinding.emptyMessage.text = value
        }

    @DrawableRes
    var errorIcon = 0
        set(value) {
            field = value
            errorBinding.errorImage.setImageResource(value)
        }

    @DrawableRes
    var emptyIcon = 0
        set(value) {
            field = value
            emptyBinding.emptyImage.setImageResource(value)
        }

    init {
        recyclerView.clipToPadding = false

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.MyRecyclerView,
            0,
            0
        ).apply {
            try {
                errorText = getString(R.styleable.MyRecyclerView_errorText) ?: "Something went wrong"
                emptyText = getString(R.styleable.MyRecyclerView_emptyText) ?: "Nothing to show"
                errorIcon = getResourceId(R.styleable.MyRecyclerView_errorIcon, R.drawable.hosnino_ai)
                emptyIcon = getInt(R.styleable.MyRecyclerView_emptyIcon, R.drawable.hosnino_ai)
                val pH = getInt(R.styleable.MyRecyclerView_paddingHorizontal, 0)
                val pV = getInt(R.styleable.MyRecyclerView_paddingVertical, 0)
                recyclerView.setPadding(pH, pV, pH, pV)
            } finally {
                recycle()
            }
        }
    }

    private fun showEmptyView(msg: String? = null) {
        emptyText = msg ?: emptyText
        loadingBinding.root.visibility = View.GONE
        errorBinding.root.visibility = View.GONE
        emptyBinding.root.visibility = View.VISIBLE
    }

    private fun showErrorView(msg: String? = null) {
        errorText = msg ?: errorText
        loadingBinding.root.visibility = View.GONE
        emptyBinding.root.visibility = View.GONE
        errorBinding.root.visibility = View.VISIBLE
    }

    private fun showLoadingView() {
        emptyBinding.root.visibility = View.GONE
        errorBinding.root.visibility = View.GONE

        loadingBinding.root.visibility = View.VISIBLE
    }

    private fun hideAllViews() {
        loadingBinding.root.visibility = View.GONE
        errorBinding.root.visibility = View.GONE
        emptyBinding.root.visibility = View.GONE
    }

    fun showView(state: ViewStatus, message: String? = null) {
        hideAllViews()
        when (state) {
            ViewStatus.LOADING -> showLoadingView()
            ViewStatus.ERROR -> showErrorView(message)
            ViewStatus.EMPTY -> showEmptyView(message)
            ViewStatus.ON_DATA -> {}

        }
    }

    fun setOnRetryClickListener(callback: () -> Unit) {
        errorBinding.retryButton.setOnClickListener { callback() }
    }

    enum class ViewStatus {
        ON_DATA,
        LOADING,
        ERROR,
        EMPTY,
    }
}