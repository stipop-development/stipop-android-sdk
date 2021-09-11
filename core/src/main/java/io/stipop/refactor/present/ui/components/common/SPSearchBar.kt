package io.stipop.refactor.present.ui.components.common

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity.CENTER
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.InputMethodManager
import com.bumptech.glide.Glide.init
import io.stipop.Config
import io.stipop.refactor.present.ui.components.core.SPEditText
import io.stipop.refactor.present.ui.components.core.SPLinearLayout


class SPSearchBar(context: Context, attrs: AttributeSet? = null) : SPLinearLayout(context, attrs) {
    private var textChangedSearchKeywordListener: TextWatcher? = null
    private var onFocusChangeSearchKeywordListener: OnFocusChangeListener? = null
    private var onClickSearchDeleteButtonListener: OnClickListener? = null

    private val searchIcon: SPIconImageButton = SPIconImageButton(context)
    private val searchKeyword: SPEditText = SPEditText(context)
    private val searchDeleteButton: SPIconImageButton = SPIconImageButton(context)

    var text: String
    get() = searchKeyword.text.toString()
    set(value) = searchKeyword.setText(value)

    init {

        layoutParams = LayoutParams(
            MATCH_PARENT, WRAP_CONTENT
        )

        gravity = CENTER

        searchIcon.apply {
            setImageResource(Config.getSearchbarIconResourceId(context))
            isEnabled = false
        }

        searchKeyword.apply {
            layoutParams = LayoutParams(
                0, WRAP_CONTENT, 1f
            )
            background = null
            hint = Config.getSearchKeywordHint(context)
            setHintTextColor(Config.getSearchKeywordHintColor(context))

            setTextColor(Color.RED)
            setOnFocusChangeListener { v, hasFocus ->
                run {
                    if (hasFocus) {
                        searchIcon.isEnabled = true
                        searchDeleteButton.isEnabled = true
                    } else {
                        val inputMethodManager =
                            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
                    }

                    onFocusChangeSearchKeywordListener?.onFocusChange(v, hasFocus)
                }
            }
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    textChangedSearchKeywordListener?.beforeTextChanged(p0, p1, p2, p3)
                }

                override fun afterTextChanged(p0: Editable?) {
                    textChangedSearchKeywordListener?.afterTextChanged(p0)
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    textChangedSearchKeywordListener?.onTextChanged(p0, p1, p2, p3)
                }
            })
        }

        searchDeleteButton.apply {
            isEnabled = false
            setImageResource(Config.getSearchBarDeleteIconResourceId(context))
            setColorFilter(Color.RED)
            setOnClickListener { v ->
                run {
                    searchKeyword.text = null
                    searchKeyword.clearFocus()
                    searchIcon.isEnabled = false
                    searchDeleteButton.isEnabled = false
                    isEnabled = false
                    onClickSearchDeleteButtonListener?.onClick(v)
                }
            }
        }

        addView(searchIcon)
        addView(searchKeyword)
        addView(searchDeleteButton)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        searchKeyword.apply {
            setTextColor(Config.getSearchKeywordTextColor(context))
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)

        val shape = GradientDrawable()
        shape.cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Config.searchbarRadius.toFloat(), context.resources.displayMetrics)
        shape.setColor(Config.getSearchBarBackgroundColor(context))
        background = shape
    }

    fun setOnClickSearchDeleteButtonListener(listener: OnClickListener) {
        onClickSearchDeleteButtonListener = listener
    }

    fun setOnFocusChangeSearchKeywordListener(listener: View.OnFocusChangeListener) {
        onFocusChangeSearchKeywordListener = listener
    }

    fun addTextChangedSearchKeywordListener(listener: TextWatcher) {
        textChangedSearchKeywordListener = listener
    }
}
