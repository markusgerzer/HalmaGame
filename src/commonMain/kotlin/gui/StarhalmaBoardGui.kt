package gui

import com.soywiz.korge.ui.UIButton
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.PointArrayList
import com.soywiz.korma.geom.vector.polygon
import halma.Board
import halma.Move
import halma.StarhalmaBoard
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
    init {
        graphics {
            fun polygonOfCoordinateIndices(vararg indices: Int) {
                polygon(indices.map { i -> fieldCoordinates[i] })
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

            for (i in 0 until fieldsSize) {
                for (n in fieldNeighbors[i]) {
                    if (n > i) {
                        val a = fieldCoordinates[i]
                        val b = fieldCoordinates[n]
                        line(a, b, Colors.BLACK)
                    }
                }
            }
        }
    }

    override val guiFields: List<FieldGui> = List(fieldsSize) { idx ->
        object : FieldGui(idx) {
            private val field = circle {
                radius = FIELD_RADIUS
                color = Colors.BLACK
                xy(fieldCoordinates[idx] - xyOffest)
                if (idx !in extendedHome.flatten())
                    circle {
                        radius = FIELD_RADIUS - LINE_THICKNESS
                        color = Colors.YELLOW
                    }.centerOn(this)
            }

            private val mark = circle {
                radius = MARK_RADIUS
                stroke = Colors.GREEN
                strokeThickness = 20.0
                alpha = 0.5
                visible = false
            }.centerOn(field)

            override fun mark() { mark.visible = true }
            override fun unMark() { mark.visible = false }
        }.addTo(this)
    }

    val pans: List<Pan>
    init {
        val p = mutableListOf<Pan>()
        for (i in 0 until fieldsSize) {
            if (fields[i] > 0) {
                p.add(pan(panColors[fields[i] - 1]).xy(i))
            }
        }
        pans = p
    }

    override val goButton = uiButton {
        enabled = false
        visible = false
        xy(2000.0, 2200.0)
        scaledHeight = 230.0
        scaledWidth = 230.0

        image(goButtonIcon) {
            scaledHeight = 200.0
            scaledWidth = 200.0
        }.centerOn(this)
    }

    override suspend fun move(move: Move) {
        starhalmaBoard.move(move)
        when (move) {
            is Move.Walk -> panAt(move.startFieldIdx).moveTo(move.destFieldIdx)
            is Move.Jump -> panAt(move.startFieldIdx).moveTo(move.destFieldIdxList)
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
        val xyOffest = Point(FIELD_RADIUS, FIELD_RADIUS)
        val yFactor = sqrt(2.0) * 100.0
        val xFactor = 100.0

        lateinit var goButtonIcon: Bitmap

        val fieldCoordinates: List<Point>
        init {
            val coordinates = PointArrayList(fieldsSize)
            var y = 1

            repeat (4) {
                for (x in (12 - it) .. (12 + it) step 2) {
                    coordinates.add(Point(x * xFactor, y * yFactor) + xyOffest)
                }
                y++
            }

            repeat (4) {
                for (x in it .. (24 - it) step 2) {
                    coordinates.add(Point(x * xFactor, y * yFactor) + xyOffest)
                }
                y++
            }

            repeat (5) {
                for (x in (4 - it) .. (20 + it) step 2) {
                    coordinates.add(Point(x * xFactor, y * yFactor) + xyOffest)
                }
                y++
            }

            repeat(4) {
                for (x in (9 + it) .. (15 - it) step 2) {
                    coordinates.add(Point(x * xFactor, y * yFactor) + xyOffest)
                }
                y++
            }

            fieldCoordinates = coordinates.toList()
        }

        val panColors = listOf(Colors.DARKRED, Colors.LIGHTSKYBLUE, Colors.DARKGREEN, Colors.VIOLET, Colors.DIMGREY, Colors.BLACK)
    }
}