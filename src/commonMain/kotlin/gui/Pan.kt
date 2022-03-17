package gui

import com.soywiz.klock.milliseconds
import com.soywiz.korge.input.onClick
import com.soywiz.korge.tween.get
import com.soywiz.korge.tween.tween
import com.soywiz.korge.view.*
import com.soywiz.korim.color.RGBA
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.degrees
import com.soywiz.korma.geom.vector.circle
import com.soywiz.korma.geom.vector.polygon


fun Container.pan(color: RGBA) = Pan(color).addTo(this)

class Pan(color: RGBA): Graphics() {
    companion object {
        const val HEIGHT = 80.0
        const val TOP_RADIUS = 20.0
        const val BOTTOM_RADIUS = 32.0
        const val NECK_RADIUS = 5.0
        val MOVE_TIME = 1000.milliseconds
        val HALF_MOVE_TIME = 500.milliseconds
    }
    var fieldIdx = -1
        private set

    var onClickCallback: (suspend (Pan) -> Unit)? = null
    init {
        onClick { onClickCallback?.let { block -> block(this) } }
    }

    init {
        fill(color) {
            circle(.0, .0, BOTTOM_RADIUS)
            circle(.0, -HEIGHT, TOP_RADIUS)
        }
        fill(color) {
            polygon(listOf(
                Point(-BOTTOM_RADIUS, .0),
                Point(-NECK_RADIUS, -HEIGHT),
                Point(NECK_RADIUS, -HEIGHT),
                Point(BOTTOM_RADIUS, .0)
            ))
        }
    }

    fun xy(fieldIdx: Int): Pan {
        this.fieldIdx = fieldIdx
        return xy(StarhalmaBoardGui.fieldCoordinates[fieldIdx])
    }

    suspend fun tip() {
        tween(::rotation[(-16).degrees], time = HALF_MOVE_TIME)
    }

    suspend fun unTip() {
        tween(::rotation[0.degrees], time = HALF_MOVE_TIME)
    }

    suspend fun moveTo(fieldIdxList: List<Int>) {
        parent?.let {
            removeFromParent()
            addTo(it)
        }
        tip()
        for (fieldIdx in fieldIdxList) {
            tween(
                ::x[x, (x + StarhalmaBoardGui.fieldCoordinates[fieldIdx].x) / 2],
                ::y[y, (y + StarhalmaBoardGui.fieldCoordinates[fieldIdx].y) / 2 - 100],
                time = HALF_MOVE_TIME
            )
            tween(
                ::x[x, StarhalmaBoardGui.fieldCoordinates[fieldIdx].x],
                ::y[y, StarhalmaBoardGui.fieldCoordinates[fieldIdx].y],
                time = HALF_MOVE_TIME
            )
            xy(fieldIdx)
        }
        unTip()

    }

    suspend fun moveTo(fieldIdx: Int) {
        tip()
        tween(
            ::x[x, StarhalmaBoardGui.fieldCoordinates[fieldIdx].x],
            ::y[y, StarhalmaBoardGui.fieldCoordinates[fieldIdx].y],
            time = MOVE_TIME
        )
        unTip()
        xy(fieldIdx)
    }
}