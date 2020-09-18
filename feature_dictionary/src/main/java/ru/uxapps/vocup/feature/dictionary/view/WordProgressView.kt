package ru.uxapps.vocup.feature.dictionary.view

import android.content.Context
import android.graphics.*
import android.graphics.Paint.*
import android.graphics.Shader.TileMode
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.use
import androidx.core.math.MathUtils
import androidx.core.view.isVisible
import ru.uxapps.vocup.feature.dictionary.R
import kotlin.math.min

class WordProgressView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    companion object {
        private const val DEF_SIZE = 100
    }

    private var progressStartColor: Int = 0
    private var progressEndColor: Int = 0
    private var progressStrokeWidth: Float = 0f
    private var outlineColor: Int = 0
    private var outlineStrokeWidth: Float = 0f
    private var progress: Int = -1 // to init first redraw, if 0

    private val textView: TextView
    private val imageView: ImageView

    init {
        var progressAttr = 0
        context.obtainStyledAttributes(attrs, R.styleable.WordProgressView).use {
            progressStartColor = it.getColor(R.styleable.WordProgressView_progressStartColor, Color.BLACK)
            progressEndColor = it.getColor(R.styleable.WordProgressView_progressEndColor, Color.GRAY)
            progressStrokeWidth = it.getDimension(R.styleable.WordProgressView_progressWidth, 16f)
            outlineColor = it.getColor(R.styleable.WordProgressView_outlineColor, Color.LTGRAY)
            outlineStrokeWidth = it.getDimension(R.styleable.WordProgressView_outlineWidth, 4f)
            progressAttr = it.getInteger(R.styleable.WordProgressView_progress, 0)
        }
        inflate(context, R.layout.word_progress_inner, this)
        textView = findViewById(R.id.word_progress_inner_text)
        imageView = findViewById(R.id.word_progress_inner_img)
        setProgress(progressAttr)
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
    private val circleRect = RectF()

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
            circleRect.set(cen - rad, cen - rad, cen + rad, cen + rad)
            updateShader()
            prevSize = size
        }
        val sizeSpec = MeasureSpec.makeMeasureSpec(size.toInt(), MeasureSpec.EXACTLY)
        super.onMeasure(sizeSpec, sizeSpec)
    }

    private fun getSpecSize(spec: Int): Int {
        return when (MeasureSpec.getMode(spec)) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(spec)
            MeasureSpec.AT_MOST -> min(DEF_SIZE, MeasureSpec.getSize(spec))
            else -> DEF_SIZE
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        // draw outline
        canvas.drawOval(circleRect, outlinePaint)
        // draw progress
        val angle = 360f * progress / 100
        if (angle == 360f) { // arc 360 draws nothing
            canvas.drawOval(circleRect, progressPaint)
        } else {
            path.reset()
            path.arcTo(circleRect, -90f, angle, true)
            canvas.drawPath(path, progressPaint)
        }
        super.dispatchDraw(canvas)
    }

    fun setProgress(value: Int) {
        if (progress != value) {
            progress = MathUtils.clamp(value, 0, 100)
            textView.isVisible = progress < 100
            textView.text = progress.toString()
            imageView.isVisible = progress == 100
            invalidate()
        }
    }
}