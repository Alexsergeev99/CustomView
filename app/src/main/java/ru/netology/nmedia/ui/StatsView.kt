package ru.netology.nmedia.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.CycleInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.content.withStyledAttributes
import ru.netology.nmedia.R
import ru.netology.nmedia.util.AndroidUtils
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

class StatsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(
    context,
    attrs,
    defStyleAttr,
    defStyleRes,
) {
    var data: List<Float> = emptyList()
        set(value) {
            field = value
            update()
        }

    private var radius = 0F
    private var center = PointF()
    private var oval = RectF()
    private var textSize = AndroidUtils.dp(context, 40F).toFloat()
    private var lineWidth = AndroidUtils.dp(context, 16F).toFloat()
    private var colors = emptyList<Int>()
    private fun smartCounter(sum: Float): Float = if (sum > 1) sum.pow(-1) else 1F

    private var progress = 0F
    private var valueAnimator: ValueAnimator? = null

    private val paint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        strokeWidth = lineWidth
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        textSize = this@StatsView.textSize
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.StatsView) {
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth)
            textSize = getDimension(R.styleable.StatsView_fontSize, textSize)
            val resId = getResourceId(R.styleable.StatsView_colors, 0)
            colors = resources.getIntArray(resId).toList()
        }
    }

    private fun update() {
        valueAnimator?.let {
            it.removeAllListeners()
            it.cancel()
        }
        progress = 0F

        valueAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
            addUpdateListener { anim ->
                progress = anim.animatedValue as Float
                invalidate()
            }
            duration = 3000
            interpolator = OvershootInterpolator(2f)
        }.also {
            it.start()
        }
    }

    private fun randomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWidth / 2
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius,
            center.y - radius,
            center.x + radius,
            center.y + radius
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) {
            return
        }

        var startFrom = -90F
        data.forEachIndexed { index, datum ->
            val angle = 360F * datum * smartCounter(data.sum())
            paint.color = colors.getOrNull(index) ?: randomColor()
            canvas.drawArc(oval, startFrom, angle * progress, false, paint)
            startFrom += angle
        }
        paint.color = colors[0]

        canvas.drawText(
            "%.2f%%".format(data.sum() * progress * 100 * smartCounter(data.sum())),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint,
        )
        canvas.drawPoint(center.x, center.y - radius, paint)
    }
}