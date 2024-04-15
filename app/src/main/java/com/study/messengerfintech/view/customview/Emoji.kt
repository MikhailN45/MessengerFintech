package com.study.messengerfintech.view.customview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.study.messengerfintech.R
import com.study.messengerfintech.model.data.Reaction
import com.study.messengerfintech.utils.Utils.sp

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
            reaction?.num = num
            requestLayout()
        }

    private var userId = ""
        set(value) {
            reaction?.userId = value
            isSelected = value == "me"
            field = value
        }

    private fun setEmoji(num: Int) {
        resources.getStringArray(R.array.emojis).apply {
            if (num < size)
                smileCode = get(num)
        }
    }

    fun setReaction(reaction: Reaction) {
        this.reaction = reaction
        setEmoji(reaction.smile)
        num = reaction.num
        userId = reaction.userId
    }

    private var reaction: Reaction? = null

    var clickCallback = { }

    private val textToDraw
        get() = "$smileCode $num"

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 14f.sp(context)
        textAlign = Paint.Align.CENTER
    }

    private val textBounds = Rect()
    private val textCoordinate = PointF()
    private val tempFontMetrics = Paint.FontMetrics()

    private val smileArray: Array<String> = resources.getStringArray(R.array.emojis)

    init {
        val typedArray: TypedArray = context.obtainStyledAttributes(
            attrs, R.styleable.Emoji, defStyleAttr, defStyleRes
        )

        smileCode = smileArray[typedArray.getInt(R.styleable.Emoji_smiles, 1)]
        num = typedArray.getInt(R.styleable.Emoji_customNum, num)

        textPaint.color = typedArray.getColor(R.styleable.Emoji_textColor, textPaint.color)
        textPaint.textSize = typedArray.getDimension(R.styleable.Emoji_textSize, textPaint.textSize)

        setBackgroundResource(R.drawable.emoji_background)

        setOnClickListener {
            performClickEmoji()
        }
        typedArray.recycle()
    }

    private fun performClickEmoji() {
        isSelected = !isSelected
        if (userId == "me") {
            num -= 1
            userId = "other"
        } else {
            num += 1
            userId = "me"
        }
        clickCallback()
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