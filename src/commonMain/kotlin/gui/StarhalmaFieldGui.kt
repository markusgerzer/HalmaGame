package gui

import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korma.geom.Point
import halma.StarhalmaStaticBoardMappings
import halma.StarhalmaStaticBoardMappings.fieldsSize


fun Container.starhalmaFieldGuiList() = List(fieldsSize) { idx ->
    StarhalmaFieldGui(idx).addTo(this)
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
        radius = StarhalmaBoardGuiConfig.FIELD_RADIUS
        color = Colors.BLACK
        anchor(0.5, 0.5)
        xy(StarhalmaBoardGuiConfig.fieldCoordinates0[idx])
        if (idx !in StarhalmaStaticBoardMappings.extendedHome.flatten()) {
            this@StarhalmaFieldGui.circle {
                radius = StarhalmaBoardGuiConfig.FIELD_RADIUS - 2 * StarhalmaBoardGuiConfig.LINE_THICKNESS
                color = Colors.YELLOW
                anchor(0.5, 0.5)
                xy(StarhalmaBoardGuiConfig.fieldCoordinates0[idx])
            }
        }
    }

    private val mark = circle {
        radius = StarhalmaBoardGuiConfig.MARK_RADIUS
        stroke = Colors.GREEN
        strokeThickness = StarhalmaBoardGuiConfig.MARK_LINE_THICKNESS
        alpha = 0.5
        visible = false
        anchor(.5, .5)
        val xyOffset = Point(strokeThickness, strokeThickness) / 2
        xy(StarhalmaBoardGuiConfig.fieldCoordinates0[idx] + xyOffset)
    }

    override fun mark() { mark.visible = true }
    override fun unMark() { mark.visible = false }
}
