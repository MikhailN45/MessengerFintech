package com.study.messengerfintech.presentation.customview

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.study.messengerfintech.R

class FlexBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    private var paddingRows: Int
    private var paddingColumns: Int

    init {
        val typedArray: TypedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.FlexBox,
            defStyleAttr,
            defStyleRes
        )
        paddingRows =
            typedArray.getDimension(R.styleable.FlexBox_paddingRows, DIMEN20).toInt()
        paddingColumns =
            typedArray.getDimension(R.styleable.FlexBox_paddingColumns, DIMEN20).toInt()
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        var widthLine = paddingRight + paddingLeft
        var heightLine = 0
        var totalWidth = availableWidth
        var totalHeight = paddingTop + paddingBottom
        var sumHeight = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, totalHeight)

            if (widthLine + child.measuredWidth >= availableWidth) {
                totalHeight += heightLine + paddingRows
                widthLine = paddingRight + paddingLeft
                heightLine = 0
            }

            widthLine += child.measuredWidth + paddingColumns
            if (child.isVisible) heightLine = maxOf(heightLine, child.measuredHeight)
            sumHeight += child.measuredHeight
        }

        if (totalHeight == paddingTop + paddingBottom) totalWidth = widthLine * 2
        totalHeight += heightLine * 2

        setMeasuredDimension(
            resolveSize(totalWidth, widthMeasureSpec),
            resolveSize(totalHeight, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var currentTop = paddingTop
        var currentStart = paddingLeft
        var heightLine = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (currentStart + child.measuredWidth + paddingRight >= measuredWidth) {
                currentTop += heightLine + paddingRows
                heightLine = 0
                currentStart = paddingLeft
            }

            child.layout(
                currentStart,
                currentTop,
                currentStart + child.measuredWidth,
                currentTop + child.measuredHeight
            )
            currentStart += child.measuredWidth + paddingColumns
            heightLine = maxOf(heightLine, child.measuredHeight)
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

    companion object Dimens {
        private const val DIMEN20 = 20f
    }
}
