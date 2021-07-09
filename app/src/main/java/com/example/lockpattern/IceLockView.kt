package com.example.lockpattern

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.GridLayout


class IceLockView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridLayout(context, attributeSet, defStyleAttr) {

    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mPath: Path = Path()
    private var mRowAndColumnCount = 3
    private val listDots: MutableList<DotView> = mutableListOf()
    private val listDotsSelected: MutableList<DotView> = mutableListOf()
    private var lastX: Float = 0f
    private var lastY: Float = 0f
    private var newX: Float = 0f
    private var newY: Float = 0f
    private var paddingAreaDot: Float = 0.2f

    private var listener: OnIceLockListener? = null

    private var attrColorDot = Color.GRAY
    private var attrColorLine = Color.GRAY
    private var attrColorLineAfter =  Color.GRAY
    private var attrColorDotAfter = Color.GRAY
    private var attrColorLineSelected = Color.BLUE
    private var attrColorDotSelected = Color.BLUE
    private var timeDelayReset: Long = 1000
    private lateinit var listIdDots:MutableList<Int>
    private lateinit var listIdDotsSuccess:MutableList<Int>

    init {
        context.theme.obtainStyledAttributes(attributeSet, R.styleable.IceLockView, 0, 0)
            .apply {
                try {
                    attrColorDot = getColor(R.styleable.IceLockView_ice_dotColor, Color.GRAY)
                    attrColorLine = getColor(R.styleable.IceLockView_ice_lineColor, Color.GRAY)
                    attrColorLineSelected =
                        getColor(R.styleable.IceLockView_ice_selectedLineColor, Color.BLUE)
                    attrColorDotSelected =
                        getColor(R.styleable.IceLockView_ice_selectedDotColor, Color.GRAY)
                } finally {
                    recycle()
                }
            }
        mPaint.color = attrColorLine
        mPaint.strokeWidth = 10f
        mPaint.style = Paint.Style.STROKE
        mPaint.isAntiAlias = true
        rowCount = mRowAndColumnCount
        columnCount = mRowAndColumnCount
        listIdDots = mutableListOf()
        listIdDotsSuccess = mutableListOf()
        setUpDots()
    }

    private fun setUpDots() {
        for (i in 0 until mRowAndColumnCount) {
            for (j in 0 until mRowAndColumnCount) {
                val dotView = DotView(context, attrColorDot)
                addView(dotView)
                listDots.add(dotView)
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            val mX = event.x
            val mY = event.y
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val firstDot = getDotJustSelected(mX, mY)
                    firstDot?.let {
                        listDotsSelected.add(it)
                    }
                    invalidate()
                }
                MotionEvent.ACTION_MOVE -> {
                    drawLineDotToDot(mX, mY)
                    newX = mX
                    newY = mY
                    if (lastX != 0f || lastY != 0f) {
                        invalidate()
                    } else {
                    }
                }
                MotionEvent.ACTION_UP -> {
                    reset()
                }
                else -> {
                }
            }
        }

        return true
    }

    private fun reset() {
        listener?.onListenerMatrix(listIdDots)
        mPaint.color = attrColorLineAfter
        for(dot in listDotsSelected){
            dot.onChangeColorDot(attrColorDotAfter)
        }
        newY = lastY
        newX = lastX
        invalidate()
        postDelayed({
            newY = 0f
            lastX = 0f
            newX = 0f
            lastY = 0f
            mPath.reset()
            mPaint.color = attrColorLine
            listDotsSelected.clear()
            for (dot in listDots) {
                dot.onChangeColorDot(attrColorDot)
            }
            invalidate()
        }, timeDelayReset)
        listIdDots.clear()
    }

    private fun getDotJustSelected(mX: Float, mY: Float): DotView? {
        for ((index, dot) in listDots.withIndex()) {
            if (mX.toInt() >= dot.left && mX.toInt() <= dot.right && mY.toInt() >= dot.top && mY.toInt() <= dot.bottom) {
                lastY = (dot.top + (dot.bottom - dot.top) / 2).toFloat()
                lastX = (dot.left + (dot.right - dot.left) / 2).toFloat()
                newX = lastX
                newY = lastY
                dot.onChangeColorDot(attrColorDotSelected)
                listIdDots.add(index)
                mPath.moveTo(lastX, lastY)
                return dot
            }
        }
        return null
    }

    private fun drawLineDotToDot(mX: Float, mY: Float) {
        for ((index, dot) in listDots.withIndex()) {
            if (!listDotsSelected.contains(dot)) {
                val innerPadding = dot.width * paddingAreaDot
                if (mX.toInt() >= dot.left + innerPadding
                    && mX.toInt() <= dot.right - innerPadding
                    && mY.toInt() >= dot.top + innerPadding
                    && mY.toInt() <= dot.bottom - innerPadding
                ) {
                    lastX = (dot.left + (dot.right - dot.left) / 2).toFloat()
                    lastY = (dot.top + (dot.bottom - dot.top) / 2).toFloat()
                    mPath.lineTo(lastX, lastY)
                    mPath.moveTo(lastX, lastY)
                    listDotsSelected.add(dot)
                    dot.onChangeColorDot(attrColorDotSelected)
                    listIdDots.add(index)
                }
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        canvas?.drawLine(lastX, lastY, newX, newY, mPaint)
        canvas?.drawPath(mPath, mPaint)
    }

    fun onIceLockListener(onIceLockListener: OnIceLockListener) {
        listener = onIceLockListener
    }

    fun setColorAfterDraw(dotColor: Int, lineColor: Int) {
        attrColorDotAfter = dotColor
        attrColorLineAfter = lineColor
    }

    fun setTimeDelayAfterDraw(millis: Long) {
        timeDelayReset = millis
    }

    interface OnIceLockListener {
        fun onListenerMatrix(idDots: MutableList<Int>)
    }
}