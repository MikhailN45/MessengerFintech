package com.study.messengerfintech.customview

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.study.messengerfintech.R
import com.study.messengerfintech.data.Message
import com.study.messengerfintech.data.Reaction
import com.study.messengerfintech.utils.Utils.toPx

class MessageBodyViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    init {
        inflate(context, R.layout.message_body_viewgroup, this)
    }

    private val messageText: TextView = findViewById(R.id.message_text)
    private val nicknameText: TextView = findViewById(R.id.message_sender_nickname)
    var plus = ImageButton(context).apply {
        setImageResource(R.drawable.ic_add_reaction)
        setBackgroundResource(R.drawable.plus_background)
        visibility = GONE
    }

    private lateinit var message: Message

    fun setMessage(message: Message) {
        this.message = message
        this.messageText.text = message.message
        this.nicknameText.text = message.senderNickname

        if (message.isMine) {
            nicknameText.visibility = GONE
            getChildAt(AVATAR_POSITION).visibility = GONE
            getChildAt(MESSAGE_BOX_POSITION).setBackgroundResource(R.drawable.my_message_background)
        } else {
            nicknameText.visibility = VISIBLE
            getChildAt(AVATAR_POSITION).visibility = VISIBLE
            getChildAt(MESSAGE_BOX_POSITION).setBackgroundResource(R.drawable.message_background)
        }

        (getChildAt(FLEXBOX_POSITION) as FlexBox).apply {
            removeAllViews()
            if (message.reactions.isNotEmpty()) plus.visibility = VISIBLE
            else plus.visibility = GONE
        }
        for (reaction in message.reactions) addEmoji(reaction)
    }

    private fun addEmoji(reaction: Reaction) {
        val flexBox = (getChildAt(FLEXBOX_POSITION) as FlexBox)
        val emoji = Emoji(context).apply {
            setReaction(reaction)
            clickCallback = {
                if (reaction.num == 0) {
                    flexBox.removeView(this)
                    message.reactions.remove(reaction)
                    if (message.reactions.size == 0) plus.visibility = GONE
                }
            }
        }

        if (!message.reactions.contains(reaction)) message.reactions.add(reaction)
        plus.visibility = VISIBLE
        flexBox.addView(emoji, 0)
    }

    fun setMessageOnLongClick(callback: () -> Unit) {
        getChildAt(MESSAGE_BOX_POSITION).setOnLongClickListener {
            callback()
            true
        }
        plus.setOnClickListener {
            callback()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        plus.apply {
            if (parent == null) (getChildAt(FLEXBOX_POSITION) as FlexBox).addView(this)
            with(layoutParams) {
                height = PLUS_BUTTON_SIDE.toPx()
                width = height
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        require(childCount == 3)

        val imageView = getChildAt(AVATAR_POSITION)
        val textView = getChildAt(MESSAGE_BOX_POSITION)
        val flexBoxView = getChildAt(FLEXBOX_POSITION) as FlexBox

        var totalWidth = 0
        var totalHeight = 0

        measureChildWithMargins(
            imageView, widthMeasureSpec, 0, heightMeasureSpec, 0
        )

        val marginLeft = (imageView.layoutParams as MarginLayoutParams).leftMargin
        val marginRight = (imageView.layoutParams as MarginLayoutParams).rightMargin
        totalWidth += imageView.measuredWidth + marginLeft + marginRight
        totalHeight = maxOf(totalHeight, imageView.measuredHeight)

        measureChildWithMargins(
            textView, widthMeasureSpec, imageView.measuredWidth, heightMeasureSpec, 0
        )

        val textMarginLeft = (textView.layoutParams as MarginLayoutParams).leftMargin
        val textMarginRight = (textView.layoutParams as MarginLayoutParams).rightMargin
        val textWidth = textView.measuredWidth + textMarginLeft + textMarginRight
        totalHeight = maxOf(totalHeight, textView.measuredHeight)

        measureChildWithMargins(
            flexBoxView, widthMeasureSpec, imageView.measuredWidth, heightMeasureSpec, totalHeight
        )

        val topMargin = (flexBoxView.layoutParams as MarginLayoutParams).topMargin
        totalHeight += flexBoxView.measuredHeight + topMargin
        totalWidth += maxOf(flexBoxView.measuredWidth, textWidth)

        if (message.isMine) totalWidth = MeasureSpec.getSize(widthMeasureSpec)

        setMeasuredDimension(
            resolveSize(totalWidth + paddingRight + paddingLeft, widthMeasureSpec),
            resolveSize(totalHeight + paddingTop + paddingBottom, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val imageView = getChildAt(AVATAR_POSITION)
        val textView = getChildAt(MESSAGE_BOX_POSITION)
        val flexBoxView = getChildAt(FLEXBOX_POSITION)

        imageView.layout(
            paddingLeft,
            paddingTop,
            paddingLeft + imageView.measuredWidth,
            paddingTop + imageView.measuredHeight
        )
        val topMargin = (flexBoxView.layoutParams as MarginLayoutParams).topMargin

        if (message.isMine) {
            textView.layout(
                measuredWidth - textView.measuredWidth,
                paddingTop,
                measuredWidth,
                paddingTop + textView.measuredHeight
            )

            flexBoxView.layout(
                measuredWidth - paddingRight - flexBoxView.measuredWidth, //textView.measuredWidth,
                textView.bottom + topMargin,
                measuredWidth - paddingRight,
                textView.bottom + flexBoxView.measuredHeight
            )
        } else {
            val marginRight = (imageView.layoutParams as MarginLayoutParams).rightMargin

            textView.layout(
                imageView.right + marginRight,
                paddingTop,
                imageView.right + textView.measuredWidth,
                paddingTop + textView.measuredHeight
            )

            flexBoxView.layout(
                imageView.right + marginRight,
                textView.bottom + topMargin,
                imageView.right + flexBoxView.measuredWidth,
                textView.bottom + flexBoxView.measuredHeight
            )
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun checkLayoutParams(p: LayoutParams): Boolean {
        return p is MarginLayoutParams
    }

    override fun generateLayoutParams(p: LayoutParams): LayoutParams {
        return MarginLayoutParams(p)
    }

    companion object {
        private const val PLUS_BUTTON_SIDE = 34F
        private const val AVATAR_POSITION = 0
        private const val MESSAGE_BOX_POSITION = 1
        private const val FLEXBOX_POSITION = 2
    }
}