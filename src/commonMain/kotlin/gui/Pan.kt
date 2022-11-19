package gui

import com.soywiz.klock.*
import com.soywiz.korge.input.*
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.vector.*


fun Container.pan(
    color: RGBA,
    block: @ViewDslMarker Pan.() -> Unit = {}
) = Pan(color).addTo(this).apply(block)

class Pan(color: RGBA): Container() {
    companion object {
        private const val HEIGHT = 32.0 //16.0 //80.0
        private const val TOP_RADIUS = 8.0 //4.0 //20.0
        private const val BOTTOM_RADIUS = 14.0 //6.4 //32.0
        private const val NECK_RADIUS = 2.0 //1.0 //5.0
        private const val JUMP_HEIGHT = 40.0 //20.0 //100.0
        private val MOVE_TIME = 1000.milliseconds
        private val HALF_MOVE_TIME = 500.milliseconds
    }
    var fieldIdx = -1
    var onClickCallback: (suspend (Pan) -> Unit)? = null

    init {
        graphics {
            fill(color) {
                circle(.0, .0, BOTTOM_RADIUS)
                circle(.0, -HEIGHT, TOP_RADIUS)
            }

            fill(color) {
                polygon(
                    listOf(
                        Point(-BOTTOM_RADIUS, .0),
                        Point(-NECK_RADIUS, -HEIGHT),
                        Point(NECK_RADIUS, -HEIGHT),
                        Point(BOTTOM_RADIUS, .0)
                    )
                )
            }

            onClick { onClickCallback?.let { block -> block(this@Pan) } }
        }
    }

    suspend fun tip() {
        tween(::rotation[(-16).degrees], time = HALF_MOVE_TIME)
    }

    suspend fun unTip() {
        tween(::rotation[0.degrees], time = HALF_MOVE_TIME)
    }

    suspend fun moveTo(points: List<Point>) {
        parent?.let {
            removeFromParent()
            addTo(it)
        }
        tip()
        for (p in points) {
            tween(
                ::x[x, (x + p.x) / 2],
                ::y[y, (y + p.y) / 2 - JUMP_HEIGHT],
                time = HALF_MOVE_TIME
            )
            tween(
                ::x[x, p.x],
                ::y[y, p.y],
                time = HALF_MOVE_TIME
            )
        }
        unTip()

    }

    suspend fun moveTo(p: Point) {
        tip()
        tween(
            ::x[x, p.x],
            ::y[y, p.y],
            time = MOVE_TIME
        )
        unTip()
    }
}
