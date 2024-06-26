package com.study.messengerfintech.presentation.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.study.messengerfintech.R
import com.study.messengerfintech.domain.model.UnitedReaction
import com.study.messengerfintech.domain.model.User
import com.study.messengerfintech.utils.Utils.toSp

class Emoji @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var smileCode: String = ""
        private set(value) {
            field = value
            requestLayout()
        }

    private var num = 0
        set(value) {
            if (value < 0) return
            field = value
            requestLayout()
        }

    var reaction: UnitedReaction? = null
        set(value) {
            field = value
            if (value == null) return
            num = value.usersId.size
            smileCode = value.getUnicode()
            isSelected = value.usersId.contains(User.ME.id)
        }

    var clickCallback = { }

    private val textToDraw
        get() = "$smileCode $num"

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 14f.toSp(context)
        textAlign = Paint.Align.CENTER
    }

    private val textBounds = Rect()
    private val textCoordinate = PointF()
    private val tempFontMetrics = Paint.FontMetrics()

    init {
        setBackgroundResource(R.drawable.emoji_background)

        setOnClickListener {
            performClickEmoji()
        }
    }

    private fun performClickEmoji() {
        isSelected = !isSelected
        reaction?.usersId?.let {
            if (isSelected) {
                it.add(User.ME.id)
                num += 1
            } else {
                it.remove(User.ME.id)
                num -= 1
            }
            clickCallback()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        textPaint.getTextBounds(textToDraw, 0, textToDraw.length, textBounds)
        val actualWidth = textBounds.width() + paddingRight + paddingLeft + DEFAULT_SPACE
        val actualHeight = textBounds.height() + paddingTop + paddingBottom + DEFAULT_SPACE
        setMeasuredDimension(
            resolveSize(actualWidth, widthMeasureSpec),
            resolveSize(actualHeight, heightMeasureSpec)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        textPaint.getFontMetrics(tempFontMetrics)
        textCoordinate.x = w / 2f
        textCoordinate.y = h / 2f + textBounds.height() / 2 - tempFontMetrics.descent
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState =
            super.onCreateDrawableState(extraSpace + SUPPORTED_DRAWABLE_STATE.size)
        if (isSelected) mergeDrawableStates(drawableState, SUPPORTED_DRAWABLE_STATE)
        return drawableState
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawText(textToDraw, textCoordinate.x, textCoordinate.y, textPaint)
    }

    companion object {
        private const val DEFAULT_SPACE = 48
        private val SUPPORTED_DRAWABLE_STATE = intArrayOf(android.R.attr.state_selected)
    }
}