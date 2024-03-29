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
import halma.*
import kotlinx.coroutines.sync.*
import ui.*
import kotlin.native.concurrent.*

suspend fun Container.starhalmaBoardGui(
    numberOfPlayers: Int,
    starhalmaBoard: StarhalmaBoard = StarhalmaBoard(numberOfPlayers),
    playerColors: List<RGBA> = StarhalmaBoardGuiConfig.defaultPlayerColors,
    playerNames: List<String> = StarhalmaBoardGuiConfig.defaultPlayerNames
): StarhalmaBoardGui = StarhalmaBoardGui(numberOfPlayers, starhalmaBoard, playerColors, playerNames).addTo(this)


class StarhalmaBoardGui private constructor(
    numberOfPlayers: Int,
    private val starhalmaBoard: StarhalmaBoard = StarhalmaBoard(numberOfPlayers),
    val playerColors: List<RGBA> = StarhalmaBoardGuiConfig.defaultPlayerColors,
    val playerNames: List<String> = StarhalmaBoardGuiConfig.defaultPlayerNames
): FixedSizeContainer(
    StarhalmaBoardGuiConfig.STAR_HALMA_BOARD_GUI_WIDTH,
    StarhalmaBoardGuiConfig.STAR_HALMA_BOARD_GUI_HEIGHT
), Board by starhalmaBoard, BoardGui {

    init {
        require(numberOfPlayers <= playerColors.size)
        require(numberOfPlayers <= playerNames.size)
    }

    private val animationMutex = Mutex()
    private var spinRequest = false

    override val onExit = Signal<Unit>()

    private val boardElements = /*zoomContainer.*/container {
        scaleY = StarhalmaBoardGuiConfig.SCALE_Y
        // addZoomComponent(ZoomComponent(this))
        // ZoomComponent is brocken !
    }

    override val onEmptyFieldClicked = AsyncSignal<Int>()
    init { onEmptyFieldClicked { println("Clicked on Field $it") } }
    private val background = boardElements.starhalmaBoardBackground(this)

    private val marks = background.starhalmaMarks()

    override fun mark(fieldIdx: Int) { marks[fieldIdx].visible = true }
    override fun unMark(fieldIdx: Int) { marks[fieldIdx].visible = false }

    private val pans = boardElements.panList(starhalmaBoard, playerColors)

    private val paused get() = background.speed <= 0.0

    private val pauseText = UIText(S.pause).apply {
        textColor = Colors.BLACK.withAd(.5)
        textSize = StarhalmaBoardGuiConfig.MSG_TEXT_SIZE * 3
        textAlignment = TextAlignment.MIDDLE_CENTER
        centerOn(this@StarhalmaBoardGui)
    }

    private fun pause() {
        boardElements.forEachChild { it.speed = 0.0 }
        pauseText.addTo(this)
    }
    private fun endPause() {
        pauseText.removeFromParent()
        boardElements.forEachChild { it.speed = 1.0 }
    }
    private fun togglePause() { if (paused) endPause() else pause() }

    var spin = 0.degrees
        set(value) {
            background.rotation = value
            for (pan in pans) { pan.xy(spinF(value, pan.fieldIdx)) }
            field = value
        }

    private fun spinF(angle: Angle, fieldIdx: Int): Point {
        val (angle0, r) = StarhalmaBoardGuiConfig.fieldPolar[fieldIdx]
        val angle1 = Angle.fromDegrees(angle0.degrees + angle.degrees - 180.0)
        return Point.fromPolar(StarhalmaBoardGuiConfig.midpoint, angle1, r)
    }

    fun getCurrentFieldCoordinates() = List(StarhalmaStaticBoardMappings.fieldsSize) {
        val p = spinF(spin, it)
        Point(
            p.x,
            p.y * StarhalmaBoardGuiConfig.SCALE_Y
        )
    }

    private val waitingForMoveCompletionText = UIText(S.waitingForMoveCompletion).apply {
        textColor = Colors.BLACK.withAd(.5)
        textSize = StarhalmaBoardGuiConfig.MSG_TEXT_SIZE
        textAlignment = TextAlignment.MIDDLE_CENTER
        centerXOn(this@StarhalmaBoardGui)
        y = this@StarhalmaBoardGui.height * 0.4
    }

    private var spinAnimationIsRunning = false
    private suspend fun spinAnimation(angle: Angle) {
        suspend fun doSpinAnimation() {
            spinRequest = false
            spinAnimationIsRunning = true
            tween(::spin[(spin.degrees + angle.degrees).degrees])
            enableSpinButtons()
            delay(1000.milliseconds)
            spinAnimationIsRunning = false
        }

        disableSpinButtons()
        spinRequest = true
        if (animationMutex.tryLock()) {
            try { doSpinAnimation() }
            finally {
                animationMutex.unlock()
            }
        }
        else {
            if (!spinAnimationIsRunning) waitingForMoveCompletionText.addTo(this)
            animationMutex.withLock {
                waitingForMoveCompletionText.removeFromParent()
                doSpinAnimation()
            }
        }
    }


    override val goButton = uiButton("") {
        enabled = false
        visible = false
        scaledHeight = StarhalmaBoardGuiConfig.BUTTON_SIZE
        scaledWidth = StarhalmaBoardGuiConfig.BUTTON_SIZE
        alignRightToRightOf(this@StarhalmaBoardGui, StarhalmaBoardGuiConfig.GO_BUTTON_X_PADDING)
        alignBottomToBottomOf(this@StarhalmaBoardGui, StarhalmaBoardGuiConfig.GO_BUTTON_Y_PADDING)

        image(goButtonIcon) {
            scaledHeight = StarhalmaBoardGuiConfig.BUTTON_IMAGE_SIZE
            scaledWidth = StarhalmaBoardGuiConfig.BUTTON_IMAGE_SIZE
        }.centerOn(this)
    }

    private val spinClockwiseButton = uiButton("") {
        scaledHeight = StarhalmaBoardGuiConfig.BUTTON_SIZE
        scaledWidth = StarhalmaBoardGuiConfig.BUTTON_SIZE
        alignTopToTopOf(this@StarhalmaBoardGui, StarhalmaBoardGuiConfig.BUTTON_PADDING)
        alignLeftToLeftOf(this@StarhalmaBoardGui, StarhalmaBoardGuiConfig.BUTTON_PADDING)
        onClick { spinAnimation(60.degrees) }
        image(clockwiseIcon) {
            scaledHeight = StarhalmaBoardGuiConfig.BUTTON_IMAGE_SIZE
            scaledWidth = StarhalmaBoardGuiConfig.BUTTON_IMAGE_SIZE
        }.centerOn(this)
    }

    private val spinAntiClockwiseButton = uiButton("") {
        scaledHeight = StarhalmaBoardGuiConfig.BUTTON_SIZE
        scaledWidth = StarhalmaBoardGuiConfig.BUTTON_SIZE
        alignTopToTopOf(spinClockwiseButton)
        alignLeftToRightOf(spinClockwiseButton, StarhalmaBoardGuiConfig.BUTTON_PADDING)
        onClick { spinAnimation((-60).degrees) }
        image(antiClockwiseIcon) {
            scaledHeight = StarhalmaBoardGuiConfig.BUTTON_IMAGE_SIZE
            scaledWidth = StarhalmaBoardGuiConfig.BUTTON_IMAGE_SIZE
        }.centerOn(this)
    }

    private val cancelButton = uiButton("X") {
        scaledWidth = StarhalmaBoardGuiConfig.ROUND_TEXT_SIZE
        scaledHeight = StarhalmaBoardGuiConfig.ROUND_TEXT_SIZE
        alignTopToTopOf(this@StarhalmaBoardGui, StarhalmaBoardGuiConfig.BUTTON_PADDING)
        alignRightToRightOf(this@StarhalmaBoardGui, StarhalmaBoardGuiConfig.BUTTON_PADDING)
        onClick {
            val gameWasPaused = paused
            disableButtons()
            pause()
            stage?.uiConfirmBox(S.exitConfirm, 464.0, 128.0, 20.0, 20.0, 16.0, S.yes, S.no) {
                textSize = 32.0
                onConfirm { onExit() }
                onNoConfirm {
                    if (!gameWasPaused) endPause()
                    enableButtons()
                }
            }
        }
    }

    private val _pauseButton = uiButton("||") {
        scaledWidth = StarhalmaBoardGuiConfig.ROUND_TEXT_SIZE
        scaledHeight = StarhalmaBoardGuiConfig.ROUND_TEXT_SIZE
        alignTopToTopOf(this@StarhalmaBoardGui, StarhalmaBoardGuiConfig.BUTTON_PADDING)
        alignRightToLeftOf(cancelButton, StarhalmaBoardGuiConfig.BUTTON_PADDING)
        onClick { togglePause() }
    }

    private val spinButtons = listOf(spinClockwiseButton, spinAntiClockwiseButton)
    private val buttons =  spinButtons + listOf(_pauseButton, cancelButton, goButton)

    private fun disableButtons() { buttons.forEach { it.disable() } }
    private fun enableButtons() { buttons.forEach { it.enable() } }
    private fun disableSpinButtons() { spinButtons.forEach { it.disable() } }
    private fun enableSpinButtons() { spinButtons.forEach { it.enable() } }


    val roundText = uiText(S.gameStarts) {
        textSize = StarhalmaBoardGuiConfig.ROUND_TEXT_SIZE
        textColor = Colors.BLACK
        textAlignment = TextAlignment.RIGHT
        alignTopToTopOf(this@StarhalmaBoardGui, StarhalmaBoardGuiConfig.BUTTON_PADDING)
        alignRightToLeftOf(_pauseButton, StarhalmaBoardGuiConfig.BUTTON_PADDING)
    }

    private val msgBox = roundRect(
        StarhalmaBoardGuiConfig.MSG_BOX_WIDTH,
        StarhalmaBoardGuiConfig.MSG_BOX_HEIGHT,
        StarhalmaBoardGuiConfig.MSG_BOX_RX,
        StarhalmaBoardGuiConfig.MSG_BOX_RX
    ) {
        stroke = Colors.BLACK
        strokeThickness = StarhalmaBoardGuiConfig.MSG_BOX_STROKE_THICKNESS
        alignLeftToLeftOf(this@StarhalmaBoardGui, StarhalmaBoardGuiConfig.BUTTON_PADDING)
        alignBottomToBottomOf(this@StarhalmaBoardGui, StarhalmaBoardGuiConfig.BUTTON_PADDING)
    }

    private val msgText = uiText(S.empty) {
        textSize = StarhalmaBoardGuiConfig.MSG_TEXT_SIZE
        textColor = Colors.BLACK
        alignLeftToLeftOf(msgBox, StarhalmaBoardGuiConfig.MSG_TEXT_PADDING)
        alignTopToTopOf(msgBox, StarhalmaBoardGuiConfig.MSG_TEXT_PADDING)
    }

    override fun hookBeforeMove(player: Player<out Board>) {
        roundText.text = S.round(player.game.round)

        val playerName = playerNames[player.id - 1]
        msgText.text = when (player) {
            is PlayerAI, is PlayerStupidAI -> S.compMoveMsg(playerName)
            is PlayerGui -> S.payerMoveMsg(playerName)
            else -> S.unknown
        }
        msgBox.stroke = playerColors[player.id - 1]
    }

    override suspend fun hookGameEnd(winner: Player<out Board>) {
        val playerName = playerNames[winner.id - 1]
        val playerType = when (winner) {
            is PlayerAI, is PlayerStupidAI -> S.aiType
            is PlayerGui -> S.empty
            else -> S.unknown
        }
        msgText.text = S.winMsg(playerName, playerType)
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
        private lateinit var goButtonIcon: Bitmap
        private lateinit var clockwiseIcon: Bitmap
        private lateinit var antiClockwiseIcon: Bitmap

        private var isInitialized = false
        suspend operator fun invoke(
            numberOfPlayers: Int,
            starhalmaBoard: StarhalmaBoard = StarhalmaBoard(numberOfPlayers),
            playerColors: List<RGBA> = StarhalmaBoardGuiConfig.defaultPlayerColors,
            playerNames: List<String> = StarhalmaBoardGuiConfig.defaultPlayerNames
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
