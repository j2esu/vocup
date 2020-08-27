package ru.uxapps.vocup.feature.dictionary

import android.content.Context
import android.graphics.*
import android.graphics.Paint.*
import android.graphics.Shader.TileMode
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.use
import androidx.core.math.MathUtils
import kotlin.math.min

class WordProgressView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    companion object {
        private const val DEF_SIZE = 100
    }

    private var progressStartColor: Int = 0
    private var progressEndColor: Int = 0
    private var progressStrokeWidth: Float = 0f
    private var outlineColor: Int = 0
    private var outlineStrokeWidth: Float = 0f
    private var progress: Int = 0

    init {
        context.obtainStyledAttributes(attrs, R.styleable.WordProgressView).use {
            progressStartColor = it.getColor(R.styleable.WordProgressView_progressStartColor, Color.BLACK)
            progressEndColor = it.getColor(R.styleable.WordProgressView_progressEndColor, Color.BLACK)
            progressStrokeWidth = it.getDimension(R.styleable.WordProgressView_progressWidth, 0f)
            outlineColor = it.getColor(R.styleable.WordProgressView_outlineColor, Color.BLACK)
            outlineStrokeWidth = it.getDimension(R.styleable.WordProgressView_outlineWidth, 0f)
            progress = it.getInteger(R.styleable.WordProgressView_progress, 0)
        }
    }

    private val progressPaint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Style.STROKE
        strokeCap = Cap.ROUND
        strokeWidth = progressStrokeWidth
    }

    private val outlinePaint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Style.STROKE
        strokeCap = Cap.ROUND
        strokeWidth = outlineStrokeWidth
        color = outlineColor
    }

    private var size = 0f
    private var prevSize = 0f
    private var cen = 0f
    private var rad = 0f
    private val path = Path()

    private fun updateShader() {
        progressPaint.shader = LinearGradient(
            cen, size, cen, 0f, progressStartColor, progressEndColor, TileMode.MIRROR
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        size = min(getSpecSize(widthMeasureSpec), getSpecSize(heightMeasureSpec)).toFloat()
        if (size != prevSize) {
            rad = (size - progressStrokeWidth) / 2
            cen = size / 2
            updateShader()
            prevSize = size
        }
        setMeasuredDimension(size.toInt(), size.toInt())
    }

    private fun getSpecSize(spec: Int): Int {
        return when (MeasureSpec.getMode(spec)) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(spec)
            MeasureSpec.AT_MOST -> min(DEF_SIZE, MeasureSpec.getSize(spec))
            else -> DEF_SIZE
        }
    }

    override fun onDraw(canvas: Canvas) {
        // draw outline
        canvas.drawCircle(cen, cen, rad, outlinePaint)
        // draw progress
        val angle = 360f * progress / 100f
        path.reset()
        path.arcTo(cen - rad, cen - rad, cen + rad, cen + rad, -90f, angle, true)
        canvas.drawPath(path, progressPaint)
    }

    fun setProgress(value: Int) {
        if (progress != value) {
            progress = MathUtils.clamp(value, 0, 100)
            invalidate()
        }
    }
}