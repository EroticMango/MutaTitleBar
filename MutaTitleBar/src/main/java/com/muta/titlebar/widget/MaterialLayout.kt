package com.muta.titlebar.widget

import android.widget.RelativeLayout
import android.view.MotionEvent
import android.view.ViewGroup
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.muta.titlebar.R


/**
 * MaterialLayout是模拟Android 5.0中View被点击的波纹效果的布局，与其他的模拟Material
 * Desigin效果的View不同，所有在MaterialLayout布局下的子视图被点击时都会产生波纹效果,而不是某个特定的View才会有这样的效果.
 *
 * Created by YBJ on 2017/7/5.
 */
class MaterialLayout : RelativeLayout {
    private val DEFAULT_RADIUS = 10
    private val DEFAULT_FRAME_RATE = 10
    private val DEFAULT_DURATION = 200
    private val DEFAULT_ALPHA = 255
    private val DEFAULT_SCALE = 0.8f
    private val DEFAULT_ALPHA_STEP = 5

    /**
     * 动画帧率
     */
    private var mFrameRate = DEFAULT_FRAME_RATE
    /**
     * 渐变动画持续时间
     */
    private var mDuration = DEFAULT_DURATION
    /**

     */
    private val mPaint = Paint()
    /**
     * 被点击的视图的中心点
     */
    private var mCenterPoint: Point? = null
    /**
     * 视图的Rect
     */
    private var mTargetRectf: RectF? = null
    /**
     * 起始的圆形背景半径
     */
    private var mRadius = DEFAULT_RADIUS
    /**
     * 最大的半径
     */
    private var mMaxRadius = DEFAULT_RADIUS

    /**
     * 渐变的背景色
     */
    private var mCirclelColor = Color.LTGRAY
    /**
     * 每次重绘时半径的增幅
     */
    private var mRadiusStep = 1
    /**
     * 保存用户设置的alpha值
     */
    private var mBackupAlpha: Int = 0

    /**
     * 圆形半径针对于被点击视图的缩放比例,默认为0.8
     */
    private var mCircleScale = DEFAULT_SCALE
    /**
     * 颜色的alpha值, (0, 255)
     */
    private var mColorAlpha = DEFAULT_ALPHA
    /**
     * 每次动画Alpha的渐变递减值
     */
    private var mAlphaStep = DEFAULT_ALPHA_STEP

    private var mTargetView: View? = null

    /**
     * @param context
     */
    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context!!, attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context!!, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        if (isInEditMode) {
            return
        }

        if (attrs != null) {
            initTypedArray(context, attrs)
        }

        initPaint()

        this.setWillNotDraw(false)
        this.isDrawingCacheEnabled = true
    }

    private fun initTypedArray(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.MaterialLayout)
        mCirclelColor = typedArray.getColor(R.styleable.MaterialLayout_color, Color.LTGRAY)
        mDuration = typedArray.getInteger(R.styleable.MaterialLayout_duration,
                DEFAULT_DURATION)
        mFrameRate = typedArray
                .getInteger(R.styleable.MaterialLayout_framerate, DEFAULT_FRAME_RATE)
        mColorAlpha = typedArray.getInteger(R.styleable.MaterialLayout_alpha_color , DEFAULT_ALPHA)
        mCircleScale = typedArray.getFloat(R.styleable.MaterialLayout_scale, DEFAULT_SCALE)

        typedArray.recycle()

    }

    private fun initPaint() {
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        mPaint.color = mCirclelColor
        mPaint.alpha = mColorAlpha

        // 备份alpha属性用于动画完成时重置
        mBackupAlpha = mColorAlpha
    }

    /**
     * 点击的某个坐标点是否在View的内部

     * @param touchView
     * *
     * @param x 被点击的x坐标
     * *
     * @param y 被点击的y坐标
     * *
     * @return 如果点击的坐标在该view内则返回true,否则返回false
     */
    private fun isInFrame(touchView: View, x: Float, y: Float): Boolean {
        initViewRect(touchView)
        return mTargetRectf!!.contains(x, y)
    }

    /**
     * 获取点中的区域,屏幕绝对坐标值,这个高度值也包含了状态栏和标题栏高度

     * @param touchView
     */
    private fun initViewRect(touchView: View) {
        val location = IntArray(2)
        touchView.getLocationOnScreen(location)
        // 视图的区域
        mTargetRectf = RectF(location[0].toFloat(), location[1].toFloat(), (location[0] + touchView.width).toFloat(), (location[1] + touchView.height).toFloat())

    }

    /**
     * 减去状态栏和标题栏的高度
     */
    private fun removeExtraHeight() {
        val location = IntArray(2)
        this.getLocationOnScreen(location)
        // 减去两个该布局的top,这个top值就是状态栏的高度
        mTargetRectf!!.top -= location[1].toFloat()
        mTargetRectf!!.bottom -= location[1].toFloat()
        // 计算中心点坐标
        val centerHorizontal = (mTargetRectf!!.left + mTargetRectf!!.right).toInt() / 2
        val centerVertical = ((mTargetRectf!!.top + mTargetRectf!!.bottom) / 2).toInt()
        // 获取中心点
        mCenterPoint = Point(centerHorizontal, centerVertical)

    }

    private fun findTargetView(viewGroup: ViewGroup, x: Float, y: Float): View? {
        val childCount = viewGroup.childCount
        // 迭代查找被点击的目标视图
        for (i in 0..childCount - 1) {
            val childView = viewGroup.getChildAt(i)
            if (childView is ViewGroup) {
                return findTargetView(childView, x, y)
            } else if (isInFrame(childView, x, y)) { // 否则判断该点是否在该View的frame内
                return childView
            }
        }

        return null
    }

    private fun isAnimEnd(): Boolean {
        return mRadius >= mMaxRadius
    }

    private fun calculateMaxRadius(view: View) {
        // 取视图的最长边
        val maxLength = Math.max(view.width, view.height)
        // 计算Ripple圆形的半径
        mMaxRadius = (maxLength / 2 * mCircleScale).toInt()

        val redrawCount = mDuration / mFrameRate
        // 计算每次动画半径的增值
        mRadiusStep = (mMaxRadius - DEFAULT_RADIUS) / redrawCount
        // 计算每次alpha递减的值
        mAlphaStep = (mColorAlpha - 100) / redrawCount
    }

    /**
     * 处理ACTION_DOWN触摸事件, 注意这里获取的是Raw x, y,
     * 即屏幕的绝对坐标,但是这个当屏幕中有状态栏和标题栏时就需要去掉这些高度,因此得到mTargetRectf后其高度需要减去该布局的top起点
     * ，也就是标题栏和状态栏的总高度.

     * @param event
     */
    private fun deliveryTouchDownEvent(event: MotionEvent) {
        if (event.action == MotionEvent.ACTION_DOWN) {
            mTargetView = findTargetView(this, event.rawX, event.rawY)
            if (mTargetView != null) {
                removeExtraHeight()
                // 计算相关数据
                calculateMaxRadius(mTargetView!!)
                // 重绘视图
                invalidate()
            }
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        deliveryTouchDownEvent(event)
        return super.onInterceptTouchEvent(event)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        // 绘制Circle
        drawRippleIfNecessary(canvas)
    }

    private fun drawRippleIfNecessary(canvas: Canvas) {
        if (isFoundTouchedSubView()) {
            // 计算新的半径和alpha值
            mRadius += mRadiusStep
            mColorAlpha -= mAlphaStep

            // 裁剪一块区域,这块区域就是被点击的View的区域.通过clipRect来获取这块区域,使得绘制操作只能在这个区域范围内的进行,
            // 即使绘制的内容大于这块区域,那么大于这块区域的绘制内容将不可见. 这样保证了背景层只能绘制在被点击的视图的区域
            canvas.clipRect(mTargetRectf)
            mPaint.setAlpha(mColorAlpha)
            // 绘制背景圆形,也就是
            canvas.drawCircle(mCenterPoint!!.x.toFloat(), mCenterPoint!!.y.toFloat(), mRadius.toFloat(), mPaint)
        }

        if (isAnimEnd()) {
            reset()
        } else {
            invalidateDelayed()
        }
    }

    /**
     * 发送重绘消息
     */
    private fun invalidateDelayed() {
        this.postDelayed({ invalidate() }, mFrameRate.toLong())
    }

    /**
     * 判断是否找到被点击的子视图

     * @return
     */
    private fun isFoundTouchedSubView(): Boolean {
        return mCenterPoint != null && mTargetView != null
    }

    private fun reset() {
        mCenterPoint = null
        mTargetRectf = null
        mRadius = DEFAULT_RADIUS
        mColorAlpha = mBackupAlpha
        mTargetView = null
        invalidate()
    }
}