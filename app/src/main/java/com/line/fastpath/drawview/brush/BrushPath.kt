package com.line.fastpath.drawview.brush

import android.graphics.Canvas
import android.graphics.Path
import android.view.MotionEvent
import com.line.fastpath.drawview.brush.paint.PercentPaint
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.hypot
import kotlin.math.sqrt

/**
 * Created zhangjian on 2021/5/14(17:39) in project jyxb_mobile.
 */
class BrushPath(private var pointWidthStandard: Float) {

    //添加曲线列表
    val quadList: MutableList<QuadPath> = mutableListOf()
    private val newestQuadList: MutableList<QuadPath> = mutableListOf()

    class QuadPath(var path: Path, var width: Float)

    fun eventTo(x: Float = 0f, y: Float = 0f, event: Int = 0, widthByPressure: Float) {
        //有压感的走压感，没有压感的走速度推算
        val widthWithPressure = if (widthByPressure == 0f) {
            null
        } else {
            widthByPressure
        }
        when (event) {
            MotionEvent.ACTION_DOWN -> {
                paintStart(x, y)
            }
            MotionEvent.ACTION_MOVE -> paintMove(x, y, widthWithPressure)
            MotionEvent.ACTION_UP -> {
                paintMove(x, y, widthWithPressure)
                paintUp(x, y, widthWithPressure)
            }
        }
    }

    private var drawStarted = false
    private var lastPointWidth = 0f

    private var lastPointX: Float = 0f
    private var lastPointY: Float = 0f

    private var mBezier = Bezier()

    private fun paintStart(x: Float, y: Float) {
        mBezier.reset()
        lastPointX = x
        lastPointY = y
        lastPointWidth = pointWidthStandard
        drawStarted = true
        path.reset()
        path.moveTo(lastPointX, lastPointY)
    }

    private fun paintMove(x: Float, y: Float, widthWithPressure: Float?) {
        if (!drawStarted) return
        if (lastPointX != 0f && lastPointY != 0f) {
            addPoint(x, y, widthWithPressure)
        }
    }

    /**
     * 通过mBezier增加点,速度推算压感
     *
     * @param x
     * @param y
     */
    private fun addPoint(x: Float, y: Float, widthWithPressure: Float?) {
        val mGradualChangeList: MutableList<ControllerPoint> = mutableListOf()
        if (x == lastPointX && y == lastPointY) return
        val curDis = hypot((x - lastPointX).toDouble(), (y - lastPointY).toDouble())
        val deltaZ = sqrt(curDis)
        val curPointWidth = if (widthWithPressure == null) {
            //速度推算代码，如果要去掉直接使用pointWidthStandard即可
//            //没有压感数据
//            //通过斜边的距离推测速度
//            //0-7的时候宽度缩放比例为1（原始宽度），超过7后(封顶13)按比例依次递减到2/5
//            val scale = if (deltaZ < DIS_VEL_CAL_FACTOR) 1f else (-0.1 * deltaZ + 1.7).coerceAtLeast(0.4)
//                .toFloat()
//            pointWidthStandard * scale
            pointWidthStandard
        } else {
            widthWithPressure
        }
        if (mBezier.isEmpty) {
            mBezier.init(lastPointX, lastPointY, lastPointWidth, x, y, curPointWidth)
        } else {
            mBezier.addNode(x, y, curPointWidth)
        }
        val steps: Int = if (curPointWidth == lastPointWidth) {
            //两点的宽度一样的,根据距离增加中间件,当前宽度的1/4作为最小绘制单位
            1.coerceAtLeast((deltaZ / lastPointWidth * 4).toInt())
        } else {
            //宽度变化细分成宽度变化为0.1的各部分
            ceil((abs(lastPointWidth - curPointWidth) * 10).toDouble())
                .toInt()
        }
        var t = 0.0
        while (t < 1.0) {
            val point = mBezier.getPoint(t)
            mGradualChangeList.add(point)
            t += 1.0 / steps
        }
        newestQuadList.clear()
        for (controllerPoint in mGradualChangeList) {
            val curX = controllerPoint.x
            val curY = controllerPoint.y
            val curW = controllerPoint.width
            quadTo(lastPointX, lastPointY, (lastPointX + curX) / 2f, (lastPointY + curY) / 2f, curW)
        }
        quadList.addAll(newestQuadList)
    }

    private val path = Path()

    private fun paintUp(x: Float, y: Float, widthWithPressure: Float?) {
        if (!drawStarted) return
        quadTo((lastPointX + x) / 2f, (lastPointY + y) / 2f, x, y, widthWithPressure ?: lastPointWidth)
        quadList.addAll(newestQuadList)
        drawStarted = false
    }

    private fun quadTo(x1: Float, y1: Float, x2: Float, y2: Float, width: Float) {
        if (lastPointX != x2 || lastPointY != y2) {
            path.quadTo(x1, y1, x2, y2)
            val quadPath = QuadPath(Path(path), width)
            newestQuadList.add(quadPath)
            path.reset()
            path.moveTo(x2, y2)
        }
        lastPointX = x2
        lastPointY = y2
        lastPointWidth = width
    }

    fun drawNewest(canvas: Canvas, paint: PercentPaint) {
        for (quadPath in newestQuadList) {
            paint.faceStrokeWidth = quadPath.width
            canvas.drawPath(quadPath.path, paint)
        }
        newestQuadList.clear()
    }

    companion object {
        private const val DIS_VEL_CAL_FACTOR = 7f
    }

}