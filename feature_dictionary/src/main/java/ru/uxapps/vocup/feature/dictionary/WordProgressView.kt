package ru.uxapps.vocup.feature.dictionary

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.EXACTLY
import androidx.core.content.res.use
import androidx.core.math.MathUtils

class WordProgressView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val mainPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        context.obtainStyledAttributes(attrs, R.styleable.WordProgressView).use {
            mainPaint.color = it.getColor(R.styleable.WordProgressView_mainColor, Color.BLACK)
        }
    }

    private var progress: Int = 100

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        require(MeasureSpec.getMode(widthMeasureSpec) == EXACTLY) { "Require exact width" }
        require(MeasureSpec.getMode(heightMeasureSpec) == EXACTLY) { "Require exact height" }
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        require(width == height) { "Require square form" }
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        val w = measuredWidth.toFloat()
        val h = measuredHeight.toFloat()
        val angle = 360f * progress / 100f
        canvas.drawArc(0f, 0f, w, h, -90f, angle, true, mainPaint)
    }

    fun setProgress(progress: Int) {
        this.progress = MathUtils.clamp(progress, 0, 100)
        invalidate()
    }
}