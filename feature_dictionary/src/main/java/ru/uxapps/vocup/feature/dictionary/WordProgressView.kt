package ru.uxapps.vocup.feature.dictionary

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.use
import androidx.core.graphics.rotationMatrix
import androidx.core.math.MathUtils

class WordProgressView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var firstColor: Int = 0
    private var secondColor: Int = 0
    private var radius: Float = 0f
    private var strokeWidth: Float = 0f
    private var dashWidth = 3f

    init {
        context.obtainStyledAttributes(attrs, R.styleable.WordProgressView).use {
            firstColor = it.getColor(R.styleable.WordProgressView_firstColor, Color.BLACK)
            secondColor = it.getColor(R.styleable.WordProgressView_secondColor, Color.BLACK)
            radius = it.getDimension(R.styleable.WordProgressView_radius, 0f)
            strokeWidth = it.getDimension(R.styleable.WordProgressView_strokeWidth, 0f)
        }
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = this@WordProgressView.strokeWidth
        shader = SweepGradient(radius, radius, firstColor, secondColor).apply {
            setLocalMatrix(rotationMatrix(-90f, radius, radius))
        }
    }

    private val dashPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = firstColor
        strokeCap = Paint.Cap.ROUND
        strokeWidth = dashWidth
    }

    private var progress: Int = 60
    private val path = Path()
    private val size = radius * 2

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(size.toInt(), size.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        val angle = 360f * progress / 100f
        path.arcTo(0f, 0f, size, size, -90f, angle, true)
        canvas.drawPath(path, progressPaint)
        canvas.drawLine(radius, -strokeWidth, radius, strokeWidth, dashPaint)
    }

    fun setProgress(progress: Int) {
        this.progress = MathUtils.clamp(progress, 0, 100)
        invalidate()
    }
}