package gui

import com.soywiz.korge.ui.UIButton
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.PointArrayList
import com.soywiz.korma.geom.degrees
import com.soywiz.korma.geom.vector.line
import com.soywiz.korma.geom.vector.polygon
import halma.Board
import halma.Move
import halma.StarhalmaBoard
import halma.StarhalmaStaticBoardMappings
import halma.StarhalmaStaticBoardMappings.extendedHome
import halma.StarhalmaStaticBoardMappings.fieldsSize
import kotlin.math.sqrt


interface BoardGui: Board {
    val guiFields: List<FieldGui>
    val goButton: UIButton
    fun panAt(fieldIdx: Int): Pan
}

fun Container.starhalmaBoardGui(
    starhalmaBoard: StarhalmaBoard = StarhalmaBoard()
) = StarhalmaBoardGui(starhalmaBoard).addTo(this)

class StarhalmaBoardGui(
    private val starhalmaBoard: StarhalmaBoard = StarhalmaBoard()
): Container(), Board by starhalmaBoard, BoardGui {
    init { scaleY = 0.82 }

    val backg = sgraphics {
        fun polygonOfCoordinateIndices(vararg indices: Int) {
            polygon(indices.map { i -> fieldCoordinates0[i] - Point(xFactor, yFactor) })
        }
        fill(Colors.RED) {
            polygonOfCoordinateIndices(0, 18, 14)
            polygonOfCoordinateIndices(102, 106, 120)
        }
        fill(Colors.BLUE) {
            polygonOfCoordinateIndices(18, 22, 64)
            polygonOfCoordinateIndices(56, 102, 98)
        }
        fill(Colors.GREEN) {
            polygonOfCoordinateIndices(64, 110, 106)
            polygonOfCoordinateIndices(10, 14, 56)
        }
        stroke(Colors.BLACK, StrokeInfo(thickness = 3.0)) {
            for (i in 0 until StarhalmaStaticBoardMappings.fieldsSize) {
                for (n in StarhalmaStaticBoardMappings.fieldNeighbors[i]) {
                    if (n > i) {
                        val a = fieldCoordinates0[i] - Point(xFactor, yFactor)
                        val b = fieldCoordinates0[n] - Point(xFactor, yFactor)
                        line(a, b)
                    }
                }
            }
        }
        xy(midpoint)
        anchor(0.5, 0.5)
    }

    class StarhalmaFieldGui(idx: Int): FieldGui(idx) {
        override var x
            get() = station.x
            set(value) {
                this.forEachChild { it.x = value }
            }

        override var y
            get() = station.y
            set(value) {
                this.forEachChild { it.y = value }
            }

        private val station = circle {
            radius = FIELD_RADIUS
            color = Colors.BLACK
            anchor(0.5, 0.5)
            xy(fieldCoordinates0[idx])
            val innerCircle =
                if (idx !in extendedHome.flatten())
                    circle {
                        radius = FIELD_RADIUS - LINE_THICKNESS
                        color = Colors.YELLOW
                        anchor(0.5, 0.5)
                        xy(fieldCoordinates0[idx])
                    }
                else null
        }

        private val mark = circle {
            radius = MARK_RADIUS
            stroke = Colors.GREEN
            strokeThickness = 20.0
            alpha = 0.5
            visible = false
            anchor(.5, .5)
            xy(fieldCoordinates0[idx])
        }

        override fun mark() { mark.visible = true }
        override fun unMark() { mark.visible = false }
    }

    override val guiFields = List(fieldsSize) { idx ->
        StarhalmaFieldGui(idx).addTo(this)
    }

    val pans: List<Pan>
    init {
        val panList = mutableListOf<Pan>()
        for (i in 0 until fieldsSize) {
            if (fields[i] > 0) {
                val pan = pan(panColors[fields[i] - 1]).xy(fieldCoordinates0[i])
                pan.fieldIdx = i
                panList.add(pan)
            }
        }
        pans = panList
    }

    override val goButton = uiButton {
        enabled = false
        visible = false
        xy(2000.0, 2400.0)
        scaledHeight = 230.0
        scaledWidth = 230.0

        image(goButtonIcon) {
            scaledHeight = 200.0
            scaledWidth = 200.0
        }.centerOn(this)
    }

    private fun spin(angle: Angle, fieldIdx: Int): Point {
        val (angle0, r) = fieldPolar[fieldIdx]
        val angle1 = Angle.fromDegrees(angle0.degrees + angle.degrees - 180.0)
        return Point.fromPolar(midpoint, angle1, r)
    }
    private fun Pan.spin(angle: Angle) { xy(spin(angle, fieldIdx)) }
    private fun StarhalmaFieldGui.spin(angle: Angle) { xy(spin(angle, idx)) }

    var spin = 0.degrees
        set(value) {
            backg.rotation = value
            guiFields.forEach { it.spin(value) }
            pans.forEach { it.spin(value) }
            field = value
        }

    override suspend fun move(move: Move) {
        starhalmaBoard.move(move)
        when (move) {
            is Move.Walk -> {
                val pan = panAt(move.startFieldIdx)
                val p = spin(spin, move.destFieldIdx)
                pan.fieldIdx = move.destFieldIdx
                pan.moveTo(p)
            }
            is Move.Jump -> {
                val pan = panAt(move.startFieldIdx)
                val points = move.destFieldIdxList.map { spin(spin, it) }
                pan.fieldIdx = move.destFieldIdx
                pan.moveTo(points)
            }
        }
    }

    override fun panAt(fieldIdx: Int): Pan {
        for (pan in pans) {
            if (pan.fieldIdx == fieldIdx) return pan
        }
        throw IllegalStateException("No pan at idx $fieldIdx!")
    }

    companion object {
        const val FIELD_RADIUS = 35.0
        const val MARK_RADIUS = 60.0
        const val LINE_THICKNESS = 5.0
        val yFactor = sqrt(3.0) * 100.0
        val xFactor = 100.0

        lateinit var goButtonIcon: Bitmap

        val fieldCoordinates0: List<Point>
        init {
            val coordinates = PointArrayList(fieldsSize)
            var y = 1
            repeat (4) {
                for (x in (13 - it) .. (13 + it) step 2) {
                    coordinates.add(Point(x * xFactor, y * yFactor))
                }
                y++
            }
            repeat (4) {
                for (x in (it + 1) .. (25 - it) step 2) {
                    coordinates.add(Point(x * xFactor, y * yFactor))
                }
                y++
            }
            repeat (5) {
                for (x in (5 - it) .. (21 + it) step 2) {
                    coordinates.add(Point(x * xFactor, y * yFactor))
                }
                y++
            }
            repeat(4) {
                for (x in (10 + it) .. (16 - it) step 2) {
                    coordinates.add(Point(x * xFactor, y * yFactor))
                }
                y++
            }
            fieldCoordinates0 = coordinates.toList()
        }

        val midpoint = fieldCoordinates0[60]
        
        data class Polar(val angle: Angle, val r: Double)
        val fieldPolar = fieldCoordinates0.map {
            Polar(Angle.between(it, midpoint), it.distanceTo(midpoint))
        }

        val panColors = listOf(Colors.DARKRED, Colors.LIGHTSKYBLUE, Colors.DARKGREEN, Colors.VIOLET, Colors.DIMGREY, Colors.BLACK)

        suspend fun initialize() {
            goButtonIcon = resourcesVfs["check_mark.png"].readBitmap()
        }
    }
}