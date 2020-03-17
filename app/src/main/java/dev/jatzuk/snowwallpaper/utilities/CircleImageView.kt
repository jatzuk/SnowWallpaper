package dev.jatzuk.snowwallpaper.utilities

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap

class CircleImageView : AppCompatImageView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private val desiredSize = 180
    private val radius = desiredSize / 2f - 20
    var bitmap: Bitmap? = null

    fun setPreviewImage(drawable: Drawable) {
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
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(desiredSize, desiredSize)
    }

    private fun getCroppedBitmap(bitmap: Bitmap): Bitmap {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 256, 256, false)
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

        paint.apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        canvas.drawCircle(scaledBitmap.width / 2f, scaledBitmap.height / 2f, radius, paint)

        return output
    }
}
