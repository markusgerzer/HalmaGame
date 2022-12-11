package gui

import com.soywiz.korge.input.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.vector.*
import halma.*

fun Graphics.starhalmaMarks() = List(StarhalmaStaticBoardMappings.fieldsSize) {
    circle {
        radius = StarhalmaBoardGuiConfig.MARK_RADIUS
        stroke = Colors.GREEN
        strokeThickness = StarhalmaBoardGuiConfig.MARK_LINE_THICKNESS
        alpha = 0.5
        visible = false
        anchor(.5, .5)
        val offset = Point(strokeThickness, strokeThickness) / 2
        xy(StarhalmaBoardGuiConfig.fieldCoordinates0[it] - StarhalmaBoardGuiConfig.midpoint + offset)
    }
}


fun Container.starhalmaBoardBackground(
    starhalmaBoardGui: StarhalmaBoardGui
) = graphics( {
    val offset = Point(
        StarhalmaBoardGuiConfig.xFactor - StarhalmaBoardGuiConfig.FIELD_RADIUS,
        StarhalmaBoardGuiConfig.yFactor - StarhalmaBoardGuiConfig.FIELD_RADIUS
    )

    fun polygonOfCoordinateIndices(vararg indices: Int) {
        polygon(indices.map { i ->
            StarhalmaBoardGuiConfig.fieldCoordinates0[i] - offset
        })
    }

    // Red Homes
    fill(Colors.RED) {
        polygonOfCoordinateIndices(0, 18, 14)
        polygonOfCoordinateIndices(102, 106, 120)
    }
    // Blue Homes
    fill(Colors.BLUE) {
        polygonOfCoordinateIndices(18, 22, 64)
        polygonOfCoordinateIndices(56, 102, 98)
    }
    // Green Homes
    fill(Colors.GREEN) {
        polygonOfCoordinateIndices(64, 110, 106)
        polygonOfCoordinateIndices(10, 14, 56)
    }
    // Lines
    stroke(Colors.BLACK, StrokeInfo(thickness = StarhalmaBoardGuiConfig.LINE_THICKNESS)) {
        for (i in 0 until StarhalmaStaticBoardMappings.fieldsSize) {
            for (n in StarhalmaStaticBoardMappings.fieldNeighbors[i]) {
                if (n > i) {
                    val a = StarhalmaBoardGuiConfig.fieldCoordinates0[i] - offset
                    val b = StarhalmaBoardGuiConfig.fieldCoordinates0[n] - offset
                    line(a, b)
                }
            }
        }
    }
    // Fields
    fill(Colors.BLACK) {
        for (i in 0 until StarhalmaStaticBoardMappings.fieldsSize) {
            circle(
                StarhalmaBoardGuiConfig.fieldCoordinates0[i] - offset,
                StarhalmaBoardGuiConfig.FIELD_RADIUS
            )
        }
    }
    fill(Colors.YELLOW) {
        for (i in StarhalmaStaticBoardMappings.middleFields)
            circle(
                StarhalmaBoardGuiConfig.fieldCoordinates0[i] - offset,
                StarhalmaBoardGuiConfig.FIELD_RADIUS - 2 * StarhalmaBoardGuiConfig.LINE_THICKNESS
            )
    }
} ) {
    xy(StarhalmaBoardGuiConfig.midpoint)
    anchor(0.5, 0.5)
    onClick {
        val coordinates = starhalmaBoardGui.getCurrentFieldCoordinates()
        for ((i, p) in coordinates.withIndex()) {
            if(p.distanceTo(it.currentPosStage) <= StarhalmaBoardGuiConfig.FIELD_RADIUS) {
                starhalmaBoardGui.onEmptyFieldClicked(i)
            }
        }
    }
}
