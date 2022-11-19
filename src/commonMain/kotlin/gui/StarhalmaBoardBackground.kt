package gui

import com.soywiz.korge.view.Container
import com.soywiz.korge.view.anchor
import com.soywiz.korge.view.graphics
import com.soywiz.korge.view.xy
import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.vector.StrokeInfo
import com.soywiz.korma.geom.vector.line
import com.soywiz.korma.geom.vector.polygon
import halma.StarhalmaStaticBoardMappings

fun Container.starhalmaBoardBackground() = graphics( {
    fun polygonOfCoordinateIndices(vararg indices: Int) {
        polygon(indices.map { i ->
            StarhalmaBoardGuiConfig.fieldCoordinates0[i] -
                    Point(StarhalmaBoardGuiConfig.xFactor, StarhalmaBoardGuiConfig.yFactor)
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
                    val a = StarhalmaBoardGuiConfig.fieldCoordinates0[i] -
                            Point(StarhalmaBoardGuiConfig.xFactor, StarhalmaBoardGuiConfig.yFactor)
                    val b = StarhalmaBoardGuiConfig.fieldCoordinates0[n] -
                            Point(StarhalmaBoardGuiConfig.xFactor, StarhalmaBoardGuiConfig.yFactor)
                    line(a, b)
                }
            }
        }
    }
} ) {
    xy(StarhalmaBoardGuiConfig.midpoint)
    anchor(0.5, 0.5)
}
