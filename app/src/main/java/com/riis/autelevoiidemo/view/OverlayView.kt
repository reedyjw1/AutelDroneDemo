package com.riis.autelevoiidemo.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.riis.autelevoiidemo.model.BoundingBox

class OverlayView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    var boxes: List<BoundingBox> = mutableListOf()
    lateinit var title: String
    private var boxPaint = Paint()
    private var textPaint = Paint()
    private var boxTextPaint = Paint()

    init {
        boxPaint.color = Color.YELLOW
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeWidth = 12.0f
        boxPaint.strokeCap = Paint.Cap.ROUND
        boxPaint.strokeJoin = Paint.Join.ROUND
        boxPaint.strokeMiter = 100.0f

        boxTextPaint.color = Color.YELLOW
        boxTextPaint.style = Paint.Style.FILL

        textPaint.color = Color.BLACK
        textPaint.textSize = 12.0f
        textPaint.textAlign = Paint.Align.CENTER
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        canvas?.let {
            boxes.forEach { box ->
                val rect = RectF(box.startX, box.startY, box.endX, box.endY)
                //val rectText = RectF(box.startX, box.startY+50, box.endX, box.startY)
                canvas.drawRect(rect, boxPaint)
                //canvas.drawRect(rectText, boxTextPaint)
                //canvas.drawText(title, box.startX, box.startY+25, textPaint)
            }
        }
    }
}
