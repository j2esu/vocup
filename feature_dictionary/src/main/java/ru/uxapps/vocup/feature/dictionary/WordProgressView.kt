package ru.uxapps.vocup.feature.dictionary

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.use
import androidx.core.graphics.rotationMatrix
import androidx.core.math.MathUtils
import kotlin.math.max

class WordProgressView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var radius: Float = 0f
    private var progressStartColor: Int = 0
    private var progressEndColor: Int = 0
    private var progressWidth: Float = 0f
    private var outlineColor: Int = 0
    private var outlineWidth: Float = 0f
    private var progress: Int = 60
    private var progressAlphaOffset: Float = 0f

    init {
        context.obtainStyledAttributes(attrs, R.styleable.WordProgressView).use {
            radius = it.getDimension(R.styleable.WordProgressView_radius, 0f)
            progressStartColor = it.getColor(R.styleable.WordProgressView_progressStartColor, Color.BLACK)
            progressEndColor = it.getColor(R.styleable.WordProgressView_progressEndColor, Color.BLACK)
            progressWidth = it.getDimension(R.styleable.WordProgressView_progressBarWidth, 0f)
            outlineColor = it.getColor(R.styleable.WordProgressView_outlineColor, Color.BLACK)
            outlineWidth = it.getDimension(R.styleable.WordProgressView_outlineWidth, 0f)
            progress = it.getInteger(R.styleable.WordProgressView_progress, 0)
            progressAlphaOffset = it.getFloat(R.styleable.WordProgressView_progressAlphaOffset, 0f)
        }
    }

    private val size = radius * 2
    private val progressStartOffset = progressWidth
    private val outlineOffset = progressStartOffset

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = progressWidth
        val colors = intArrayOf(Color.TRANSPARENT, progressStartColor, progressEndColor)
        val pos = floatArrayOf(0f, progressAlphaOffset, 1f)
        shader = SweepGradient(radius, radius, colors, pos).apply {
            setLocalMatrix(rotationMatrix(-90f + progressStartOffset / 2, radius, radius))
        }
    }

    private val outlinePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = outlineWidth
        color = outlineColor
    }

    private val path = Path()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(size.toInt(), size.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        // draw progress
        var angle = (360f - progressStartOffset) * progress / 100f
        path.reset()
        path.arcTo(0f, 0f, size, size, -90f + progressStartOffset, angle, true)
        canvas.drawPath(path, progressPaint)
        // draw outline
        angle = max(0f, 360 - angle - progressStartOffset - outlineOffset)
        path.reset()
        path.arcTo(0f, 0f, size, size, -90f, -angle, true)
        canvas.drawPath(path, outlinePaint)
    }

    fun setProgress(progress: Int) {
        this.progress = MathUtils.clamp(progress, 0, 100)
        invalidate()
    }
}