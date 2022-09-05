package com.udacity.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import kotlin.math.min
import kotlin.properties.Delegates

enum class ButtonState {
    IDLE,
    CLICKED,
    LOADING,
    COMPLETED
}

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0                       // Button width
    private var heightSize = 0                      // Button height

    private var buttonState = ButtonState.IDLE      // Button state

    private val valueAnimator = ValueAnimator()

    // position variable which will be used to draw label and indicator circle position
    private val pointPosition: PointF = PointF(0.0f, 0.0f)

    private val btnPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val fontPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        //color = Color.WHITE
        textSize = 55.0f
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG).apply {
        color = Color.BLACK
        textAlign = Paint.Align.LEFT
        textSize = 55.0f
    }

    private val circularProgressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }


    init {
        isClickable = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        widthSize = w
        heightSize = h
    }

    // onHover

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), btnPaint)

        drawButtonText(canvas)

    }

    private fun drawButtonText(canvas: Canvas?) {
        val textHeight = textPaint.fontMetrics.descent - textPaint.fontMetrics.descent
        val textY = textHeight - fontPaint.fontMetrics.descent
        canvas?.drawText("Download".toString(), pointPosition.x, textY, fontPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minW: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minW, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}