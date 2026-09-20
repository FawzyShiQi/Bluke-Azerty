package dev.arnv.bluke.ui

/**
 * Converts every Bluke visual keyboard skin to Windows French
 * (Legacy, AZERTY) while preserving the physical HID usage codes.
 *
 * HID sends physical key positions; the host OS applies its keyboard layout.
 * Therefore the key displayed as A intentionally keeps the physical Q HID code,
 * Z keeps W, Q keeps A, W keeps Z, etc.
 */
object AzertyLayout {
    private const val KEY_NONUS_BACKSLASH = 0x64

    fun apply(rows: List<List<KeyLayoutInfo>>): List<List<KeyLayoutInfo>> {
        return rows.map { sourceRow ->
            val row = sourceRow.map { key ->
                val labels = frenchLegacyLegendForKeycode(key.keyCode)
                if (labels == null) {
                    key
                } else {
                    key.copy(
                        legend = labels.first,
                        shiftedLegend = labels.second
                    )
                }
            }.toMutableList()

            // French keyboards use the ISO extra key (< >) to the right of
            // the shortened left Shift. Keep all original HID positions intact.
            val hasBottomAlphaRow = row.any { it.keyCode == KeyboardLayouts.KEY_Z }
            val leftShiftIndex = row.indexOfFirst { it.keyCode == KeyboardLayouts.MOD_LSHIFT }

            if (
                hasBottomAlphaRow &&
                leftShiftIndex >= 0 &&
                row.none { it.keyCode == KEY_NONUS_BACKSLASH }
            ) {
                val leftShift = row[leftShiftIndex]
                if (leftShift.widthRatio >= 2.0f) {
                    val newShiftWidth = leftShift.widthRatio - 1.0f
                    row[leftShiftIndex] = leftShift.copy(widthRatio = newShiftWidth)

                    row.add(
                        leftShiftIndex + 1,
                        KeyLayoutInfo(
                            legend = "<",
                            shiftedLegend = ">",
                            widthRatio = 1.0f,
                            heightRatio = leftShift.heightRatio,
                            x = leftShift.x + newShiftWidth,
                            y = leftShift.y,
                            keyCode = KEY_NONUS_BACKSLASH,
                            category = KeyColorCategory.ALPHA
                        )
                    )
                }
            }

            // If a skin contains two Alt keys, the one on the right is AltGr.
            val altIndexes = row.indices.filter {
                row[it].keyCode == KeyboardLayouts.MOD_LALT
            }
            if (altIndexes.size >= 2) {
                val rightAltIndex = altIndexes.last()
                row[rightAltIndex] = row[rightAltIndex].copy(
                    legend = "AltGr",
                    shiftedLegend = "",
                    keyCode = KeyboardLayouts.MOD_RALT,
                    category = KeyColorCategory.MOD
                )
            }

            row
        }
    }

    /**
     * Visible legends for Windows "French (Legacy, AZERTY)".
     * Pair = unshifted legend to shifted legend.
     */
    private fun frenchLegacyLegendForKeycode(keyCode: Int): Pair<String, String>? {
        return when (keyCode) {
            KeyboardLayouts.KEY_GRAVE -> "²" to ""
            KeyboardLayouts.KEY_1 -> "&" to "1"
            KeyboardLayouts.KEY_2 -> "é" to "2"
            KeyboardLayouts.KEY_3 -> "\"" to "3"
            KeyboardLayouts.KEY_4 -> "'" to "4"
            KeyboardLayouts.KEY_5 -> "(" to "5"
            KeyboardLayouts.KEY_6 -> "-" to "6"
            KeyboardLayouts.KEY_7 -> "è" to "7"
            KeyboardLayouts.KEY_8 -> "_" to "8"
            KeyboardLayouts.KEY_9 -> "ç" to "9"
            KeyboardLayouts.KEY_0 -> "à" to "0"
            KeyboardLayouts.KEY_MINUS -> ")" to "°"
            KeyboardLayouts.KEY_EQUAL -> "=" to "+"

            KeyboardLayouts.KEY_Q -> "a" to ""
            KeyboardLayouts.KEY_W -> "z" to ""
            KeyboardLayouts.KEY_E -> "e" to ""
            KeyboardLayouts.KEY_R -> "r" to ""
            KeyboardLayouts.KEY_T -> "t" to ""
            KeyboardLayouts.KEY_Y -> "y" to ""
            KeyboardLayouts.KEY_U -> "u" to ""
            KeyboardLayouts.KEY_I -> "i" to ""
            KeyboardLayouts.KEY_O -> "o" to ""
            KeyboardLayouts.KEY_P -> "p" to ""
            KeyboardLayouts.KEY_LBRACKET -> "^" to "¨"
            KeyboardLayouts.KEY_RBRACKET -> "$" to "£"
            KeyboardLayouts.KEY_BACKSLASH -> "*" to "µ"

            KeyboardLayouts.KEY_A -> "q" to ""
            KeyboardLayouts.KEY_S -> "s" to ""
            KeyboardLayouts.KEY_D -> "d" to ""
            KeyboardLayouts.KEY_F -> "f" to ""
            KeyboardLayouts.KEY_G -> "g" to ""
            KeyboardLayouts.KEY_H -> "h" to ""
            KeyboardLayouts.KEY_J -> "j" to ""
            KeyboardLayouts.KEY_K -> "k" to ""
            KeyboardLayouts.KEY_L -> "l" to ""
            KeyboardLayouts.KEY_SEMICOLON -> "m" to ""
            KeyboardLayouts.KEY_APOSTROPHE -> "ù" to "%"

            KeyboardLayouts.KEY_Z -> "w" to ""
            KeyboardLayouts.KEY_X -> "x" to ""
            KeyboardLayouts.KEY_C -> "c" to ""
            KeyboardLayouts.KEY_V -> "v" to ""
            KeyboardLayouts.KEY_B -> "b" to ""
            KeyboardLayouts.KEY_N -> "n" to ""
            KeyboardLayouts.KEY_M -> "," to "?"
            KeyboardLayouts.KEY_COMMA -> ";" to "."
            KeyboardLayouts.KEY_PERIOD -> ":" to "/"
            KeyboardLayouts.KEY_SLASH -> "!" to "§"

            else -> null
        }
    }
}
