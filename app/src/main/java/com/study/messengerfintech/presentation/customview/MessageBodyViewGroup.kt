package com.study.messengerfintech.presentation.customview

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.study.messengerfintech.R
import com.study.messengerfintech.domain.model.Message
import com.study.messengerfintech.domain.model.UnitedReaction
import com.study.messengerfintech.domain.model.User
import com.study.messengerfintech.utils.EmojiAdd
import com.study.messengerfintech.utils.EmojiDelete
import com.study.messengerfintech.utils.OnEmojiClickEvent
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

    private val senderAvatar: ImageView = findViewById(R.id.sender_avatar)
    private val messageText: TextView = findViewById(R.id.message_text)
    private val nicknameText: TextView = findViewById(R.id.message_sender_nickname)
    var plus = ImageButton(context).apply {
        setImageResource(R.drawable.ic_add_reaction)
        setBackgroundResource(R.drawable.plus_background)
        visibility = GONE
    }

    private lateinit var message: Message

    fun setMessageData(message: Message) {
        this.message = message
        this.messageText.text =
            HtmlCompat.fromHtml(message.content, HtmlCompat.FROM_HTML_MODE_LEGACY).trim()
        this.nicknameText.text = message.senderName
        Glide.with(context)
            .load(message.avatarUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.drawable.ic_avatar_placeholder)
            .into(this.senderAvatar)

        if (message.isFromMe) {
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
            if (message.emojiCodeReactionMap.isNotEmpty()) plus.visibility = VISIBLE
            else plus.visibility = GONE
        }
        for (reaction in message.emojiCodeReactionMap)
            addEmoji(reaction.value)
    }

    private fun addEmoji(reaction: UnitedReaction) {
        val flexBox = (getChildAt(FLEXBOX_POSITION) as FlexBox)
        val emoji = Emoji(context).apply {
            this.reaction = reaction
            clickCallback = {
                if (reaction.usersId.size == 0) {
                    flexBox.removeView(this)
                    message.emojiCodeReactionMap.remove(reaction.getUnicode())
                }

                if (message.emojiCodeReactionMap.size == 0) plus.visibility = GONE

                val emojiClick =
                    if (reaction.usersId.contains(User.ME.id)) EmojiAdd(message.id, reaction.name)
                    else EmojiDelete(message.id, reaction.name)

                emojiClickListener(emojiClick)
            }
        }

        plus.visibility = VISIBLE
        flexBox.addView(emoji, 0)
    }

    private var emojiClickListener: (OnEmojiClickEvent) -> Unit = { _ -> }
    fun setOnEmojiClickListener(callback: (OnEmojiClickEvent) -> Unit) {
        emojiClickListener = callback
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

        if (message.isFromMe) totalWidth = MeasureSpec.getSize(widthMeasureSpec)

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

        if (message.isFromMe) {
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