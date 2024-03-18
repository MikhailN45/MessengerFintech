package com.study.messengerfintech.customview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.study.messengerfintech.R
import com.study.messengerfintech.utils.Utils.sp

class Emoji @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var reactionsList: ReactionsList = ReactionsList.SMILING
        set(value) {
            if (field != value)
                field = value
            requestLayout()
        }

    private var num = 0
        set(value) {
            if (field != value)
                field = value
            requestLayout()
        }

    private val textToDraw
        get() = "$reactionsList $num"

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 14f.sp(context)
        textAlign = Paint.Align.CENTER
    }

    private val textBounds = Rect()
    private val textCoordinate = PointF()
    private val tempFontMetrics = Paint.FontMetrics()


    init {
        val typedArray: TypedArray = context.obtainStyledAttributes(
            attrs, R.styleable.Emoji, defStyleAttr, defStyleRes
        )

        reactionsList = when (typedArray.getInt(R.styleable.Emoji_smiles, 0)) {
            1 -> ReactionsList.SMILING
            2 -> ReactionsList.WINKING
            3 -> ReactionsList.HEART
            4 -> ReactionsList.SAD
            5 -> ReactionsList.JOY
            6 -> ReactionsList.NOT_INTERESTED
            7 -> ReactionsList.HUG
            8 -> ReactionsList.PARTY
            9 -> ReactionsList.CRY
            else -> ReactionsList.NOT_INTERESTED
        }

        num = typedArray.getInt(R.styleable.Emoji_customNum, num)
        textPaint.color = typedArray.getColor(R.styleable.Emoji_textColor, textPaint.color)
        textPaint.textSize = typedArray.getDimension(R.styleable.Emoji_textSize, textPaint.textSize)
        setOnClickListener { isSelected = !isSelected }
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        textPaint.getTextBounds(textToDraw, 0, textToDraw.length, textBounds)
        val actualWidth = textBounds.width() + paddingRight + paddingLeft + 48
        val actualHeight = textBounds.height() + paddingTop + paddingBottom + 48
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
        if (isSelected)
            mergeDrawableStates(drawableState, SUPPORTED_DRAWABLE_STATE)
        return drawableState
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawText(textToDraw, textCoordinate.x, textCoordinate.y, textPaint)
    }

    companion object {
        private val SUPPORTED_DRAWABLE_STATE = intArrayOf(android.R.attr.state_selected)
    }
}