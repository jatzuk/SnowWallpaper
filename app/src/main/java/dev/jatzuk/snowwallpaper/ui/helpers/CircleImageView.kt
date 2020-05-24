package dev.jatzuk.snowwallpaper.ui.helpers

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import dev.jatzuk.snowwallpaper.R

class CircleImageView : AppCompatImageView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private var desiredSize =
        resources.getDimensionPixelSize(R.dimen.circle_image_view_default_size)
    private var radius = desiredSize / 2f - 20
    private var bitmap: Bitmap? = null

    var isStrokeEnabled = false
    private var strokePaint: Paint? = null

    fun setPreviewImage(drawable: Drawable, size: Int) {
        desiredSize = size
        radius = desiredSize / 2f - 20
        bitmap = getCroppedBitmap(drawable.toBitmap())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (bitmap != null) {
            canvas.drawBitmap(
                bitmap!!,
                (width / 2f) - bitmap!!.width / 2,
                (height / 2f) - bitmap!!.height / 2,
                null
            )

            if (isStrokeEnabled) drawStroke(canvas)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(desiredSize, desiredSize)
    }

    private fun getCroppedBitmap(bitmap: Bitmap): Bitmap {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, desiredSize, desiredSize, false)
        val output =
            Bitmap.createBitmap(scaledBitmap.width, scaledBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        val rect = Rect(0, 0, scaledBitmap.width, scaledBitmap.height)

        paint.isAntiAlias = true
        canvas.run {
            drawARGB(0, 0, 0, 0)
            drawCircle(scaledBitmap.width / 2f, scaledBitmap.height / 2f, radius, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            drawBitmap(scaledBitmap, rect, rect, paint)
        }

        return output
    }

    fun setStroke(width: Int, color: Int) {
        strokePaint = Paint().apply {
            this.color = color
            style = Paint.Style.STROKE
            strokeWidth = width.toFloat()
        }
        isStrokeEnabled = true
        invalidate()
    }

    fun disableStroke() {
        strokePaint = null
        isStrokeEnabled = false
        invalidate()
    }

    private fun drawStroke(canvas: Canvas) {
        canvas.drawCircle(desiredSize / 2f, desiredSize / 2f, radius, strokePaint!!)
    }
}
