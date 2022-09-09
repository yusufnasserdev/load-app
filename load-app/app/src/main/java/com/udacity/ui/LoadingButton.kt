package com.udacity.ui

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnRepeat
import androidx.core.content.res.getStringOrThrow
import androidx.core.content.withStyledAttributes
import com.udacity.R
import timber.log.Timber
import kotlin.math.min

enum class ButtonState {
    IDLE,
    LOADING
}

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Dimensions
    private var widthSize = 0f                       // Button width
    private var heightSize = 0f                      // Button height
    private var radius = 0f

    // Color attributes
    private var btnColor = 0
    private var loadingColor1 = 0
    private var loadingColor2 = 0
    private var txtColor = 0

    // Texts attributes
    private var idleText: String = String()
    private var loadingText: String = String()
    private var btnText: String = String()

    private var btnState = ButtonState.IDLE      // Button state

    // Float variable holding the percentage animation progress of the download
    private var progress = 0f

    // Paint object for the button color
    private val btnPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Paint object for the loading color
    private val loadingPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Paint object for the font used in the text
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)

    // Paint object for the circular progress shape
    private val progressArcPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Value Animator
    private val valueAnimator = ValueAnimator.ofFloat(0f, 1f)

    // Init block for initializing attributes
    init {
        isClickable = true

        // Binding custom attributes
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {

            // Colors
            txtColor = getColor(R.styleable.LoadingButton_txtColor, 0)
            btnColor = getColor(R.styleable.LoadingButton_btnColor, 0)
            loadingColor1 = getColor(R.styleable.LoadingButton_loadingColor1, 0)
            loadingColor2 = getColor(R.styleable.LoadingButton_loadingColor2, 0)

            // Texts
            idleText = getStringOrThrow(R.styleable.LoadingButton_idleTxt)
            loadingText = getStringOrThrow(R.styleable.LoadingButton_loadingTxt)

            // Paint objects
            btnPaint.apply {
                style = Paint.Style.FILL
                color = btnColor
            }

            loadingPaint.apply {
                style = Paint.Style.FILL
                color = loadingColor1
            }

            labelPaint.apply {
                color = txtColor
                textAlign = Paint.Align.CENTER
                textSize = 66f
            }

            progressArcPaint.apply {
                style = Paint.Style.FILL
                color = loadingColor2
            }

        }

        valueAnimator
            .apply {

                duration = 3000
                repeatCount = ValueAnimator.INFINITE

                addUpdateListener {
                    progress = it.animatedValue as Float

                    if (progressIsDone())
                        resetProgress()

                    invalidate()
                }

                doOnEnd {
                    isClickable = true
                }

            }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        widthSize = w.toFloat()
        heightSize = h.toFloat()
        radius = (min(w, h) / 2.0 * 0.7).toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawButtonRect(canvas)
        drawLoadingRect(canvas)
        drawButtonLabel(canvas)
        drawProgressArc(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minW: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minW, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )

        widthSize = w.toFloat()
        heightSize = h.toFloat()
        setMeasuredDimension(w, h)
    }

    override fun performClick(): Boolean {
        super.performClick()
        isClickable = false
        Timber.i("Clicked")
        btnState = ButtonState.LOADING
        valueAnimator.start()
        return true
    }

    private fun progressIsDone(): Boolean {
        return progress == 1f
    }

    private fun resetProgress() {
        progress = 0f
    }

    fun doneLoading() {
        valueAnimator.cancel()
        btnState = ButtonState.IDLE
        resetProgress()
        invalidate()
    }

    private fun drawButtonRect(canvas: Canvas?) {
        canvas?.drawRect(0f, 0f, widthSize, heightSize, btnPaint)
    }

    private fun drawLoadingRect(canvas: Canvas?) {
        canvas?.drawRect(0f, 0f, widthSize * progress, heightSize, loadingPaint)
    }

    private fun drawProgressArc(canvas: Canvas?) {

        // Center point of the arc
        val center = PointF(widthSize * 0.65f, heightSize * 0.5f)

        // Arc boundaries
        val oval = RectF(
            center.x - radius,
            center.y - radius,
            center.x + radius,
            center.y + radius
        )

        canvas?.drawArc(
            oval,
            0f,
            360f * progress,
            true,
            progressArcPaint
        )
    }

    private fun drawButtonLabel(canvas: Canvas?) {
        labelPaint.color = txtColor

        btnText = if (btnState == ButtonState.LOADING)
            loadingText
        else
            idleText


        val txtX = if (btnState == ButtonState.LOADING)
            widthSize * 0.4f
        else
            widthSize * 0.5f

        val txtY = (heightSize / 2f) - ((labelPaint.descent() + labelPaint.ascent()) / 2f)
        canvas?.drawText(btnText, txtX, txtY, labelPaint)
    }
}