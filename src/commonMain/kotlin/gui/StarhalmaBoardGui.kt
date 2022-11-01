package gui

import com.soywiz.klock.*
import com.soywiz.korge.input.*
import com.soywiz.korge.tween.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.color.*
import com.soywiz.korim.format.*
import com.soywiz.korim.text.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.vector.*
import halma.*
import halma.StarhalmaStaticBoardMappings.extendedHome
import halma.StarhalmaStaticBoardMappings.fieldsSize
import kotlinx.coroutines.sync.*
import misc.*
import ui.*
import kotlin.native.concurrent.*


suspend fun Container.starhalmaBoardGui(
    numberOfPlayers: Int,
    starhalmaBoard: StarhalmaBoard = StarhalmaBoard(numberOfPlayers),
    playerColors: List<RGBA> = StarhalmaBoardGui.defaultPlayerColors,
    playerNames: List<String> = StarhalmaBoardGui.defaultPlayerNames
): StarhalmaBoardGui = StarhalmaBoardGui(numberOfPlayers, starhalmaBoard, playerColors, playerNames).addTo(this)


class StarhalmaBoardGui private constructor(
    numberOfPlayers: Int,
    private val starhalmaBoard: StarhalmaBoard = StarhalmaBoard(numberOfPlayers),
    val playerColors: List<RGBA> = defaultPlayerColors,
    val playerNames: List<String> = defaultPlayerNames
): FixedSizeContainer(STAR_HALMA_BOARD_GUI_WIDTH, STAR_HALMA_BOARD_GUI_HEIGHT), Board by starhalmaBoard, BoardGui {

    init { scaleY = SCALE_Y }

    private val animationMutex = Mutex()
    private var spinRequest = false

    private val _exit = SimpleEventSuspend()
    fun onExit(callback: suspend () -> Unit) = _exit.addCallback(callback)

    val backg = graphics( {
        fun polygonOfCoordinateIndices(vararg indices: Int) {
            polygon(indices.map { i -> fieldCoordinates0[i] - Point(xFactor, yFactor) })
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
        stroke(Colors.BLACK, StrokeInfo(thickness = LINE_THICKNESS)) {
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
        } ) {
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
            if (idx !in extendedHome.flatten()) {
                this@StarhalmaFieldGui.circle {
                    radius = FIELD_RADIUS - 2 * LINE_THICKNESS
                    color = Colors.YELLOW
                    anchor(0.5, 0.5)
                    xy(fieldCoordinates0[idx])
                }
            }
        }

        private val mark = circle {
            radius = MARK_RADIUS
            stroke = Colors.GREEN
            strokeThickness = MARK_LINE_THICKNESS
            alpha = 0.5
            visible = false
            anchor(.5, .5)
            val xyOffset = Point(strokeThickness, strokeThickness) / 2
            xy(fieldCoordinates0[idx] + xyOffset)
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
                val pan = pan(playerColors[fields[i] - 1]).xy(fieldCoordinates0[i])
                pan.fieldIdx = i
                panList.add(pan)
            }
        }
        pans = panList
    }

    /*
    private fun correctPans() {
        for (pan in pans) {
            pan.xy(spinF(spin, pan.fieldIdx))
        }
    }
    */

    private val boardElements = listOf(backg) + guiFields + pans
    private val paused get() = backg.speed <= 0.0

    private val pauseText = uiText("P A U S E D") {
        visible = false
        textColor = Colors.BLACK.withAd(.5)
        textSize = MSG_TEXT_SIZE * 3
        textAlignment = TextAlignment.MIDDLE_CENTER
        xy(midpoint - Point(windowBounds.width / 2, windowBounds.height / 2))
    }

    private fun pause() {
        boardElements.forEach { it.speed = 0.0 }
        pauseText.visible = true
    }
    private fun endPause() {
        pauseText.visible = false
        boardElements.forEach { it.speed = 1.0 }
    }
    private fun togglePause() { if (paused) endPause() else pause() }

    var spin = 0.degrees
        set(value) {
            backg.rotation = value
            guiFields.forEach { it.spinF(value) }
            pans.forEach { it.spinF(value) }
            field = value
        }

    private fun spinF(angle: Angle, fieldIdx: Int): Point {
        val (angle0, r) = fieldPolar[fieldIdx]
        val angle1 = Angle.fromDegrees(angle0.degrees + angle.degrees - 180.0)
        return Point.fromPolar(midpoint, angle1, r)
    }
    private fun Pan.spinF(angle: Angle) { xy(spinF(angle, fieldIdx)) }
    private fun StarhalmaFieldGui.spinF(angle: Angle) { xy(spinF(angle, idx)) }

    private suspend fun spinAnimation(angle: Angle) {
        disableSpinButtons()
        spinRequest = true
        animationMutex.withLock {
            spinRequest = false
            tween(::spin[(spin.degrees + angle.degrees).degrees])
            enableSpinButtons()
            delay(1000.milliseconds)
        }
    }


    override val goButton = uiButton("") {
        enabled = false
        visible = false
        scaledHeight = BUTTON_SIZE
        scaledWidth = BUTTON_SIZE
        y = (STAR_HALMA_BOARD_GUI_HEIGHT - height - GO_BUTTON_Y_PADDING) / SCALE_Y
        alignRightToRightOf(this@StarhalmaBoardGui, GO_BUTTON_X_PADDING)

        image(goButtonIcon) {
            scaledHeight = BUTTON_IMAGE_SIZE
            scaledWidth = BUTTON_IMAGE_SIZE
        }.centerOn(this)
    }

    private val spinClockwiseButton = uiButton("") {
        scaledHeight = BUTTON_SIZE
        scaledWidth = BUTTON_SIZE
        alignTopToTopOf(this@StarhalmaBoardGui, BUTTON_PADDING)
        alignLeftToLeftOf(this@StarhalmaBoardGui, BUTTON_PADDING)
        onClick { spinAnimation(60.degrees) }
        image(clockwiseIcon) {
            scaledHeight = BUTTON_IMAGE_SIZE
            scaledWidth = BUTTON_IMAGE_SIZE
        }.centerOn(this)
    }

    private val spinAntiClockwiseButton = uiButton("") {
        scaledHeight = BUTTON_SIZE
        scaledWidth = BUTTON_SIZE
        alignTopToTopOf(spinClockwiseButton)
        alignLeftToRightOf(spinClockwiseButton, BUTTON_PADDING)
        onClick { spinAnimation((-60).degrees) }
        image(antiClockwiseIcon) {
            scaledHeight = BUTTON_IMAGE_SIZE
            scaledWidth = BUTTON_IMAGE_SIZE
        }.centerOn(this)
    }

    private val cancelButton = uiButton("X") {
        scaledWidth = ROUND_TEXT_SIZE
        scaledHeight = ROUND_TEXT_SIZE
        alignTopToTopOf(this@StarhalmaBoardGui, BUTTON_PADDING)
        alignRightToRightOf(this@StarhalmaBoardGui, BUTTON_PADDING)
        onClick {
            val gameWasPaused = paused
            disableButtons()
            pause()
            stage?.confirmBox("Exit Game?", 300.0, 100.0, 20.0, 20.0) {
                onConfirm { _exit() }
                onNoConfirm {
                    if (!gameWasPaused) endPause()
                    enableButtons()
                }
            }
        }
    }

    private val _pauseButton = uiButton("||") {
        scaledWidth = ROUND_TEXT_SIZE
        scaledHeight = ROUND_TEXT_SIZE
        alignTopToTopOf(this@StarhalmaBoardGui, BUTTON_PADDING)
        alignRightToLeftOf(cancelButton, BUTTON_PADDING)
        onClick { togglePause() }
    }

    private val spinButtons = listOf(spinClockwiseButton, spinAntiClockwiseButton)
    private val buttons =  spinButtons + listOf(_pauseButton, cancelButton, goButton)

    private fun disableButtons() { buttons.forEach { it.disable() } }
    private fun enableButtons() { buttons.forEach { it.enable() } }
    private fun disableSpinButtons() { spinButtons.forEach { it.disable() } }
    private fun enableSpinButtons() { spinButtons.forEach { it.enable() } }


    val roundText = uiText("Game starts") {
        textSize = ROUND_TEXT_SIZE
        textColor = Colors.BLACK
        textAlignment = TextAlignment.RIGHT
        alignTopToTopOf(this@StarhalmaBoardGui, BUTTON_PADDING)
        alignRightToLeftOf(_pauseButton, BUTTON_PADDING)
    }

    private val msgBox = roundRect(MSG_BOX_WIDTH, MSG_BOX_HEIGHT, MSG_BOX_RX, MSG_BOX_RX) {
        stroke = Colors.BLACK
        strokeThickness = MSG_BOX_LINE_THICKNESS
        y = (STAR_HALMA_BOARD_GUI_HEIGHT - height) / SCALE_Y
        alignLeftToLeftOf(this@StarhalmaBoardGui, BUTTON_PADDING)
    }

    private val msgText = uiText("") {
        textSize = MSG_TEXT_SIZE
        textColor = Colors.BLACK
        alignLeftToLeftOf(msgBox, MSG_TEXT_PADDING)
        alignTopToTopOf(msgBox, MSG_TEXT_PADDING)
    }


    override fun hookBeforeMove(player: Player<out Board>) {
        roundText.text = "Round ${player.game.round}"

        val playerName = playerNames[player.id - 1]
        msgText.text = when (player) {
            is PlayerAI, is PlayerStupidAI -> "$playerName player\n[Computer] makes\nhis move.\n"
            is PlayerGui -> "$playerName player\nplease make\nyour move.\n"
            else -> "???"
        }
    }

    override suspend fun hookGameEnd(winner: Player<out Board>) {
        val playerName = playerNames[winner.id - 1]
        val playerType = when (winner) {
            is PlayerAI, is PlayerStupidAI -> "[Computer]"
            is PlayerGui -> ""
            else -> "???"
        }
        msgText.text = "$playerName $playerType\nhas won.\nBack to Menu\n"
        val goButtonClicked = Signal<Unit>()
        goButton.onClick {
            goButtonClicked.invoke()
        }
        goButton.visible = true
        goButton.enabled = true
        goButtonClicked.waitOne()
    }

    override suspend fun move(move: Move) {
        starhalmaBoard.move(move)

        animationMutex.lock()
        try {
            while (spinRequest) {
                animationMutex.unlock()
                animationMutex.lock()
            }

            when (move) {
                is Move.Walk -> {
                    val pan = panAt(move.startFieldIdx)
                    val p = spinF(spin, move.destFieldIdx)
                    pan.fieldIdx = move.destFieldIdx
                    pan.moveTo(p)
                }

                is Move.Jump -> {
                    val pan = panAt(move.startFieldIdx)
                    val points = move.destFieldIdxList.map { spinF(spin, it) }
                    pan.fieldIdx = move.destFieldIdx
                    pan.moveTo(points)
                }
            }
        } finally { animationMutex.unlock() }
    }

    override fun panAt(fieldIdx: Int): Pan {
        for (pan in pans) {
            if (pan.fieldIdx == fieldIdx) return pan
        }
        throw IllegalStateException("No pan at idx $fieldIdx!")
    }

    @ThreadLocal
    companion object {
        private const val STAR_HALMA_BOARD_GUI_WIDTH = 512.0 //2600.0
        private const val STAR_HALMA_BOARD_GUI_HEIGHT = 512.0 //2600.0
        private const val SCALE_Y = 0.82
        private const val FIELD_RADIUS = 7.0 //35.0
        private const val MARK_RADIUS = 12.0 //60.0
        private const val LINE_THICKNESS = 0.6 //3.0
        private const val MARK_LINE_THICKNESS = 7.0 * LINE_THICKNESS
        private const val BUTTON_SIZE = 46.0 //230.0
        private const val BUTTON_IMAGE_SIZE = .9 * BUTTON_SIZE
        private const val ROUND_TEXT_SIZE = 16.0 //80.0
        private const val MSG_TEXT_SIZE = 20.0 //96.0
        private const val MSG_BOX_LINE_THICKNESS = 6.0 * LINE_THICKNESS
        private const val BUTTON_PADDING = 17.0 //50.0
        private const val MSG_TEXT_PADDING = 15.0 //75.0
        private const val MSG_BOX_WIDTH = 180 //900
        private const val MSG_BOX_HEIGHT = 100 //500
        private const val MSG_BOX_RX = 4 //20
        private const val GO_BUTTON_X_PADDING = 50 //250
        private const val GO_BUTTON_Y_PADDING = 30 //150
        private const val xFactor = 20.0 //100.0
        private const val yFactor = 1.7320508076 * xFactor //sqrt(3.0) * xFactor

        private lateinit var goButtonIcon: Bitmap
        private lateinit var clockwiseIcon: Bitmap
        private lateinit var antiClockwiseIcon: Bitmap

        private val fieldCoordinates0: List<Point>
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

        private val midpoint = fieldCoordinates0[60]

        private data class Polar(val angle: Angle, val r: Double)
        private val fieldPolar = fieldCoordinates0.map {
            Polar(Angle.between(it, midpoint), it.distanceTo(midpoint))
        }

        val defaultPlayerColors = listOf(
            Colors.DARKRED, Colors.LIGHTSKYBLUE, Colors.DARKGREEN,
            Colors.VIOLET, Colors.DIMGREY, Colors.BLACK
        )
        val defaultPlayerNames = listOf("Red", "Blue", "Green", "Violet", "Grey", "Black")

        private var isInitialized = false
        suspend operator fun invoke (
            numberOfPlayers: Int,
            starhalmaBoard: StarhalmaBoard = StarhalmaBoard(numberOfPlayers),
            playerColors: List<RGBA> = defaultPlayerColors,
            playerNames: List<String> = defaultPlayerNames
        ): StarhalmaBoardGui {
            if (!isInitialized) {
                goButtonIcon = resourcesVfs["check_mark.png"].readBitmap()
                clockwiseIcon = resourcesVfs["clockwise.png"].readBitmap()
                antiClockwiseIcon = resourcesVfs["anti_clockwise.png"].readBitmap()
                isInitialized = true
            }
            return StarhalmaBoardGui(numberOfPlayers, starhalmaBoard, playerColors, playerNames)
        }
    }
}
