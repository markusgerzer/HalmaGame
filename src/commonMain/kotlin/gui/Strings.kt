package gui

import com.soywiz.korio.util.i18n.*

var S = when(Language.CURRENT.iso6391) {
    "de" -> DE
    else -> EN
}



sealed interface Strings {
    val nrLabel: String
    val typeLabel: String
    val colorLabel: String
    val playerGui: String
    val playerAI: String
    val playerHashedAI: String
    val playerStupidAI: String
    val colorRed: String
    val colorBlue: String
    val colorGreen: String
    val colorViolet: String
    val colorGrey: String
    val colorBlack: String
    val pause: String
    val waitingForMoveCompletion: String
    val exitConfirm: String
    val gameStarts: String
    val aiType: String
    val yes: String
    val no: String
    val round: (Int) -> String
    val compMoveMsg: (String) -> String
    val payerMoveMsg: (String) -> String
    val winMsg: (String, String) -> String
    val unknown get() = "???"
    val empty get() = ""
}

object EN : Strings {
    override val nrLabel = "Nr."
    override val typeLabel = "Player"
    override val colorLabel = "Color"
    override val playerGui = "Human Player"
    override val playerAI = "AI"
    override val playerHashedAI = "Hashed AI"
    override val playerStupidAI = "Stupid AI"
    override val colorRed = "Red"
    override val colorBlue = "Blue"
    override val colorGreen = "Green"
    override val colorViolet = "Violet"
    override val colorGrey = "Grey"
    override val colorBlack = "Black"
    override val pause = "P A U S E D"
    override val waitingForMoveCompletion = "Rotating Board when move has completed."
    override val exitConfirm = "Exit Game?"
    override val gameStarts = "Game starts"
    override val aiType = "[Computer]"
    override val yes = "Yes"
    override val no = "No"
    override val round = { i: Int -> "Round $i" }
    override val compMoveMsg = { name: String ->
        "$name player\n" +
            "[Computer] makes\n" +
            "his move.\n"
    }
    override val payerMoveMsg = { name: String ->
        "$name player\n" +
            "please make\n" +
            "your move.\n"
    }
    override val winMsg = { name: String, type: String ->
        "$name $type\n" +
            "has won.\n" +
            "Back to Menu\n"
    }
}

object DE : Strings {
    override val nrLabel = "Nr."
    override val typeLabel = "Spieler"
    override val colorLabel = "Farbe"
    override val playerGui = "Spieler"
    override val playerAI = "KI"
    override val playerHashedAI = "Hashed KI"
    override val playerStupidAI = "Stupid KI"
    override val colorRed = "Rot"
    override val colorBlue = "Blau"
    override val colorGreen = "Grün"
    override val colorViolet = "Violet"
    override val colorGrey = "Grau"
    override val colorBlack = "Schwarz"
    override val pause = "P A U S E"
    override val waitingForMoveCompletion = "Rotiere das Brett, wenn der Zug beendet ist."
    override val exitConfirm = "Spiel beenden?"
    override val gameStarts = "Spiel started"
    override val aiType = "[Computer]"
    override val yes = "Ja"
    override val no = "Nein"
    override val round = { i: Int -> "Runde $i" }
    override val compMoveMsg = { name: String ->
        "Spieler $name player\n" +
            "[Computer] macht\n" +
            "seinen Zug.\n"
    }
    override val payerMoveMsg = { name: String ->
        "Spieler $name\n" +
            "bitte mache\n" +
            "deinen Zug.\n"
    }
    override val winMsg = { name: String, type: String ->
        "$name $type\n" +
            "hat gewonnen.\n" +
            "Zurück zum Menü\n"
    }
}



