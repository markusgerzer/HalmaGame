package gui

import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.PointArrayList
import halma.StarhalmaStaticBoardMappings

object StarhalmaBoardGuiConfig {
    const val STAR_HALMA_BOARD_GUI_WIDTH = 512.0
    const val STAR_HALMA_BOARD_GUI_HEIGHT = 512.0
    const val SCALE_Y = 0.82
    const val FIELD_RADIUS = 7.0
    const val MARK_RADIUS = 12.0
    const val LINE_THICKNESS = 0.63
    const val MARK_LINE_THICKNESS = 7.0 * LINE_THICKNESS
    const val BUTTON_SIZE = 46.0
    const val BUTTON_IMAGE_SIZE = .9 * BUTTON_SIZE
    const val ROUND_TEXT_SIZE = 16.0
    const val MSG_TEXT_SIZE = 20.0
    const val MSG_BOX_STROKE_THICKNESS = 12.0 * LINE_THICKNESS
    const val BUTTON_PADDING = 17.0
    const val MSG_TEXT_PADDING = 15.0
    const val MSG_BOX_WIDTH = 180
    const val MSG_BOX_HEIGHT = 100
    const val MSG_BOX_RX = 4
    const val GO_BUTTON_X_PADDING = 50
    const val GO_BUTTON_Y_PADDING = 30
    const val xFactor = 20.0
    const val yFactor = 1.7320508076 * xFactor //sqrt(3.0) * xFactor

    val fieldCoordinates0: List<Point>
    init {
        val coordinates = PointArrayList(StarhalmaStaticBoardMappings.fieldsSize)
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

    val defaultPlayerColors = listOf(
        Colors.DARKRED, Colors.LIGHTSKYBLUE, Colors.DARKGREEN,
        Colors.VIOLET, Colors.DIMGREY, Colors.BLACK
    )
    val defaultPlayerNames = listOf("Red", "Blue", "Green", "Violet", "Grey", "Black")
}
