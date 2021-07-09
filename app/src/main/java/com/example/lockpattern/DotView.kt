package com.example.lockpattern

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.View

class DotView(context:Context,color:Int) :View(context) {
    private val mPaint = Paint()

    init {
        mPaint.color = color
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val cellWidth = MeasureSpec.getSize(widthMeasureSpec) / 3
        setMeasuredDimension(cellWidth, cellWidth)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val centerY = height / 2
        val centerX = width / 2
        canvas?.drawCircle(centerX.toFloat(),centerY.toFloat(),50f,mPaint)
    }

    fun onChangeColorDot(color:Int){
        mPaint.color = color
        invalidate()
    }
}